;; River City - geopolitical, energy, and AI infrastructure observability
;; Copyright (C) 2026 River City contributors
;; SPDX-License-Identifier: GPL-3.0-or-later

(ns river-city.infra.portwatch
  (:require [babashka.fs :as fs]
            [babashka.http-client :as http]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [river-city.domain.portwatch-snapshot :as snapshot]
            [river-city.shape.portwatch :as shape])
  (:import [java.net URLEncoder]
           [java.nio.charset StandardCharsets]
           [java.time Instant ZoneOffset]
           [java.time.format DateTimeFormatter]))

(def query-where "portname LIKE '%Hormuz%' OR portname LIKE '%Mandeb%'")
(def page-size 1000)
(def raw-latest "data/raw/portwatch/latest.json")
(def normalized-latest-edn "data/normalized/portwatch/latest.edn")
(def normalized-latest-json "data/normalized/portwatch/latest.json")
(def projection-latest-edn "projections/portwatch/latest.edn")
(def projection-latest-json "projections/portwatch/latest.json")
(def chart-data-path "charts/portwatch/data/latest.json")
(def map-points-path "charts/portwatch/data/map-points.json")
(def map-geojson-path "maps/portwatch/latest.geojson")
(def report-latest-path "reports/daily/latest.md")
(def manifest-edn-path "projections/manifest.edn")
(def manifest-json-path "projections/manifest.json")

(defn utc-now []
  (.format DateTimeFormatter/ISO_INSTANT (Instant/now)))

(defn- ensure-parent! [path]
  (when-let [parent (fs/parent path)]
    (fs/create-dirs parent)))

(defn write-edn! [path value]
  (ensure-parent! path)
  (binding [*print-length* nil *print-level* nil]
    (spit path (str (pr-str value) "\n")))
  path)

(defn- json-key [value]
  (if (keyword? value)
    (if-let [ns (namespace value)]
      (str ns "/" (name value))
      (name value))
    (str value)))

(defn- json-safe [value]
  (cond
    (map? value)
    (into (sorted-map)
          (map (fn [[key item]]
                 [(json-key key) (json-safe item)]))
          value)

    (set? value) (mapv json-safe (sort-by str value))
    (sequential? value) (mapv json-safe value)

    (keyword? value)
    (if-let [ns (namespace value)]
      (str ns "/" (name value))
      (name value))

    :else value))

(defn write-json! [path value]
  (ensure-parent! path)
  (spit path (str (json/generate-string (json-safe value) {:pretty true}) "\n"))
  path)

(defn read-edn-file [path]
  (edn/read-string (slurp path)))

(defn read-json-file [path]
  (json/parse-string (slurp path) true))

(defn- url-encode [value]
  (URLEncoder/encode (str value) (.name StandardCharsets/UTF_8)))

(defn- query-url [offset]
  (let [params {:where query-where
                :outFields (str/join "," shape/out-fields)
                :returnGeometry "false"
                :orderByFields "date ASC,portid ASC,ObjectId ASC"
                :resultOffset offset
                :resultRecordCount page-size
                :f "json"}]
    (str shape/endpoint
         "?"
         (->> params
              (sort-by (comp name key))
              (map (fn [[key value]]
                     (str (url-encode (name key)) "=" (url-encode value))))
              (str/join "&")))))

(defn- request-json! [url]
  (loop [attempt 1]
    (let [{:keys [status body] :as response}
          (http/get url {:headers {"accept" "application/json"
                                   "user-agent" "River-City/1.0 (+https://github.com/octave-commons/River-City)"}
                         :connect-timeout 30000
                         :timeout 90000
                         :as :string
                         :throw false})]
      (if (= status 200)
        (let [payload (json/parse-string body true)]
          (when-let [error (:error payload)]
            (throw (ex-info "PortWatch returned an ArcGIS error"
                            {:river-city/error :portwatch-upstream-error
                             :url url
                             :upstream error})))
          payload)
        (if (< attempt 3)
          (do
            (Thread/sleep (* attempt 1500))
            (recur (inc attempt)))
          (throw (ex-info "PortWatch request failed"
                          {:river-city/error :portwatch-http-error
                           :url url
                           :status status
                           :body body
                           :response (dissoc response :body)})))))))

(defn fetch-all! []
  (loop [offset 0 page-count 0 features []]
    (let [payload (request-json! (query-url offset))
          page (vec (:features payload))
          accumulated (into features page)
          more? (or (:exceededTransferLimit payload)
                    (= page-size (count page)))]
      (if (and (seq page) more?)
        (recur (+ offset page-size) (inc page-count) accumulated)
        {:pages (inc page-count) :features accumulated}))))

