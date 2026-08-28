(require '[babashka.http-client :as http]
         '[cheshire.core :as json]
         '[clojure.string :as str]
         '[river-city.infra.ledger :as ledger]
         '[river-city.law.core :as law]
         '[river-city.law.ledger :as ledger-law])

(def endpoint
  "https://services9.arcgis.com/weJ1QsnbMYJlCHdG/arcgis/rest/services/Daily_Chokepoints_Data/FeatureServer/0/query")

(def out-fields
  "ObjectId,date,portid,portname,n_container,n_dry_bulk,n_general_cargo,n_roro,n_tanker,n_cargo,n_total,capacity_container,capacity_dry_bulk,capacity_general_cargo,capacity_roro,capacity_tanker,capacity_cargo,capacity")

(defn target-chokepoint? [portname]
  (boolean (and portname (re-find #"(?i)(hormuz|mandeb)" portname))))

(defn producer []
  {:git-sha (or (System/getenv "GITHUB_SHA") "local")
   :workflow (or (System/getenv "GITHUB_WORKFLOW") "local")
   :job (or (System/getenv "GITHUB_JOB") "collect-portwatch")})

(defn normalize [attrs]
  (into (sorted-map)
        (for [[k v] attrs]
          [(keyword k) v])))

(def response
  (http/get endpoint
            {:query-params {:where "1=1"
                            :outFields out-fields
                            :orderByFields "date DESC"
                            :resultRecordCount 1000
                            :returnGeometry "false"
                            :f "json"}
             :throw false}))

(when-not (= 200 (:status response))
  (throw (ex-info "PortWatch request failed"
                  {:status (:status response)
                   :body (:body response)})))

(def payload (json/parse-string (:body response)))

(when-let [error (get payload "error")]
  (throw (ex-info "PortWatch API returned an error" error)))

(def features
  (->> (get payload "features")
       (map #(get % "attributes"))
       (filter #(target-chokepoint? (get % "portname")))))

(def results
  (for [attrs features
        :let [data (normalize attrs)
              source-record-id (:ObjectId data)
              id (ledger/event-id :source/imf-portwatch source-record-id data)
              event {:ledger/version 1
                     :event/id id
                     :event/type :observation/portwatch
                     :source/id :source/imf-portwatch
                     :source/record-id source-record-id
                     :observed-at (str (:date data))
                     :ingested-at (str (java.time.Instant/now))
                     :producer (producer)
                     :data data}]]
    (do
      (law/assert-valid! ledger-law/LedgerEvent event)
      {:event event
       :result (ledger/write-once! (ledger/event-path event) event)})))

(let [written (count (filter #(= :written (:result %)) results))
      unchanged (count (filter #(= :unchanged (:result %)) results))]
  (println (pr-str {:collector :portwatch
                    :seen (count results)
                    :written written
                    :unchanged unchanged})))
