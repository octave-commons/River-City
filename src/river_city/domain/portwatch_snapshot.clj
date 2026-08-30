;; River City - geopolitical, energy, and AI infrastructure observability
;; Copyright (C) 2026 River City contributors
;; SPDX-License-Identifier: GPL-3.0-or-later

(ns river-city.domain.portwatch-snapshot
  (:import [java.time Instant LocalDate ZoneOffset]
           [java.time.format DateTimeFormatter]))

(def projection-type :river-city/portwatch-snapshot)
(def projection-version 1)
(def default-history-days 180)

(defn epoch-ms->local-date [epoch-ms]
  (-> (Instant/ofEpochMilli epoch-ms)
      (.atZone ZoneOffset/UTC)
      (.toLocalDate)))

(defn local-date->epoch-ms [^LocalDate date]
  (-> date
      (.atStartOfDay ZoneOffset/UTC)
      (.toInstant)
      (.toEpochMilli)))

(defn iso-date [^LocalDate date]
  (.format date DateTimeFormatter/ISO_LOCAL_DATE))

(defn percentile
  "Linear-interpolated percentile over numeric values. Returns nil for no data."
  [values p]
  (when (seq values)
    (let [xs (vec (sort (map double values)))
          position (* (double p) (dec (count xs)))
          lower-index (int (Math/floor position))
          upper-index (int (Math/ceil position))
          fraction (- position lower-index)
          lower-value (nth xs lower-index)
          upper-value (nth xs upper-index)]
      (+ (* (- 1.0 fraction) lower-value)
         (* fraction upper-value)))))

(defn baseline-statistics [values]
  (when (seq values)
    {:baseline-n (count values)
     :baseline-min (double (apply min values))
     :baseline-q25 (percentile values 0.25)
     :baseline-median (percentile values 0.50)
     :baseline-q75 (percentile values 0.75)
     :baseline-max (double (apply max values))}))

(defn- observation->row [observation]
  (let [date (epoch-ms->local-date (:source/date observation))]
    {:date (iso-date date)
     :date-ms (:source/date observation)
     :year (.getYear date)
     :month-day (format "%02d-%02d" (.getMonthValue date) (.getDayOfMonth date))
     :port-id (:port/id observation)
     :port-name (:port/name observation)
     :source-record-id (:source/record-id observation)
     :vessels-container (get-in observation [:vessels :container])
     :vessels-dry-bulk (get-in observation [:vessels :dry-bulk])
     :vessels-general-cargo (get-in observation [:vessels :general-cargo])
     :vessels-roro (get-in observation [:vessels :roro])
     :vessels-tanker (get-in observation [:vessels :tanker])
     :vessels-cargo (get-in observation [:vessels :cargo])
     :vessels-total (get-in observation [:vessels :total])
     :capacity-total (get-in observation [:capacity :total])
     ::date date}))

(defn- latest-row-by-port [rows]
  (->> rows
       (group-by :port-id)
       (map (fn [[port-id port-rows]]
              [port-id (apply max-key :date-ms port-rows)]))
       (into {})))

(defn- baseline-samples [rows current-year]
  (reduce
   (fn [acc row]
     (if (and (< (:year row) current-year)
              (number? (:vessels-total row)))
       (update acc [(:port-id row) (:month-day row)] (fnil conj []) (:vessels-total row))
       acc))
   {}
   rows))

(defn- enrich-row [row samples latest-by-port]
  (let [stats (baseline-statistics
               (get samples [(:port-id row) (:month-day row)]))
        baseline (:baseline-median stats)
        observed (:vessels-total row)
        deviation (when (and (number? observed) (number? baseline))
                    (- (double observed) (double baseline)))
        deviation-percent (when (and deviation (not (zero? (double baseline))))
                            (* 100.0 (/ deviation (double baseline))))
        latest? (= (:date-ms row) (get-in latest-by-port [(:port-id row) :date-ms]))]
    (cond-> (merge (dissoc row ::date :year :month-day) stats)
      true (assoc :latest latest?
                  :baseline-status "provisional-observed-history"
                  :baseline-policy-issue 3)
      deviation (assoc :deviation-absolute deviation)
      deviation-percent (assoc :deviation-percent deviation-percent))))

(defn project
  "Build a repository snapshot directly from normalized PortWatch observations.

   This is a disposable source projection, not a replacement for the Clio event
   ledger. The baseline is descriptive: same calendar day across all prior
   available years, with median and interquartile range."
  [{:keys [generated-at retrieved-at rows history-days]
    :or {history-days default-history-days}}]
  (let [flat-rows (->> rows (map observation->row) (sort-by (juxt :date-ms :port-name)) vec)]
    (when-not (seq flat-rows)
      (throw (ex-info "Cannot project an empty PortWatch observation set"
                      {:river-city/error :empty-portwatch-projection})))
    (let [latest-date ^LocalDate (::date (apply max-key :date-ms flat-rows))
          current-year (.getYear latest-date)
          cutoff (.minusDays latest-date (dec history-days))
          samples (baseline-samples flat-rows current-year)
          latest-by-port (latest-row-by-port flat-rows)
          recent-rows (->> flat-rows
                           (filter #(not (.isBefore ^LocalDate (::date %) cutoff)))
                           (map #(enrich-row % samples latest-by-port))
                           vec)
          latest (->> latest-by-port vals
                      (map #(enrich-row % samples latest-by-port))
                      (sort-by :port-name)
                      vec)
          years (sort (distinct (map :year flat-rows)))]
      {:projection/type projection-type
       :projection/version projection-version
       :generated-at generated-at
       :source/retrieved-at retrieved-at
       :source/id :source/imf-portwatch
       :lineage/mode :direct-source-snapshot
       :lineage/ledger-backed? false
       :lineage/event-host :foresight
       :lineage/event-host-status :pending
       :lineage/issue 2
       :coverage {:from (iso-date (::date (first flat-rows)))
                  :through (iso-date latest-date)
                  :source-rows (count flat-rows)
                  :projection-rows (count recent-rows)
                  :history-days history-days
                  :years years
                  :ports (->> flat-rows (map :port-name) distinct sort vec)}
       :baseline {:type :observed-prior-year-calendar-day
                  :status :provisional
                  :center :median
                  :band [:q25 :q75]
                  :exclusions []
                  :normal-regime? false
                  :policy-issue 3
                  :note "Descriptive prior-year observations; not an approved normal-regime baseline."}
       :latest latest
       :rows recent-rows})))
