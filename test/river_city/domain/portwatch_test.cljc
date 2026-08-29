(ns river-city.domain.portwatch-test
  (:require [clojure.test :refer [deftest is testing]]
            [river-city.domain.portwatch :as domain]
            [river-city.shape.portwatch :as shape]))

(def base-data
  {:source/id :source/imf-portwatch
   :source/record-id 42
   :source/date 1787875200000
   :port/id 7
   :port/name "Strait of Hormuz"
   :vessels {:tanker 6 :total 10}
   :capacity {:tanker 700000 :total 900000}})

(defn event
  [id seq data]
  {:event/id id
   :event/type shape/event-type
   :event/stream (domain/stream-id data)
   :event/seq seq
   :event/data data})

(deftest normalizes-provider-shape
  (let [value (shape/normalize-attributes
               {"ObjectId" 42
                "date" "1787875200000"
                "portid" 7
                "portname" "Strait of Hormuz"
                "n_tanker" 6
                "n_total" 10
                "capacity_tanker" 700000
                "capacity" 900000})]
    (is (shape/valid-observation? value))
    (is (= 42 (:source/record-id value)))
    (is (= 1787875200000 (:source/date value)))
    (is (= {:tanker 6 :total 10} (:vessels value)))))

(deftest blank-source-record-id-is-rejected
  (is (false? (shape/valid-observation?
               (assoc base-data :source/record-id "")))))

(deftest correction-keeps-history-but-projects-one-current-row
  (let [old (event "00000000-0000-4000-8000-000000000001" 1 base-data)
        corrected (event "00000000-0000-4000-8000-000000000002"
                         2
                         (assoc-in base-data [:vessels :total] 11))
        projection (domain/project [old corrected])]
    (testing "all contributing facts remain traceable"
      (is (= [(:event/id old) (:event/id corrected)]
             (:source-events projection))))
    (testing "only the newest stream revision appears in current rows"
      (is (= 1 (count (:rows projection))))
      (is (= 2 (:event/seq (first (:rows projection)))))
      (is (= 11 (get-in projection [:rows 0 :vessels :total]))))))
