;; SPDX-License-Identifier: GPL-3.0-or-later
;; Invoked by the JVM host integration test using its dependency classpath.
(require '[clojure.edn :as edn]
         '[river-city.infra.research :as research]
         '[river-city.shape.research :as shape])
(let [[directory primary-id counterpart-id] *command-line-args*
      rows (fn [value event-id]
             (mapv (fn [day] {:port-id "hormuz" :port-name "Hormuz" :date (str "2026-10-" day)
                              :source-record-id day :vessels-total value :event-ids [event-id]
                              :baseline-median 100 :baseline-q25 90 :baseline-q75 110 :baseline-n 5})
                   (range 10 17)))
      primary (first (shape/portwatch-signals {:rows (rows 20 primary-id)} "2026-10-21"))
      counterpart (first (shape/portwatch-signals {:rows (rows 100 counterpart-id)} "2026-10-21"))
      signal (assoc primary :counterpart (select-keys counterpart [:unit :comparison-key :observations]))
      result (research/run! {:signals [signal] :directory directory :as-of "2026-10-21" :input-refs []
                            :worker (fn [path _]
                                      (let [id (:request-id (edn/read-string (slurp (str path "/request.edn"))))]
                                        {:request-id id :status "null" :evidence [] :production-change? false}))})]
  (prn (select-keys (first (:results result)) [:path :request])))
