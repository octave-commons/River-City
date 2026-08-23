(ns river-city.shape.core)

(defn canonical-observation
  [{:keys [id source observed-at metric value unit dimensions evidence] :as x}]
  {:observation/id id
   :observation/source source
   :observation/observed-at observed-at
   :observation/metric metric
   :observation/value value
   :observation/unit unit
   :observation/dimensions (or dimensions {})
   :observation/evidence evidence
   :observation/raw x})
