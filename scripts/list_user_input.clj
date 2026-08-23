(require '[clojure.edn :as edn]
         '[clojure.string :as str])

(doseq [{:keys [kind label status for action]}
        (edn/read-string (slurp "backlog/user-input.edn"))]
  (println (format "[%s] %s (%s)" (name status) action label))
  (println "  kind:" (name kind))
  (println "  issues:" (str/join "; " for)))
