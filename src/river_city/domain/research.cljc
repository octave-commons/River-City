;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.domain.research
  (:refer-clojure :exclude [abs]))

(defn finite? [x]
  (and (number? x) #?(:clj (Double/isFinite (double x)) :cljs (js/isFinite x))))
(defn- abs [x] (if (neg? x) (- x) x))
(defn- mean [xs] (when (seq xs) (/ (reduce + xs) (count xs))))
(defn- relative [x scale] (/ (abs x) (max 1.0 (abs scale))))
(defn- divergence [policy signal rows]
  (when-let [other (:counterpart signal)]
    (if-not (and (some? (:comparison-key signal))
                 (= (:comparison-key signal) (:comparison-key other))
                 (= (:unit signal) (:unit other)))
      :incomparable
      (let [by-date (into {} (map (juxt :date :value) (:observations other)))
            pairs (keep (fn [{:keys [date value]}]
                          (let [v (get by-date date)]
                            (when (and (finite? value) (finite? v)) [value v]))) rows)]
        (if (< (count pairs) (:min-points policy))
          :no-overlap
          (when (>= (mean (map (fn [[a b]] (relative (- a b) b)) pairs))
                    (:divergence-relative policy)) :divergent))))))

(defn assess
  "Pure diagnostic routing. Thresholds never define analytical normality,
   probabilities, causal coefficients or production model parameters."
  [policy signal]
  (let [rows (vec (take-last (:window policy) (sort-by :date (:observations signal))))
        values (vec (keep #(when (finite? (:value %)) (:value %)) rows))
        n (count values)
        center (mean values)
        latest (:value (last rows))
        enough? (>= n (:min-points policy))
        age (:source-age-days signal)
        invalid-time? (or (not (finite? age)) (neg? age))
        stale? (and (not invalid-time?) (> age (:max-age-days policy)))
        scale (or center 1)
        spread (if (seq values) (relative (- (apply max values) (apply min values)) scale) 0)
        steps (map (fn [[a b]] (relative (- b a) scale)) (partition 2 1 values))
        deltas (map (fn [[a b]] (- b a)) (partition 2 1 values))
        reversals (count (filter (fn [[a b]] (neg? (* a b))) (partition 2 1 deltas)))
        noisy? (and enough? (>= reversals 2) (seq steps)
                    (>= (mean steps) (:noise-relative policy)))
        days (keep :day rows)
        gaps? (and (:cadence-days signal)
                   (some #(> (- (second %) (first %)) (:cadence-days signal)) (partition 2 1 days)))
        flat? (and enough? (<= spread (:flat-relative policy)))
        comparison (:comparison signal)
        reference (:value comparison)
        abnormal? (and (finite? latest) (finite? reference)
                       (>= (relative (- latest reference) reference) (:abnormal-relative policy)))
        uncertain? (or (not (finite? reference)) (< (or (:n comparison) 0) 3)
                       (and (finite? (:lower comparison)) (finite? (:upper comparison))
                            (> (relative (- (:upper comparison) (:lower comparison)) (or reference 1))
                               (:max-relative-uncertainty policy))))
        divergent (divergence policy signal rows)
        flags (cond-> #{}
                (or gaps? (not enough?) (< n (count rows)) (not (finite? latest))) (conj :insufficient)
                invalid-time? (conj :invalid-time)
                stale? (conj :stale)
                abnormal? (conj :displaced)
                flat? (conj :flat)
                (and flat? abnormal?) (conj :flat-abnormal)
                noisy? (conj :noisy)
                uncertain? (conj :uncertain)
                divergent (conj divergent)
                (= :unknown (get-in signal [:regime :status])) (conj :regime-unknown)
                (not= :explained (:explanation-status signal)) (conj :unexplained))
        usable? (and enough? (finite? latest) (not stale?) (not invalid-time?))
        chart-kind (cond (or stale? invalid-time?) :freshness
                         (not usable?) :coverage
                         :else :series)
        rank (cond (not usable?) 0
                   (= divergent :divergent) 5
                   (and abnormal? (not uncertain?) (not noisy?)) 4
                   (and (not flat?) (not noisy?)) 3
                   :else 1)
        question (cond
                   (or stale? invalid-time?) "Does a newer independent observation confirm or contradict the last measured level?"
                   (= divergent :divergent) "Do matched-date source revisions explain the disagreement, or does it persist on a common observation basis?"
                   (and flat? abnormal?) "Does the persistently displaced level survive source-revision checks and an independent indicator of usable capacity?"
                   abnormal? "Does the displacement from descriptive history persist in fresh independent observations with matched coverage and revision state?"
                   noisy? "Does the apparent movement persist after matching coverage and sampling dates across independent sources?"
                   (not enough?) "Can additional source-backed observations distinguish missing coverage from a change in the measured process?"
                   uncertain? "Would more comparable historical observations narrow the comparison enough to distinguish these alternatives?"
                   :else "Does an independent indicator confirm the measured pattern over the next comparable observation window?")]
    (merge (select-keys signal [:id :label :unit :observations :comparison :regime :counterpart :comparison-key])
           {:flags flags :observations rows :source-age-days age :points n :latest latest
            :spread-relative spread :chart-kind chart-kind :rank rank
            :normal-regime-established? (and (true? (:approved? comparison))
                                            (= :normal (get-in signal [:regime :status])))
            :question question :research? (boolean (seq flags))})))

(defn causal-parents
  "Source events used by the primary assessment and comparable matched pairs."
  [assessment]
  (let [rows (:observations assessment)
        primary (into {} (map (juxt :date identity)) rows)
        counterpart (:counterpart assessment)
        comparable? (and (some? (:comparison-key assessment))
                         (= (:comparison-key assessment) (:comparison-key counterpart))
                         (= (:unit assessment) (:unit counterpart)))
        matched (when comparable?
                  (filter #(and (finite? (:value %))
                                (finite? (:value (get primary (:date %)))))
                          (:observations counterpart)))]
    (->> (concat rows matched) (mapcat :event-ids) distinct sort vec)))

(defn lead [assessments]
  (or (first (sort-by (juxt (comp - :rank) :id) assessments))
      {:id "coverage" :label "Canonical signal coverage" :chart-kind :coverage
       :rank 0 :question "Which canonical signal can supply enough current observations to test a substantive explanation?"
       :flags #{:insufficient} :points 0 :observations []}))

(defn admit
  "All attempts, including null/failed/reserved, consume their day budget.
   History is read from retained dossiers, never from an untrusted provider."
  [policy day history assessments]
  (let [spent (count (filter #(= day (:day %)) history))
        limit (min (:max-runs-per-report policy)
                   (max 0 (- (:max-runs-per-day policy) spent)))
        seen (set (map :request-id history))]
    (reduce
     (fn [{:keys [admitted] :as acc} a]
       (let [prior-days (keep #(when (= (:id a) (:id %)) (:day %)) history)
             last-day (when (seq prior-days) (apply max prior-days))
             reason (cond (not (:research? a)) :informative
                          (contains? seen (:request-id a)) :duplicate
                          (and last-day (< (- day last-day) (:cooldown-days policy))) :cooldown
                          (>= (count admitted) limit) :budget)]
         (if reason (update acc :suppressed conj {:id (:id a) :request-id (:request-id a) :reason reason})
             (update acc :admitted conj a))))
     {:admitted [] :suppressed []}
     (sort-by (juxt (comp - :rank) :id) assessments))))

(defn research-brief [assessment]
  {:question (:question assessment)
   :proposals [{:status :proposed :indicator "Independent usable-capacity or recipient-delivery observations"
                :rationale "Physical passage counts alone do not establish deliverability."
                :production-change? false}]
   :countermodels [{:status :proposed :explanation "Coverage, sampling, or source revisions explain the apparent pattern."}
                   {:status :proposed :explanation "A sustained physical or economic constraint explains the pattern."}]
   :tests [{:status :proposed :test "Match dates, geography, unit, observation definition and revision state across sources."
            :falsifies "Reject a common-basis disagreement if reconciled revisions remove it."}
           {:status :proposed :test "Compare a fresh independent deliverability observation with the next canonical observation."
            :falsifies "Reject persistence if the next comparable observations return inside the descriptive historical band."}]
   :production-change? false})
