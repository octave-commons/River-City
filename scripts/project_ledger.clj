(require '[river-city.infra.ledger :as ledger]
         '[river-city.law.core :as law]
         '[river-city.law.ledger :as ledger-law])

(defn producer []
  {:git-sha (or (System/getenv "RIVER_CITY_CODE_SHA")
                (System/getenv "GITHUB_SHA")
                "local")
   :workflow (or (System/getenv "GITHUB_WORKFLOW") "local")
   :job (or (System/getenv "GITHUB_JOB") "project-ledger")})

(def events
  (->> (ledger/read-events)
       (filter #(= :observation/portwatch (:event/type %)))
       (sort-by (juxt :observed-at :event/id))
       vec))

(def rows
  (mapv (fn [event]
          (assoc (:data event)
                 :event/id (:event/id event)
                 :observed-at (:observed-at event)))
        events))

(def as-of
  (or (some->> events (map :observed-at) seq (apply max))
      "1970-01-01"))

(def projection
  {:projection/version 1
   :projection/type :maritime/portwatch
   :as-of as-of
   :producer (producer)
   :source-events (mapv :event/id events)
   :data {:rows rows}})

(law/assert-valid! ledger-law/Projection projection)
(println (pr-str {:projection :maritime/portwatch
                  :events (count events)
                  :result (ledger/write-derived! "projections/maritime/latest.edn" projection)}))
