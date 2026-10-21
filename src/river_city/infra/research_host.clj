;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.infra.research-host
  (:require [clojure.java.io :as io]
            [clio.shape.edn :as edn]
            [clio.extern.jvm.crypto :as crypto]
            [clio.infra.runtime :as runtime]
            [clio.infra.ledger :as ledger]
            [clio.law.schema :as schema]
            [river-city.law.research :as law]
            [malli.core :as m]))

(def catalog
  {:river-city/research-completed
   (schema/event-schema :river-city/research-completed law/ResearchCompleted)})

(defn admit!
  "Called by the Foresight host with its schema store and complete causal input
   partitions. Uses Clio for identities/admission/replay; no local ledger kernel.
   Explicit host-path arguments are required. Existing source partitions and
   their historical catalogs must be supplied; missing parents fail closed."
  [{:keys [schema-directory ledger-path parent-ledgers dossier-path actor]}]
  (when-not (and schema-directory ledger-path dossier-path (seq actor))
    (throw (ex-info "Explicit Foresight host paths and actor required" {})))
  (let [request-path (str (io/file dossier-path "request.edn"))
        result-path (str (io/file dossier-path "result.edn"))
        request (edn/read-one (slurp request-path))
        result (edn/read-one (slurp result-path))
        rt (runtime/open schema-directory catalog)
        parents (vec (distinct (:causal/parents request)))
        parent-history (when (seq parent-ledgers) (runtime/canonicalize-files rt parent-ledgers))
        known (set (map :event/id (:canonical/events parent-history)))
        missing (remove known parents)
        data {:request-id (:request-id request)
              :request-digest (crypto/sha256 (slurp request-path)) :result-digest (crypto/sha256 (slurp result-path))
              :request-path request-path :result-path result-path :input-refs (:input-refs request)
              :status (:status result) :production-change? false}]
    (when-not (and (= (:request-id request) (:request-id result))
                   (m/validate law/Result result))
      (throw (ex-info "Invalid retained research result" {})))
    (doseq [{:keys [raw-path sha256]} (:evidence result)]
      (when-not (and (re-matches #"query-[0-3]\.json" raw-path)
                     (= sha256 (crypto/sha256 (slurp (io/file dossier-path raw-path)))))
        (throw (ex-info "Retained research evidence digest mismatch" {:path raw-path}))))
    (when (seq missing)
      (throw (ex-info "Research source parents absent from Foresight history" {:missing (vec missing)})))
    (when-not (.exists (io/file ledger-path)) (ledger/create-ledger! ledger-path))
    (let [stream (str "river-city:research:" (:request-id request))
          existing (first (filter #(= stream (:event/stream %)) (ledger/read-ledger ledger-path)))]
      (if existing
        (do
          (when-not (= data (:event/data existing))
            (throw (ex-info "Retained research dossier differs from admitted event" {:request-id (:request-id request)})))
          (runtime/ensure-durable! rt ledger-path)
          {:append/result :already-present :event existing})
        (runtime/append! rt ledger-path :river-city/research-completed
                         {:event/stream stream :event/seq 1 :event/causes parents
                          :event/actor actor :event/subject stream :event/data data})))))

(defn -main [& [options-path]]
  (when-not options-path (throw (ex-info "Pass an explicit Foresight host-options EDN file" {})))
  (prn (dissoc (admit! (edn/read-one (slurp options-path))) :runtime)))
