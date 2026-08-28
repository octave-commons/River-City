(ns river-city.law.ledger
  (:require [river-city.law.core :as law]))

(def Producer
  [:map {:closed true}
   [:git-sha law/NonBlankString]
   [:workflow {:optional true} law/NonBlankString]
   [:job {:optional true} law/NonBlankString]])

(def LedgerEvent
  [:map {:closed true}
   [:ledger/version pos-int?]
   [:event/id law/NonBlankString]
   [:event/type law/Id]
   [:source/id law/Id]
   [:source/record-id {:optional true} [:or string? int?]]
   [:observed-at law/NonBlankString]
   [:ingested-at law/NonBlankString]
   [:producer Producer]
   [:data map?]
   [:supersedes {:optional true} law/NonBlankString]])

(def Projection
  [:map {:closed true}
   [:projection/version pos-int?]
   [:projection/type law/Id]
   [:as-of law/NonBlankString]
   [:producer Producer]
   [:source-events [:vector law/NonBlankString]]
   [:data any?]])
