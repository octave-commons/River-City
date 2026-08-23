(ns river-city.law.core
  (:require [clojure.string :as str]
            [malli.core :as m]
            [malli.error :as me]))

(def NonBlankString
  [:and string? [:fn {:error/message "must not be blank"} #(not (str/blank? %))]])
(def Id [:and keyword? [:fn {:error/message "must be a qualified keyword"} qualified-keyword?]])
(def Probability [:double {:min 0.0 :max 1.0}])
(def Score [:double {:min 0.0 :max 100.0}])
(def Weight [:double {:min 0.0}])
(def Trend [:enum :rising :falling :flat :volatile :unknown])
(def EvidenceLevel [:enum :primary :structured :corroborated :reported :inferred :speculative])
(def EvidenceState [:enum :confirmed :provisional :disputed :retracted])
(def Aggregation [:enum :weighted-mean :mean :sum :max :min :residual :manual])
(def MissingPolicy [:enum :drop :zero :fail :carry-forward :unknown])
(def ChartKind [:enum :line-band :mirrored-bars :swimlane :heatmap :scatter :residual :table])
(def TheaterKind [:enum :maritime :land-war :air-war :hybrid :economic :political :other])
(def ChannelKind [:enum :energy :defense :logistics :sanctions :finance :infrastructure :ai-cost :ai-pricing :security :other])

(def Source
  [:map {:closed true}
   [:id Id]
   [:name NonBlankString]
   [:kind [:enum :api :feed :dataset :filing :report :manual]]
   [:url {:optional true} string?]
   [:license {:optional true} string?]
   [:cadence {:optional true} keyword?]
   [:auth {:optional true} [:enum :none :api-key :oauth :token]]])

(def Evidence
  [:map {:closed true}
   [:source Id]
   [:observed-at inst?]
   [:retrieved-at {:optional true} inst?]
   [:level EvidenceLevel]
   [:state EvidenceState]
   [:uri {:optional true} string?]
   [:note {:optional true} string?]
   [:confidence {:optional true} Probability]])

(def Claim
  [:map {:closed true}
   [:id Id]
   [:statement NonBlankString]
   [:evidence [:vector {:min 1} Evidence]]
   [:confidence Probability]
   [:status EvidenceState]])

(def Theater
  [:map {:closed true}
   [:id Id]
   [:name NonBlankString]
   [:kind TheaterKind]
   [:actors [:set Id]]
   [:regions [:set keyword?]]
   [:active? boolean?]])

(def Channel
  [:map {:closed true}
   [:id Id]
   [:name NonBlankString]
   [:kind ChannelKind]
   [:description {:optional true} string?]])

(def SignalResult
  [:map {:closed true}
   [:signal Id]
   [:value number?]
   [:unit keyword?]
   [:trend Trend]
   [:confidence Probability]
   [:evidence [:vector {:min 1} Evidence]]])

(def CompoundInput
  [:map {:closed true}
   [:signal Id]
   [:weight Weight]
   [:transform {:optional true} keyword?]])

(def Compound
  [:map {:closed true}
   [:id Id]
   [:name NonBlankString]
   [:aggregation Aggregation]
   [:missing MissingPolicy]
   [:inputs [:vector {:min 1} CompoundInput]]
   [:unit {:optional true} keyword?]
   [:channels {:optional true} [:set Id]]
   [:theaters {:optional true} [:set Id]]])

(def Contribution
  [:map {:closed true}
   [:signal Id]
   [:raw number?]
   [:weighted number?]
   [:weight Weight]
   [:confidence Probability]])

(def CompoundResult
  [:map {:closed true}
   [:compound Id]
   [:value number?]
   [:trend Trend]
   [:confidence Probability]
   [:contributions [:vector Contribution]]
   [:evidence [:vector Evidence]]])

(def Latent
  [:map {:closed true}
   [:id Id]
   [:name NonBlankString]
   [:description NonBlankString]
   [:channels [:set {:min 1} Id]]
   [:observables [:set {:min 1} Id]]
   [:method [:enum :residual :bayesian :rule :manual :model]]
   [:confidence-floor Probability]])

(def LatentResult
  [:map {:closed true}
   [:latent Id]
   [:value Score]
   [:trend Trend]
   [:confidence Probability]
   [:evidence-level EvidenceLevel]
   [:evidence [:vector Evidence]]
   [:explanation NonBlankString]])

(def Chart
  [:map {:closed true}
   [:id Id]
   [:title NonBlankString]
   [:kind ChartKind]
   [:dataset Id]
   [:x keyword?]
   [:y {:optional true} keyword?]
   [:series {:optional true} keyword?]
   [:baseline {:optional true} Id]
   [:normal-band {:optional true} boolean?]
   [:description {:optional true} string?]])

(def Report
  [:map {:closed true}
   [:id Id]
   [:title NonBlankString]
   [:charts [:vector Id]]
   [:compounds [:vector Id]]
   [:latents [:vector Id]]
   [:separate-fact-interpretation? boolean?]])

(def Config
  [:map {:closed true}
   [:sources [:vector Source]]
   [:theaters [:vector Theater]]
   [:channels [:vector Channel]]
   [:compounds [:vector Compound]]
   [:latents [:vector Latent]]
   [:charts [:vector Chart]]
   [:reports [:vector Report]]])

(defn valid? [schema x] (m/validate schema x))
(defn explain [schema x] (-> (m/explain schema x) me/humanize))
(defn assert-valid! [schema x]
  (if (valid? schema x)
    x
    (throw (ex-info "River City law violation" {:errors (explain schema x)}))))
