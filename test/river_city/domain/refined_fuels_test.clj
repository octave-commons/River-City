;; River City - geopolitical, energy, and AI infrastructure observability
;; Copyright (C) 2026 River City contributors
;; SPDX-License-Identifier: GPL-3.0-or-later

(ns river-city.domain.refined-fuels-test
  (:require [clojure.test :refer [deftest is testing]]
            [river-city.domain.core :as domain]))

(deftest refined-fuels-stress-test
  (testing "uses only available components and renormalizes weights"
    (is (= 50.0
           (double
            (domain/refined-fuels-stress
             {:distillate-inventory-stress 40.0
              :refined-product-flow-gap 64.0}))))))

(deftest refined-fuels-stress-empty-test
  (testing "returns nil when no usable evidence exists"
    (is (nil? (domain/refined-fuels-stress {})))))
