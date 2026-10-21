;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.law.research)

;; Data-only Malli forms; the Foresight Clio host owns event envelopes.
(def Observation
  [:map [:date :string] [:value [:maybe 'number?]] [:source-ref :string]
   [:day {:optional true} :int] [:event-ids {:optional true} [:vector :string]]])
(def Signal
  [:map [:id :string] [:label :string] [:unit :string]
   [:source-age-days [:maybe 'number?]]
   [:observations [:vector Observation]]
   [:regime [:map [:status :keyword] [:note :string]]]
   [:explanation-status [:enum :explained :unexplained :unknown]]])
(def Policy
  [:map {:closed true}
   [:version [:int {:min 1}]] [:window [:int {:min 1}]] [:min-points [:int {:min 1}]]
   [:max-age-days [:int {:min 0}]] [:flat-relative 'number?] [:noise-relative 'number?]
   [:abnormal-relative 'number?] [:divergence-relative 'number?]
   [:max-relative-uncertainty 'number?] [:cooldown-days [:int {:min 0}]]
   [:max-runs-per-day [:int {:min 0}]] [:max-runs-per-report [:int {:min 0}]]
   [:queries-per-run [:int {:min 1}]] [:max-response-bytes [:int {:min 1}]]
   [:timeout-ms [:int {:min 1}]] [:max-candidates [:int {:min 1}]]])
(def Result
  [:map [:request-id :string]
   [:status [:enum :completed :null :failed :interrupted]]
   [:evidence [:vector [:map [:uri :string] [:raw-path :string]
                        [:sha256 :string] [:retrieved-at :string]
                        [:evidence-level :keyword]]]]
   [:proposals [:vector :any]] [:countermodels [:vector :any]] [:tests [:vector :any]]
   [:production-change? [:= false]]])
(def ResearchCompleted
  [:map {:closed true}
   [:request-id :string] [:request-digest :string] [:result-digest :string]
   [:request-path :string] [:result-path :string]
   [:input-refs [:vector :any]]
   [:status [:enum :completed :null :failed :interrupted]]
   [:production-change? [:= false]]])

(def Reservation
  [:map [:request-id [:string {:min 1}]] [:id [:string {:min 1}]]
   [:day :int] [:as-of :string] [:policy Policy]])
