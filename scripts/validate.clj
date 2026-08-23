(require '[clojure.edn :as edn]
         '[river-city.law.core :as law])
(let [cfg (edn/read-string (slurp "resources/river_city/config.edn"))]
  (law/assert-valid! law/Config cfg)
  (println "River City config satisfies law."))
