(ns river-city.infra.ledger
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.nio.file Files StandardCopyOption]
           [java.security MessageDigest]))

(defn sha256 [s]
  (let [digest (.digest (doto (MessageDigest/getInstance "SHA-256")
                          (.update (.getBytes s "UTF-8"))))]
    (apply str (map #(format "%02x" (bit-and (int %) 0xff)) digest))))

(defn canonical-edn [x]
  (pr-str x))

(defn event-id [source-id source-record-id normalized]
  (sha256 (pr-str [source-id source-record-id normalized])))

(defn- ensure-parent! [path]
  (io/make-parents path)
  path)

(defn- atomic-spit! [path content]
  (ensure-parent! path)
  (let [target (.toPath (io/file path))
        tmp (.toPath (io/file (str path ".tmp")))]
    (spit (.toFile tmp) content)
    (Files/move tmp target
                (into-array StandardCopyOption
                            [StandardCopyOption/REPLACE_EXISTING
                             StandardCopyOption/ATOMIC_MOVE])))
  path)

(defn write-once!
  "Write immutable EDN. If the path exists with different content, fail closed."
  [path value]
  (let [content (canonical-edn value)
        file (io/file path)]
    (if (.exists file)
      (if (= content (slurp file))
        :unchanged
        (throw (ex-info "attempted ledger rewrite" {:path path})))
      (do (atomic-spit! path content) :written))))

(defn write-derived!
  "Replace deterministic derived EDN only when the canonical content changed."
  [path value]
  (let [content (canonical-edn value)
        file (io/file path)]
    (if (and (.exists file) (= content (slurp file)))
      :unchanged
      (do (atomic-spit! path content) :written))))

(defn event-path [event]
  (let [observed-at (:observed-at event)
        source-id (:source/id event)
        event-id (:event/id event)
        [year month day] (str/split observed-at #"-" 3)
        source-name (name source-id)]
    (format "ledger/events/%s/%s/%s/%s/%s.edn"
            year month day source-name event-id)))

(defn read-events
  ([] (read-events "ledger/events"))
  ([root]
   (let [dir (io/file root)]
     (if-not (.exists dir)
       []
       (->> (file-seq dir)
            (filter #(.isFile %))
            (filter #(str/ends-with? (.getName %) ".edn"))
            (map #(edn/read-string (slurp %))))))))
