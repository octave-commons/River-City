;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.host.research-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [clio.infra.runtime :as runtime]
            [clio.infra.ledger :as ledger]
            [clio.law.schema :as schema]
            [river-city.infra.research-host :as host])
  (:import [java.nio.file Files]
           [java.nio.file.attribute FileAttribute]))

(deftest shared-clio-admission-replay-and-missing-parent
  (let [dir (str (Files/createTempDirectory "river-clio-" (make-array FileAttribute 0)))
        schemas (str (io/file dir "schemas"))
        parent-ledger (str (io/file dir "observations.edn"))
        research-ledger (str (io/file dir "research.edn"))
        rt (runtime/open schemas {:test/observed (schema/event-schema :test/observed [:map [:value :int]])})
        _ (ledger/create-ledger! parent-ledger)
        parent (:event (runtime/append! rt parent-ledger :test/observed
                       {:event/stream "source:a" :event/seq 1 :event/actor "test"
                        :event/subject "source:a" :event/data {:value 20}}))
        request {:request-id "research-test" :causal/parents [(:event/id parent)]
                 :input-refs [{:path "source:a"}]}
        result {:request-id "research-test" :status :null :production-change? false
                :evidence [] :proposals [] :countermodels [] :tests []}
        options {:schema-directory schemas :ledger-path research-ledger :parent-ledgers [parent-ledger]
                 :dossier-path dir :actor "river-city-test"}]
    (spit (io/file dir "request.edn") (pr-str request))
    (spit (io/file dir "result.edn") (pr-str result))
    (let [first-result (host/admit! options)
          replay (host/admit! options)
          rt (runtime/open schemas host/catalog)
          history (runtime/canonicalize-files rt [research-ledger parent-ledger])]
      (is (= :appended (:append/result first-result)))
      (is (= :already-present (:append/result replay)))
      (is (= 2 (count (:canonical/events history))))
      (is (= [(:event/id parent)] (get-in first-result [:event :event/causes])))
      (is (= (:event first-result) (:event replay)))
      (let [relocated (str (Files/createTempDirectory "river-clio-copy-" (make-array FileAttribute 0)))]
        (doseq [name ["request.edn" "result.edn"]]
          (io/copy (io/file dir name) (io/file relocated name)))
        (let [moved-replay (host/admit! (assoc options :dossier-path relocated))]
          (is (= :already-present (:append/result moved-replay)))
          (is (= (:event first-result) (:event moved-replay)))))
      (is (thrown-with-msg? Exception #"parents absent" (host/admit! (assoc options :parent-ledgers []))))
      (spit (io/file dir "result.edn") (pr-str (assoc result :status :completed)))
      (is (thrown-with-msg? Exception #"differs" (host/admit! options))))))

(defn -main [& _]
  (let [{:keys [fail error]} (clojure.test/run-tests 'river-city.host.research-test)]
    (shutdown-agents)
    (System/exit (+ fail error))))

(deftest failed-non-utf8-evidence-is-admitted-by-its-byte-digest
  (let [dir (str (Files/createTempDirectory "river-binary-" (make-array FileAttribute 0)))
        request {:request-id "binary-failure" :input-refs [] :causal/parents []}
        bytes (byte-array (map unchecked-byte [255 0 195 40]))
        digest (.formatHex (java.util.HexFormat/of)
                           (.digest (java.security.MessageDigest/getInstance "SHA-256") bytes))
        result {:request-id "binary-failure" :status :failed :production-change? false
                :proposals [] :countermodels [] :tests []
                :evidence [{:uri "https://api.crossref.org/works" :raw-path "query-0.json"
                            :sha256 digest :retrieved-at "2026-10-21T00:00:00Z"
                            :evidence-level :bibliographic-metadata}]}]
    (Files/write (.toPath (io/file dir "query-0.json")) bytes (make-array java.nio.file.OpenOption 0))
    (spit (io/file dir "request.edn") (pr-str request))
    (spit (io/file dir "result.edn") (pr-str result))
    (is (= :appended (:append/result (host/admit! {:schema-directory (str (io/file dir "schemas"))
                     :ledger-path (str (io/file dir "research.edn")) :parent-ledgers []
                     :dossier-path dir :actor "river-city-test"}))))))

(deftest shaped-divergence-requires-both-sources-through-host-admission
  (let [dir (str (Files/createTempDirectory "river-divergence-" (make-array FileAttribute 0)))
        schemas (str (io/file dir "schemas"))
        rt (runtime/open schemas {:test/observed (schema/event-schema :test/observed [:map [:value :int]])})
        primary-path (str (io/file dir "primary.edn"))
        counterpart-path (str (io/file dir "counterpart.edn"))
        append-source (fn [path stream value]
                        (ledger/create-ledger! path)
                        (:event (runtime/append! rt path :test/observed
                                  {:event/stream stream :event/seq 1 :event/actor "test"
                                   :event/subject stream :event/data {:value value}})))
        primary (append-source primary-path "primary" 20)
        counterpart (append-source counterpart-path "counterpart" 100)
        command [(or (System/getenv "RIVER_CITY_BB") "bb") "--config" "/dev/null" "-cp"
                 (System/getProperty "java.class.path") "test/fixtures/divergence_dossier.clj"
                 (str (io/file dir "dossiers")) (:event/id primary) (:event/id counterpart)]
        child (.start (ProcessBuilder. ^java.util.List command))]
    (when-not (.waitFor child 20 java.util.concurrent.TimeUnit/SECONDS)
      (.destroyForcibly child)
      (throw (ex-info "Dossier fixture timed out" {})))
    (let [output (slurp (.getInputStream child))
          errors (slurp (.getErrorStream child))]
      (is (= 0 (.exitValue child)) errors)
      (when (zero? (.exitValue child))
        (let [dossier (clojure.edn/read-string output)
              options {:schema-directory schemas :ledger-path (str (io/file dir "research.edn"))
                       :parent-ledgers [primary-path] :dossier-path (:path dossier) :actor "test"}]
          (is (contains? (get-in dossier [:request :flags]) :divergent))
          (is (= #{(:event/id primary) (:event/id counterpart)} (set (get-in dossier [:request :causal/parents]))))
          (is (thrown-with-msg? Exception #"parents absent" (host/admit! options)))
          (is (= :appended (:append/result (host/admit! (update options :parent-ledgers conj counterpart-path))))))))))
