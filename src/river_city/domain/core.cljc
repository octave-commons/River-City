(ns river-city.domain.core)

(defn weighted-mean [xs]
  (let [den (reduce + (map :weight xs))]
    (when (pos? den)
      (/ (reduce + (map #(* (:weight %) (:value %)) xs)) den))))

(def refined-fuels-weights
  "Provisional weights for the refined-fuels stress series. These are model
  assumptions, not observations, and should remain auditable/configurable."
  {:distillate-inventory-stress 0.35
   :refined-product-flow-gap 0.25
   :refining-margin-stress 0.20
   :refinery-outage-stress 0.20})

(defn refined-fuels-stress
  "Compute a 0-100 refined-fuels stress score from normalized component
  signals. Missing components are dropped and the remaining weights are
  renormalized; nil means there is no usable evidence for the period."
  [components]
  (let [inputs (keep (fn [[component weight]]
                       (when-some [value (get components component)]
                         {:value value :weight weight}))
                     refined-fuels-weights)]
    (when (seq inputs)
      (weighted-mean inputs))))

(defn compound-score [{:keys [aggregation]} inputs]
  (case aggregation
    :weighted-mean (weighted-mean inputs)
    :mean (/ (reduce + (map :value inputs)) (count inputs))
    :sum (reduce + (map :value inputs))
    :max (apply max (map :value inputs))
    :min (apply min (map :value inputs))
    (throw (ex-info "Aggregation not implemented" {:aggregation aggregation}))))
