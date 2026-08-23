(require '[babashka.process :refer [shell]]
         '[clojure.edn :as edn])

(let [repo (or (first *command-line-args*)
               (throw (ex-info "usage: bb scripts/create_labels.clj owner/repo" {})))
      labels (edn/read-string (slurp "backlog/labels.edn"))]
  (doseq [{:keys [name color description]} labels]
    (shell "gh" "label" "create" name
           "--repo" repo
           "--color" color
           "--description" description
           "--force")))
