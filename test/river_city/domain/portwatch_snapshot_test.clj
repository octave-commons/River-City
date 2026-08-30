;; River City - geopolitical, energy, and AI infrastructure observability
;; Copyright (C) 2026 River City contributors
;; SPDX-License-Identifier: GPL-3.0-or-later

(ns river-city.domain.portwatch-snapshot-test
  (:require [clojure.test :refer [deftest is testing]]
            [river-city.domain.portwatch-snapshot :as snapshot]))

(defn observation [record-id date port-id port-name total]
  {:source/id :source/imf-portwatch
   :source/record-id record-id
   :source/date (snapshot/local-date->epoch-ms (java.time.LocalDate/parse date))
   :port/id port-id
   :port/name port-name
   :vessels {:total total :cargo (max 0 (dec total)) :tanker 1}
   :capacity {:total (* total 1000)}})

(deftest builds-provisional-observed-history-band
  (let [projection
        (snapshot/project
         {:generated-at "2026-08-30T12:00:00Z"
          :retrieved-at "2026-08-30T11:59:00Z"
          :rows [(observation 1 "2024-08-30" "hormuz" "Strait of Hormuz" 100)
                 (observation 2 "2025-08-30" "hormuz" "Strait of Hormuz" 120)
                 (observation 3 "2026-08-30" "hormuz" "Strait of Hormuz" 30)
                 (observation 4 "2024-08-30" "mandeb" "Bab el-Mandeb" 40)
                 (observation 5 "2025-08-30" "mandeb" "Bab el-Mandeb" 60)
                 (observation 6 "2026-08-30" "mandeb" "Bab el-Mandeb" 20)]})
        hormuz (first (filter #(= "hormuz" (:port-id %)) (:latest projection)))]
    (testing "prior years supply descriptive median and IQR"
      (is (= 2 (:baseline-n hormuz)))
      (is (= 110.0 (:baseline-median hormuz)))
      (is (= 105.0 (:baseline-q25 hormuz)))
      (is (= 115.0 (:baseline-q75 hormuz))))
    (testing "current level preserves absolute and percentage deviation"
      (is (= -80.0 (:deviation-absolute hormuz)))
      (is (< (Math/abs (+ 72.7272727 (:deviation-percent hormuz))) 0.0001)))
    (testing "policy status stays explicit"
      (is (= :provisional (get-in projection [:baseline :status])))
      (is (false? (get-in projection [:baseline :normal-regime?])))
      (is (= 3 (get-in projection [:baseline :policy-issue]))))))
