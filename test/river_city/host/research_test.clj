;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.host.research-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [clio.infra.runtime :as runtime]
            [clio.infra.ledger :as ledger]
            [clio.law.schema :as schema]
            [river-city.infra.research-host :as host])
  (:import [java.nio.file Files]
           [java.nio.file.attribute FileAttribute]))

(deftest shared-clio-admission-replay-and-missing-parent
  (let [dir (str (Files/createTempDirectory "river-clio-" (make-array FileAttribute 0)))
        schemas (str (io/file dir "schemas"))
        parent-ledger (str (io/file dir "observations.edn"))
        research-ledger (str (io/file dir "research.edn"))
        rt (runtime/open schemas {:test/observed (schema/event-schema :test/observed [:map [:value :int]])})
        _ (ledger/create-ledger! parent-ledger)
        parent (:event (runtime/append! rt parent-ledger :test/observed
                       {:event/stream "source:a" :event/seq 1 :event/actor "test"
                        :event/subject "source:a" :event/data {:value 20}}))
        request {:request-id "research-test" :causal/parents [(:event/id parent)]
                 :input-refs [{:path "source:a"}]}
        result {:request-id "research-test" :status :null :production-change? false
                :evidence [] :proposals [] :countermodels [] :tests []}
        options {:schema-directory schemas :ledger-path research-ledger :parent-ledgers [parent-ledger]
                 :dossier-path dir :actor "river-city-test"}]
    (spit (io/file dir "request.edn") (pr-str request))
    (spit (io/file dir "result.edn") (pr-str result))
    (let [first-result (host/admit! options)
          replay (host/admit! options)
          rt (runtime/open schemas host/catalog)
          history (runtime/canonicalize-files rt [research-ledger parent-ledger])]
      (is (= :appended (:append/result first-result)))
      (is (= :already-present (:append/result replay)))
      (is (= 2 (count (:canonical/events history))))
      (is (= [(:event/id parent)] (get-in first-result [:event :event/causes])))
      (is (= (:event first-result) (:event replay)))
      (is (thrown-with-msg? Exception #"parents absent" (host/admit! (assoc options :parent-ledgers []))))
      (spit (io/file dir "result.edn") (pr-str (assoc result :status :completed)))
      (is (thrown-with-msg? Exception #"differs" (host/admit! options))))))

(defn -main [& _]
  (let [{:keys [fail error]} (clojure.test/run-tests 'river-city.host.research-test)]
    (shutdown-agents)
    (System/exit (+ fail error))))
