;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.infra.research-test
  (:require [babashka.fs :as fs]
            [babashka.process :as process]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [river-city.domain.research-test :as fixture]
            [river-city.infra.research :as research]
            [river-city.infra.portwatch :as portwatch]
            [river-city.shape.research :as shape]))

(defn node-worker [items]
  (fn [directory _]
    (let [code (str "import {executeResearch} from './scripts/research_worker.mjs';"
                    "import {readFile,writeFile} from 'node:fs/promises';"
                    "const dir=process.argv[1];"
                    "const req=JSON.parse(await readFile(dir+'/request.json','utf8'));"
                    "const result=await executeResearch(req,dir,{fetcher:async()=>new Response(JSON.stringify({message:{items:"
                    (json/generate-string items) "}}))});"
                    "await writeFile(dir+'/worker-result.json',JSON.stringify(result));")
          outcome (process/shell {:out :string :err :string} "node" "--input-type=module" "-e" code (str directory))]
      (json/parse-string (slurp (str (fs/path directory "worker-result.json"))) true))))

(deftest execution-retention-replay-and-causal-handoff
  (let [directory (fs/create-temp-dir {:prefix "river-e2e-"})
        s (update (fixture/signal (repeat 7 20)) :observations
                  #(mapv (fn [x] (assoc x :event-ids ["existing-source-event"])) %))
        options {:signals [s] :as-of "2026-10-21" :input-refs [{:path "projection.edn" :digest "input-digest"}]
                 :directory directory :policy-value fixture/policy
                 :worker (node-worker [{:title ["AIS measurement uncertainty"] :DOI "10.1234/ais" :type "journal-article"}])}
        result (research/run! options)
        dossier (first (:results result))
        before (slurp (str (fs/path (:path dossier) "result.edn")))
        replay (research/run! options)
        draft (edn/read-string (slurp (str (fs/path (:path dossier) "event-draft.edn"))))]
    (is (= :completed (get-in dossier [:result :status])))
    (is (= 2 (count (get-in dossier [:result :evidence]))))
    (is (seq (get-in dossier [:result :tests])))
    (is (= ["existing-source-event"] (:causal/parents draft)))
    (is (= :pending (:admission draft)))
    (is (false? (get-in draft [:draft/data :result :production-change?])))
    (is (empty? (:results replay)))
    (is (= :duplicate (get-in replay [:admission :suppressed 0 :reason])))
    (is (= before (slurp (str (fs/path (:path dossier) "result.edn")))))
    (is (.startsWith (research/report-intro result) "!["))
    (is (re-find #"Does not establish" (research/report-intro result)))
    (is (re-find #"Question:" (research/report-intro result)))
    (is (re-find #"<svg" (research/chart-svg result)))))

(deftest null-failure-and-tamper-never-become-facts
  (doseq [[expected worker]
          [[:null (node-worker [])]
           [:failed (fn [_ _] (throw (ex-info "provider unavailable" {})))]]]
    (let [result (research/run! {:signals [(fixture/signal (repeat 7 20))]
                                :as-of "2026-10-21" :input-refs [] :directory (fs/create-temp-dir)
                                :policy-value fixture/policy :worker worker})]
      (is (= expected (get-in result [:results 0 :result :status])))
      (is (= 1 (count (:history result))))))
  (let [worker (node-worker [{:title ["A lead"] :DOI "10.1234/a"}])
        result (research/run! {:signals [(fixture/signal (repeat 7 20))] :as-of "2026-10-21"
                              :input-refs [] :directory (fs/create-temp-dir) :policy-value fixture/policy
                              :worker (fn [directory p]
                                        (let [r (worker directory p)]
                                          (spit (str (fs/path directory "query-0.json")) "tampered") r))})]
    (is (= :failed (get-in result [:results 0 :result :status])))))

(deftest source-age-is-measured-per-port-not-retrieval-or-global-coverage
  (let [p {:generated-at "2026-10-21T12:00:00Z" :source/retrieved-at "2026-10-21T11:59:00Z"
           :rows [{:port-id "a" :port-name "A" :date "2026-10-10" :vessels-total 5 :source-record-id 1}
                  {:port-id "b" :port-name "B" :date "2026-10-20" :vessels-total 6 :source-record-id 2}]}
        signals (shape/portwatch-signals p "2026-10-21")]
    (is (= [11 1] (mapv :source-age-days signals)))
    (is (every? #(false? (get-in % [:comparison :approved?])) signals))))

(deftest daily-report-composition-actually-uses-research-lead
  (let [p {:generated-at "2026-10-21T12:00:00Z" :coverage {:through "2026-10-20"} :latest []}
        r (research/run! {:signals [(fixture/signal (repeat 7 20))] :as-of "2026-10-21"
                          :input-refs [] :directory (fs/create-temp-dir) :policy-value fixture/policy
                          :worker (node-worker [])})
        report (#'portwatch/report-markdown p r)]
    (is (< (str/index-of report "![") (str/index-of report "## Status")))
    (is (re-find #"persistently displaced" report))
    (is (re-find #"\*\*null\*\*" report))))
(deftest operational-reservations-and-policy-fail-closed
  (let [directory (fs/create-temp-dir)]
    (fs/create-dir (fs/path directory "research-orphan"))
    (is (thrown-with-msg? Exception #"Incomplete research reservation" (research/history directory))))
  (is (thrown-with-msg? Exception #"Invalid research policy"
                        (research/run! {:signals [] :as-of "2026-10-21" :input-refs []
                                        :directory (fs/create-temp-dir)
                                        :policy-value (assoc fixture/policy :queries-per-run 999)}))))
(deftest chart-has-numeric-coordinates-and-breaks-missing-daily-coverage
  (let [s (assoc (fixture/signal [20 21 22 23 24]) :cadence-days 1)
        s (update s :observations #(mapv (fn [i row] (assoc row :day (nth [10 11 12 14 15] i))) (range) %))
        a (river-city.domain.research/assess fixture/policy s)
        svg (research/chart-svg {:lead a :assessments [a]})]
    (is (contains? (:flags a) :insufficient))
    (is (re-find #"M60\.0," svg))
    (is (not (re-find #"[0-9]+/[0-9]+" svg)))
    (is (re-find #"M556\.0," svg))))

(deftest retained-reservations-require-accountable-fields
  (doseq [change [#(dissoc % :day) #(assoc % :day "today") #(dissoc % :id)
                  #(dissoc % :request-id) #(dissoc % :policy)]]
    (let [dir (fs/create-temp-dir)
          r (research/run! {:signals [(fixture/signal (repeat 7 20))] :as-of "2026-10-21"
                            :input-refs [] :directory dir :policy-value fixture/policy :worker (node-worker [])})
          file (fs/path (get-in r [:results 0 :path]) "request.edn")]
      (spit (str file) (pr-str (change (edn/read-string (slurp (str file))))))
      (is (thrown-with-msg? Exception #"Invalid research reservation" (research/history dir))))))

(deftest retained-completed-results-are-revalidated-before-replay
  (doseq [change [#(assoc % :request-id "different") #(assoc % :production-change? true)
                  #(assoc % :status :invented) #(dissoc % :tests)]]
    (let [dir (fs/create-temp-dir)
          r (research/run! {:signals [(fixture/signal (repeat 7 20))] :as-of "2026-10-21"
                            :input-refs [] :directory dir :policy-value fixture/policy :worker (node-worker [])})
          file (fs/path (get-in r [:results 0 :path]) "result.edn")]
      (spit (str file) (pr-str (change (edn/read-string (slurp (str file))))))
      (is (thrown-with-msg? Exception #"Invalid or unretained research result" (research/history dir)))))
  (let [dir (fs/create-temp-dir)
        r (research/run! {:signals [(fixture/signal (repeat 7 20))] :as-of "2026-10-21"
                          :input-refs [] :directory dir :policy-value fixture/policy :worker (node-worker [])})]
    (spit (str (fs/path (get-in r [:results 0 :path]) "query-0.json")) "changed after completion")
    (is (thrown-with-msg? Exception #"Invalid or unretained research result" (research/history dir)))))

(deftest counterpart-observations-are-research-causes
  (let [s (assoc (fixture/signal (repeat 7 20)) :comparison-key "same"
                 :counterpart {:unit "vessels/day" :comparison-key "same"
                               :observations (mapv #(assoc % :event-ids ["counterpart-source"])
                                                   (:observations (fixture/signal (repeat 7 100))))})
        r (research/run! {:signals [s] :as-of "2026-10-21" :input-refs [] :directory (fs/create-temp-dir)
                          :policy-value fixture/policy :worker (node-worker [])})]
    (is (contains? (get-in r [:results 0 :request :flags]) :divergent))
    (is (= ["counterpart-source"] (get-in r [:results 0 :request :causal/parents])))))

(deftest replay-links-current-dossiers-before-bounding-history
  (let [history (mapv (fn [n] {:id (str n) :request-id (str "request-" n) :day n
                               :path (str "data/research/" n) :status :null}) (range 10))
        report (research/report-intro {:lead (river-city.domain.research/lead []) :results [] :history history
                                      :admission {:suppressed [{:request-id "request-0" :reason :duplicate}]}})]
    (is (str/includes? report "../../data/research/0/result.edn"))))

(deftest stopped-port-remains-a-canonical-stale-signal
  (let [p {:rows [{:port-id "b" :port-name "B" :date "2026-10-20" :vessels-total 20 :source-record-id 2}]
           :latest [{:port-id "a" :port-name "A" :date "2025-01-01" :vessels-total 10 :source-record-id 1}
                    {:port-id "b" :port-name "B" :date "2026-10-20" :vessels-total 20 :source-record-id 2}]}
        signals (shape/portwatch-signals p "2026-10-21")
        a (first (filter #(= "portwatch/a" (:id %)) signals))]
    (is (some? a))
    (when a
      (let [assessment (river-city.domain.research/assess fixture/policy a)]
        (is (contains? (:flags assessment) :stale))
        (is (contains? (:flags assessment) :insufficient))
        (is (:research? assessment))))))

(deftest worker-process-has-an-enforced-deadline
  (let [dir (fs/create-temp-dir)
        p (assoc fixture/policy :queries-per-run 1 :timeout-ms 1)
        start (System/nanoTime)]
    (is (thrown-with-msg? Exception #"Research worker timed out"
          (research/execute-worker! dir p ["node" "-e" "setInterval(()=>{},1000)"])))
    (is (< (/ (- (System/nanoTime) start) 1e6) 8000))))

(deftest contended-budget-lock-keeps-its-diagnostic
  (let [dir (fs/create-temp-dir)
        path (str (fs/path dir ".budget.lock"))
        code (str "(import '[java.nio.channels FileChannel] '[java.nio.file Paths StandardOpenOption])"
                  "(with-open [c (FileChannel/open (Paths/get " (pr-str path) " (make-array String 0))"
                  " (into-array StandardOpenOption [StandardOpenOption/CREATE StandardOpenOption/WRITE]))]"
                  " (let [l (.lock c)] (println :locked) (flush) (read-line)))")
        child (process/process {:out :stream :err :inherit :in :stream}
                               (or (System/getenv "RIVER_CITY_BB") "bb") "--config" "/dev/null" "-e" code)]
    (try
      (is (= ":locked" (.readLine (java.io.BufferedReader. (java.io.InputStreamReader. (:out child))))))
      (is (thrown-with-msg? Exception #"Research budget is already in use"
            (research/run! {:signals [] :as-of "2026-10-21" :input-refs [] :directory dir
                            :policy-value fixture/policy})))
      (finally (process/destroy-tree child)))))

(deftest worker-process-preserves-success-and-failure-handling
  (let [dir (fs/create-temp-dir)
        write-code "require('fs').writeFileSync(process.argv[1]+'/worker-result.json',JSON.stringify({status:'null'}))"]
    (is (= "null" (:status (research/execute-worker! dir fixture/policy ["node" "-e" write-code (str dir)]))))
    (is (thrown-with-msg? Exception #"Research worker failed"
                          (research/execute-worker! dir fixture/policy ["node" "-e" "process.exit(2)"])))))
