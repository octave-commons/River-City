;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.shape.research
  (:require [clojure.string :as str])
  (:import [java.time LocalDate]))

(defn epoch-day [date] (.toEpochDay (LocalDate/parse (subs date 0 10))))

(defn portwatch-signals
  "Evaluate source observation dates, not projection generation/retrieval time.
   Carries original source row and causal event refs when supplied by the host."
  [projection as-of]
  (->> (:rows projection)
       (group-by :port-id)
       (sort-by key)
       (mapv
        (fn [[id rows]]
          (let [rows (sort-by :date rows)
                latest (last rows)
                date (:date latest)]
            {:id (str "portwatch/" id) :label (str (:port-name latest) " visible vessel passages")
             :unit "vessels/day" :cadence-days 1 :comparison-key (str "portwatch-visible-vessels/" id)
             :source-age-days (- (epoch-day as-of) (epoch-day date))
             :regime {:status :unknown
                      :note "All-prior-year descriptive history; approved normal-regime exclusions remain issue #3."}
             :explanation-status :unknown
             :comparison {:value (:baseline-median latest) :lower (:baseline-q25 latest)
                          :upper (:baseline-q75 latest) :n (or (:baseline-n latest) 0)
                          :approved? false}
             :observations (mapv (fn [row]
                                   {:date (:date row) :day (epoch-day (:date row)) :value (:vessels-total row)
                                    :source-ref (str "imf-portwatch/" (:source-record-id row))
                                    :event-ids (vec (distinct (map str (concat (:event-ids row)
                                                        (when-let [id (:event/id row)] [id])))))}) rows)})))))
