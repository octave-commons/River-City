(ns river-city.domain.core)

(defn weighted-mean [xs]
  (let [den (reduce + (map :weight xs))]
    (when (pos? den)
      (/ (reduce + (map #(* (:weight %) (:value %)) xs)) den))))

(defn compound-score [{:keys [aggregation]} inputs]
  (case aggregation
    :weighted-mean (weighted-mean inputs)
    :mean (/ (reduce + (map :value inputs)) (count inputs))
    :sum (reduce + (map :value inputs))
    :max (apply max (map :value inputs))
    :min (apply min (map :value inputs))
    (throw (ex-info "Aggregation not implemented" {:aggregation aggregation}))))
