(require '[clojure.edn :as edn]
         '[clojure.string :as str])

(defn prefixed [prefix labels]
  (filter #(str/starts-with? % prefix) labels))

(doseq [[idx {:keys [title labels body]}]
        (map-indexed vector (edn/read-string (slurp "backlog/issues.edn")))]
  (let [lane (first (prefixed "lane:" labels))
        inputs (vec (prefixed "input:" labels))
        topics (remove #(or (str/starts-with? % "lane:")
                            (str/starts-with? % "input:")) labels)]
    (println (format "%02d. %s" (inc idx) title))
    (println "    lane:" (or lane "UNASSIGNED"))
    (println "    input:" (if (seq inputs) (str/join ", " inputs) "none"))
    (println "    tags:" (str/join ", " topics))
    (println "   " body)))
