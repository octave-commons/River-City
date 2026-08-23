(require '[babashka.process :refer [shell]]
         '[clojure.edn :as edn]
         '[clojure.string :as str])

(defn existing-issues [repo]
  (->> (:out (shell {:out :string}
                    "gh" "issue" "list"
                    "--repo" repo
                    "--state" "all"
                    "--limit" "500"
                    "--json" "number,title"
                    "--jq" ".[] | [.number, .title] | @tsv"))
       str/split-lines
       (keep (fn [line]
               (let [[n title] (str/split line #"\t" 2)]
                 (when (and n title) [title (parse-long n)]))))
       (into {})))

(let [repo (or (first *command-line-args*)
               (throw (ex-info "usage: bb scripts/create_issues.clj owner/repo" {})))
      issues (edn/read-string (slurp "backlog/issues.edn"))
      existing (existing-issues repo)]
  (doseq [{:keys [title labels body]} issues]
    (if-let [number (get existing title)]
      (do
        (apply shell (concat ["gh" "issue" "edit" (str number)
                              "--repo" repo
                              "--body" body]
                             (mapcat (fn [label] ["--add-label" label]) labels)))
        (println "synced issue" number title))
      (do
        (apply shell (concat ["gh" "issue" "create" "--repo" repo
                              "--title" title "--body" body]
                             (mapcat (fn [label] ["--label" label]) labels)))
        (println "created issue" title)))))
