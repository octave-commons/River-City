;; River City - geopolitical, energy, and AI infrastructure observability
;; Copyright (C) 2026 River City contributors
;; SPDX-License-Identifier: GPL-3.0-or-later

(ns river-city.shape.portwatch-test
  (:require [clojure.test :refer [deftest is testing]]
            [river-city.shape.portwatch :as portwatch]))

(deftest accepts-arcgis-date-only-values
  (testing "ArcGIS date-only strings normalize to UTC epoch milliseconds"
    (let [value (portwatch/normalize-attributes
                 {"ObjectId" 1
                  "date" "2026-08-30"
                  "portid" "hormuz"
                  "portname" "Strait of Hormuz"
                  "n_total" 10
                  "capacity" 100})]
      (is (= 1788048000000 (:source/date value)))
      (is (portwatch/valid-observation? value)))))

(deftest identifies-target-chokepoints
  (is (portwatch/target-chokepoint? "Strait of Hormuz"))
  (is (portwatch/target-chokepoint? "Bab el Mandeb"))
  (is (not (portwatch/target-chokepoint? "Suez Canal"))))
