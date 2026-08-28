(ns river-city.infra.ledger-test
  (:require [clojure.edn :as edn]
            [clojure.test :refer [deftest is testing]]
            [river-city.infra.ledger :as ledger]))

(deftest event-id-is-content-addressed
  (testing "same source record and normalized payload has stable identity"
    (is (= (ledger/event-id :source/test 7 (sorted-map :value 42))
           (ledger/event-id :source/test 7 (sorted-map :value 42)))))
  (testing "a corrected payload creates a new identity"
    (is (not= (ledger/event-id :source/test 7 (sorted-map :value 42))
              (ledger/event-id :source/test 7 (sorted-map :value 43))))))

(deftest immutable-write-is-idempotent
  (let [dir (.toFile (java.nio.file.Files/createTempDirectory "river-city-ledger" (make-array java.nio.file.attribute.FileAttribute 0)))
        path (str (.getAbsolutePath dir) "/event.edn")
        first-value {:event/id "same" :ingested-at "first"}
        repeated-value {:event/id "same" :ingested-at "later"}]
    (is (= :written (ledger/write-once! path first-value)))
    (is (= :unchanged (ledger/write-once! path repeated-value)))
    (is (= first-value (edn/read-string (slurp path))))))
