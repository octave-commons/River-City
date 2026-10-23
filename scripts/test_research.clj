;; SPDX-License-Identifier: GPL-3.0-or-later
(require '[babashka.classpath :as cp])
(cp/add-classpath "test")
(require '[clojure.test :as test]
         '[river-city.infra.research-test])
(let [{:keys [fail error]} (test/run-tests 'river-city.infra.research-test)]
  (System/exit (+ fail error)))
