(ns river-city.domain.portwatch
  (:require [river-city.shape.portwatch :as portwatch]))

(def projection-type :river-city/maritime-portwatch)

(defn stream-id
  "Stable Clio stream identity for one upstream PortWatch record. Corrections to
   that record advance the same stream instead of becoming unrelated facts."
  [observation]
  (str "river-city:portwatch:" (:source/record-id observation)))

(defn subject
  [observation]
  (str "river-city:chokepoint:" (:port/id observation)))

(defn observation-event?
  [event]
  (= portwatch/event-type (:event/type event)))

(defn apply-event
  "Pure fold for a canonical Clio history. The highest stream revision is the
   current source record; historical corrections remain present in source-events."
  [state event]
  (if-not (observation-event? event)
    state
    (let [stream (:event/stream event)
          current (get state stream)]
      (if (or (nil? current)
              (> (:event/seq event) (:event/seq current)))
        (assoc state stream event)
        state))))

(defn current-events
  [events]
  (->> events
       (reduce apply-event {})
       vals
       (sort-by (juxt (comp :source/date :event/data)
                      (comp :port/name :event/data)
                      :event/stream))
       vec))

(defn project
  "Build River City's disposable PortWatch projection from Clio-canonical
   events. Every historical event remains named in :source-events, while rows
   contain only the current revision of each upstream record."
  [events]
  (let [all (vec (filter observation-event? events))
        current (current-events all)]
    {:projection/type projection-type
     :source-events (mapv :event/id all)
     :rows (mapv (fn [event]
                   (assoc (:event/data event)
                          :event/id (:event/id event)
                          :event/stream (:event/stream event)
                          :event/seq (:event/seq event)))
                 current)}))