(defn ingest! []
  (let [retrieved-at (utc-now)
        {:keys [pages features]} (fetch-all!)
        raw {:retrieved-at retrieved-at
             :source/id shape/source-id
             :source/endpoint shape/endpoint
             :source/query {:where query-where
                            :out-fields shape/out-fields
                            :page-size page-size}
             :page-count pages
             :feature-count (count features)
             :features features}]
    (write-json! raw-latest raw)
    raw))

(defn- dedupe-by-source-record [observations]
  (->> observations
       (reduce (fn [acc observation]
                 (assoc acc (:source/record-id observation) observation))
               {})
       vals
       (sort-by (juxt :source/date :port/name :source/record-id))
       vec))

(defn normalize! []
  (let [raw (read-json-file raw-latest)
        observations
        (->> (:features raw)
             (map :attributes)
             (map shape/normalize-attributes)
             (filter #(shape/target-chokepoint? (:port/name %)))
             (map shape/assert-observation!)
             dedupe-by-source-record)
        normalized {:generated-at (utc-now)
                    :source/retrieved-at (:retrieved-at raw)
                    :source/id shape/source-id
                    :source/feature-count (:feature-count raw)
                    :row-count (count observations)
                    :rows observations}]
    (when-not (seq observations)
      (throw (ex-info "PortWatch normalization produced no target chokepoint rows"
                      {:river-city/error :empty-portwatch-normalization
                       :feature-count (:feature-count raw)})))
    (write-edn! normalized-latest-edn normalized)
    (write-json! normalized-latest-json normalized)
    normalized))

(defn project! []
  (let [normalized (read-edn-file normalized-latest-edn)
        projection (snapshot/project
                    {:generated-at (utc-now)
                     :retrieved-at (:source/retrieved-at normalized)
                     :rows (:rows normalized)})
        date (get-in projection [:coverage :through])
        dated-edn (str "projections/portwatch/" date ".edn")
        dated-json (str "projections/portwatch/" date ".json")]
    (write-edn! projection-latest-edn projection)
    (write-json! projection-latest-json projection)
    (write-edn! dated-edn projection)
    (write-json! dated-json projection)
    projection))

(defn- load-chokepoint-anchors []
  (-> "river_city/chokepoints.edn" io/resource slurp edn/read-string :chokepoints))

(defn- anchor-for [anchors port-name]
  (let [candidate (str/lower-case port-name)]
    (some (fn [{:keys [matches] :as anchor}]
            (when (some #(str/includes? candidate (str/lower-case %)) matches)
              anchor))
          anchors)))

(defn- latest-map-points [projection]
  (let [anchors (load-chokepoint-anchors)]
    (->> (:latest projection)
         (keep (fn [row]
                 (when-let [{:keys [id longitude latitude geometry-note]}
                            (anchor-for anchors (:port-name row))]
                   (assoc row
                          :chokepoint (name id)
                          :longitude longitude
                          :latitude latitude
                          :geometry-note geometry-note))))
         vec)))

(defn- geojson [points generated-at]
  {:type "FeatureCollection"
   :river-city/generated-at generated-at
   :river-city/geometry-status "approximate-reference-anchors"
   :features
   (mapv (fn [point]
           {:type "Feature"
            :geometry {:type "Point"
                       :coordinates [(:longitude point) (:latitude point)]}
            :properties (dissoc point :longitude :latitude)})
         points)})

(defn- passage-chart-spec []
  {"$schema" "https://vega.github.io/schema/vega-lite/v5.json"
   :title "Visible chokepoint passages vs observed prior-year calendar-day history"
   :description "The shaded IQR and median are provisional descriptive history, not an approved normal-regime baseline."
   :data {:url "data/latest.json" :format {:type "json" :property "rows"}}
   :facet {:row {:field "port-name" :type "nominal" :header {:title nil :labelAngle 0}}}
   :spec
   {:width 760
    :height 190
    :layer
    [{:mark {:type "area" :opacity 0.18}
      :encoding {:x {:field "date" :type "temporal" :title nil}
                 :y {:field "baseline-q25" :type "quantitative" :title "Visible vessels / day"}
                 :y2 {:field "baseline-q75"}}}
     {:mark {:type "line" :strokeDash [5 4]}
      :encoding {:x {:field "date" :type "temporal"}
                 :y {:field "baseline-median" :type "quantitative"}
                 :tooltip [{:field "date" :type "temporal"}
                           {:field "baseline-median" :type "quantitative"}
                           {:field "baseline-n" :type "quantitative"}]}}
     {:mark {:type "line" :point false}
      :encoding {:x {:field "date" :type "temporal"}
                 :y {:field "vessels-total" :type "quantitative"}
                 :tooltip [{:field "date" :type "temporal"}
                           {:field "port-name" :type "nominal"}
                           {:field "vessels-total" :type "quantitative"}
                           {:field "baseline-median" :type "quantitative"}
                           {:field "deviation-percent" :type "quantitative" :format ".1f"}]}}
     {:transform [{:filter "datum.latest === true"}]
      :mark {:type "point" :filled true :size 110}
      :encoding {:x {:field "date" :type "temporal"}
                 :y {:field "vessels-total" :type "quantitative"}}}]}})

(defn- deviation-chart-spec []
  {"$schema" "https://vega.github.io/schema/vega-lite/v5.json"
   :title "Visible passage deviation from observed prior-year median"
   :description "Negative values indicate traffic below the same calendar-day median across prior available years."
   :data {:url "data/latest.json" :format {:type "json" :property "rows"}}
   :transform [{:filter "isValid(datum['deviation-percent'])"}]
   :facet {:row {:field "port-name" :type "nominal" :header {:title nil :labelAngle 0}}}
   :spec
   {:width 760
    :height 150
    :layer
    [{:mark {:type "rule"}
      :encoding {:y {:datum 0}}}
     {:mark {:type "line"}
      :encoding {:x {:field "date" :type "temporal" :title nil}
                 :y {:field "deviation-percent" :type "quantitative" :title "% vs prior-year median"}
                 :tooltip [{:field "date" :type "temporal"}
                           {:field "vessels-total" :type "quantitative"}
                           {:field "baseline-median" :type "quantitative"}
                           {:field "deviation-percent" :type "quantitative" :format ".1f"}]}}]}})

(defn- cargo-mix-chart-spec []
  {"$schema" "https://vega.github.io/schema/vega-lite/v5.json"
   :title "Visible vessel mix by chokepoint"
   :data {:url "data/latest.json" :format {:type "json" :property "rows"}}
   :transform [{:fold ["vessels-total" "vessels-cargo" "vessels-tanker"]
                :as ["series" "value"]}]
   :facet {:row {:field "port-name" :type "nominal" :header {:title nil :labelAngle 0}}}
   :spec
   {:width 760
    :height 170
    :mark {:type "line"}
    :encoding {:x {:field "date" :type "temporal" :title nil}
               :y {:field "value" :type "quantitative" :title "Visible vessels / day"}
               :color {:field "series" :type "nominal"}
               :tooltip [{:field "date" :type "temporal"}
                         {:field "series" :type "nominal"}
                         {:field "value" :type "quantitative"}]}}})

(defn- map-chart-spec []
  {"$schema" "https://vega.github.io/schema/vega-lite/v5.json"
   :title "Latest visible chokepoint state"
   :width 820
   :height 430
   :projection {:type "equalEarth"}
   :layer
   [{:data {:sphere true}
     :mark {:type "geoshape" :fill "white" :stroke "lightgray"}}
    {:data {:url "https://vega.github.io/vega-datasets/data/world-110m.json"
            :format {:type "topojson" :feature "countries"}}
     :mark {:type "geoshape" :fill "#eeeeee" :stroke "white"}}
    {:data {:url "data/map-points.json" :format {:type "json" :property "rows"}}
     :mark {:type "circle" :filled true :opacity 0.85}
     :encoding {:longitude {:field "longitude" :type "quantitative"}
                :latitude {:field "latitude" :type "quantitative"}
                :size {:field "vessels-total" :type "quantitative"
                       :legend {:title "Visible vessels"}}
                :tooltip [{:field "port-name" :type "nominal"}
                          {:field "date" :type "temporal"}
                          {:field "vessels-total" :type "quantitative"}
                          {:field "baseline-median" :type "quantitative"}
                          {:field "deviation-percent" :type "quantitative" :format ".1f"}
                          {:field "geometry-note" :type "nominal"}]}}
    {:data {:url "data/map-points.json" :format {:type "json" :property "rows"}}
     :mark {:type "text" :dy -14 :fontSize 12}
     :encoding {:longitude {:field "longitude" :type "quantitative"}
                :latitude {:field "latitude" :type "quantitative"}
                :text {:field "port-name" :type "nominal"}}}]})

(defn- format-number [value]
  (if (number? value) (format "%.1f" (double value)) "—"))

(defn- report-markdown [projection]
  (let [generated-at (:generated-at projection)
        through (get-in projection [:coverage :through])
        table-lines
        (map (fn [row]
               (str "| " (:port-name row)
                    " | " (:date row)
                    " | " (or (:vessels-total row) "—")
                    " | " (format-number (:baseline-median row))
                    " | " (format-number (:deviation-percent row)) "%"
                    " | " (or (:baseline-n row) 0) " |"))
             (:latest projection))]
    (str "# River City Daily: PortWatch\n\n"
         "Generated: `" generated-at "`  \n"
         "Source coverage through: `" through "`\n\n"
         "## Status\n\n"
         "| Chokepoint | Date | Visible vessels | Prior-year median | Deviation | Baseline n |\n"
         "|---|---:|---:|---:|---:|---:|\n"
         (str/join "\n" table-lines)
         "\n\n"
         "## Stable artifacts\n\n"
         "- Projection: [`projections/portwatch/latest.edn`](../../projections/portwatch/latest.edn)\n"
         "- JSON projection: [`projections/portwatch/latest.json`](../../projections/portwatch/latest.json)\n"
         "- Passage chart: [`charts/portwatch/passage-vs-history.vl.json`](../../charts/portwatch/passage-vs-history.vl.json)\n"
         "- Deviation chart: [`charts/portwatch/deviation-from-history.vl.json`](../../charts/portwatch/deviation-from-history.vl.json)\n"
         "- Vessel mix chart: [`charts/portwatch/cargo-mix.vl.json`](../../charts/portwatch/cargo-mix.vl.json)\n"
         "- Map spec: [`charts/portwatch/map.vl.json`](../../charts/portwatch/map.vl.json)\n"
         "- GeoJSON: [`maps/portwatch/latest.geojson`](../../maps/portwatch/latest.geojson)\n\n"
         "## Interpretation constraints\n\n"
         "- The baseline is the same calendar day across all prior available years, shown as median and IQR. It is descriptive and provisional, not an approved normal-regime policy. See issue #3.\n"
         "- This repository snapshot is directly derived from the upstream source. Clio/Foresight event hosting remains the intended canonical ledger path; the manifest marks this snapshot as not yet ledger-backed.\n"
         "- Missing or dark traffic is not converted into zero. Source revisions should be retained by the eventual Clio history.\n")))

(defn render! []
  (let [projection (read-edn-file projection-latest-edn)
        generated-at (:generated-at projection)
        date (get-in projection [:coverage :through])
        points (latest-map-points projection)
        dated-report (str "reports/daily/" date ".md")
        manifest
        {:river-city/state-version 1
         :generated-at generated-at
         :latest-successful-projection
         {:portwatch {:edn projection-latest-edn
                      :json projection-latest-json
                      :source-updated-through date
                      :ledger-backed? false
                      :lineage-mode :direct-source-snapshot}}
         :artifacts
         {:chart-data chart-data-path
          :charts [{:id :chart/portwatch-passage-vs-history
                    :spec "charts/portwatch/passage-vs-history.vl.json"}
                   {:id :chart/portwatch-deviation
                    :spec "charts/portwatch/deviation-from-history.vl.json"}
                   {:id :chart/portwatch-cargo-mix
                    :spec "charts/portwatch/cargo-mix.vl.json"}
                   {:id :chart/portwatch-map
                    :spec "charts/portwatch/map.vl.json"}]
          :maps [{:id :map/portwatch-latest
                  :geojson map-geojson-path
                  :geometry-status :approximate-reference-anchors}]
          :reports [{:id :report/daily-latest :path report-latest-path}
                    {:id :report/daily-dated :path dated-report}]}
         :read-order [manifest-edn-path projection-latest-edn chart-data-path
                      map-geojson-path report-latest-path]
         :missing-series
         [{:series :baseline/approved-normal-regime
           :status :blocked
           :issue 3
           :reason "Observed-history band exists; disruption-exclusion policy is not approved."}
          {:series :ledger/clio-hosted-portwatch
           :status :pending
           :issue 2
           :reason "Direct source snapshot is operational; Foresight-hosted Clio accumulation remains pending."}]}]
    (write-json! chart-data-path {:generated-at generated-at
                                  :baseline (:baseline projection)
                                  :rows (:rows projection)})
    (write-json! map-points-path {:generated-at generated-at :rows points})
    (write-json! map-geojson-path (geojson points generated-at))
    (write-json! "charts/portwatch/passage-vs-history.vl.json" (passage-chart-spec))
    (write-json! "charts/portwatch/deviation-from-history.vl.json" (deviation-chart-spec))
    (write-json! "charts/portwatch/cargo-mix.vl.json" (cargo-mix-chart-spec))
    (write-json! "charts/portwatch/map.vl.json" (map-chart-spec))
    (ensure-parent! report-latest-path)
    (let [report (report-markdown projection)]
      (spit report-latest-path report)
      (spit dated-report report))
    (write-edn! manifest-edn-path manifest)
    (write-json! manifest-json-path manifest)
    manifest))

(defn daily! []
  (ingest!)
  (normalize!)
  (project!)
  (render!))
