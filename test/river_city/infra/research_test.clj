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
