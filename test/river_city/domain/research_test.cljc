;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.domain.research-test
  (:require [clojure.test :refer [deftest is testing]]
            [river-city.domain.research :as r]))
(def policy {:version 1 :window 14 :min-points 5 :max-age-days 3
             :flat-relative 0.03 :noise-relative 0.35 :abnormal-relative 0.25
             :divergence-relative 0.25 :max-relative-uncertainty 0.5
             :cooldown-days 7 :max-runs-per-day 2 :max-runs-per-report 2
             :queries-per-run 2 :max-response-bytes 262144 :timeout-ms 15000
             :max-candidates 4})
(defn signal [values]
  {:id "hormuz" :label "Hormuz visible passages" :unit "vessels/day"
   :source-age-days 0 :regime {:status :unknown :note "No approved exclusions"}
   :explanation-status :unknown
   :comparison {:value 100 :lower 90 :upper 110 :n 5 :approved? false}
   :observations (mapv (fn [i v] {:date (str "2026-10-" (+ 10 i))
                                 :value v :source-ref (str "imf:" i)})
                       (range) values)})
(deftest detects-flat-but-abnormal-without-approving-regime
  (let [a (r/assess policy (signal (repeat 7 20)))]
    (is (contains? (:flags a) :flat-abnormal))
    (is (contains? (:flags a) :flat))
    (is (= :unknown (get-in a [:regime :status])))
    (is (false? (:normal-regime-established? a)))
    (is (seq (:question a)))
    (is (contains? (:flags (r/assess policy (signal (repeat 7 100)))) :flat))))
(deftest distinguishes-noise-missingness-and-source-age
  (is (contains? (:flags (r/assess policy (signal [20 160 10 180 20 170]))) :noisy))
  (is (contains? (:flags (r/assess policy (assoc (signal [80 82 84 86 88]) :source-age-days 8))) :stale))
  (is (contains? (:flags (r/assess policy (assoc (signal [80 82 84 86 88]) :source-age-days -1))) :invalid-time))
  (is (contains? (:flags (r/assess policy (signal [nil nil nil nil nil]))) :insufficient))
  (is (contains? (:flags (r/assess policy (assoc-in (signal [80 82 84 86 88]) [:comparison :n] 1))) :uncertain)))
(deftest divergence-requires-overlap-and-comparability
  (let [s (signal [20 20 20 20 20])
        other {:unit "vessels/day" :comparison-key "visible:hormuz"
               :observations (:observations (signal [100 100 100 100 100]))}
        s (assoc s :comparison-key "visible:hormuz" :counterpart other)]
    (is (contains? (:flags (r/assess policy s)) :divergent))
    (is (not (contains? (:flags (r/assess policy (assoc-in s [:counterpart :unit] "barrels"))) :divergent)))
    (is (contains? (:flags (r/assess policy (assoc-in s [:counterpart :unit] "barrels"))) :incomparable))
    (is (not (contains? (:flags (r/assess policy (assoc-in s [:counterpart :observations] []))) :divergent)))))
(deftest admission-replay-cooldown-and-budget
  (let [a (assoc (r/assess policy (signal (repeat 7 20))) :request-id "a")
        b (assoc a :id "mandeb" :request-id "b")
        c (assoc a :id "third" :request-id "c")
        admitted (r/admit policy 200 [] [c a b])
        history [{:request-id "a" :id "hormuz" :day 200 :status :null}]]
    (is (= ["a" "b"] (mapv :request-id (:admitted admitted))))
    (is (= :budget (-> admitted :suppressed first :reason)))
    (is (= :duplicate (-> (r/admit policy 208 history [a]) :suppressed first :reason)))
    (is (= :cooldown (-> (r/admit policy 201 history [(assoc a :request-id "new")]) :suppressed first :reason)))
    (is (empty? (:admitted (r/admit (assoc policy :max-runs-per-day 1) 200 history [b]))))
    (is (empty? (:admitted (r/admit policy 200 (conj history {:id "x" :request-id "x" :day 200 :status :failed}) [b]))))))
(deftest report-ranking-prefers-supported-questions
  (let [flat (r/assess policy (signal (repeat 7 20)))
        stale (r/assess policy (assoc (signal [20 22 24 26 28]) :id "stale" :source-age-days 8))]
    (is (= "hormuz" (:id (r/lead [stale flat]))))
    (is (= :freshness (:chart-kind (r/lead [stale]))))
    (is (= :coverage (:chart-kind (r/lead []))))))
(deftest monotonic-movement-is-not-labelled-noise
  (is (not (contains? (:flags (r/assess policy (signal [10 50 90 130 170 210]))) :noisy))))
