(ns river-city.law.core-test
  (:require [clojure.edn :as edn]
            [clojure.test :refer [deftest is testing]]
            [river-city.law.core :as law]))
(deftest config-validates
  (testing "seed configuration conforms to law"
    (let [cfg (edn/read-string (slurp "resources/river_city/config.edn"))]
      (is (law/valid? law/Config cfg)))))
