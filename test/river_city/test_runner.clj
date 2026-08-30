;; River City - geopolitical, energy, and AI infrastructure observability
;; Copyright (C) 2026 River City contributors
;; SPDX-License-Identifier: GPL-3.0-or-later

(ns river-city.test-runner
  (:require [clojure.test :as test]
            [river-city.domain.portwatch-snapshot-test]
            [river-city.domain.portwatch-test]
            [river-city.domain.refined-fuels-test]
            [river-city.law.core-test]
            [river-city.shape.portwatch-test]))

(def test-namespaces
  '[river-city.law.core-test
    river-city.shape.portwatch-test
    river-city.domain.portwatch-test
    river-city.domain.portwatch-snapshot-test
    river-city.domain.refined-fuels-test])

(defn -main [& _]
  (let [{:keys [fail error] :as result} (apply test/run-tests test-namespaces)]
    (shutdown-agents)
    (println (pr-str {:river-city/tests result}))
    (when (pos? (+ fail error))
      (System/exit 1))))
