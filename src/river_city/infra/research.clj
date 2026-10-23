;; SPDX-License-Identifier: GPL-3.0-or-later
(ns river-city.infra.research
  (:require [babashka.fs :as fs]
            [babashka.process :as process]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [malli.core :as m]
            [river-city.domain.research :as domain]
            [river-city.law.research :as law]
            [river-city.shape.research :as shape])
  (:import [java.security MessageDigest]
           [java.nio.channels FileChannel]
           [java.nio.file StandardOpenOption Files]
           [java.util.concurrent TimeUnit]))

(def root "data/research")
(defn- read-edn [path] (edn/read-string (slurp (str path))))
(defn- write-edn [path value] (spit (str path) (str (pr-str value) "\n")))
(defn- stable [value]
  (cond (map? value) (into (sorted-map-by #(compare (str %1) (str %2))) (map (fn [[k v]] [k (stable v)]) value))
        (set? value) (mapv stable (sort-by str value))
        (sequential? value) (mapv stable value)
        :else value))
(defn digest
  "Diagnostic cache identity only. Does not implement or assert Clio identity."
  [value]
  (apply str (map #(format "%02x" (bit-and (int %) 255))
                  (.digest (MessageDigest/getInstance "SHA-256")
                           (.getBytes (pr-str (stable value)) "UTF-8")))))
(defn file-sha256 [path]
  (apply str (map #(format "%02x" (bit-and (int %) 255))
                  (.digest (MessageDigest/getInstance "SHA-256") (Files/readAllBytes (fs/path path))))))
(defn implementation []
  {:orchestrator-sha256 (file-sha256 "src/river_city/infra/research.clj")
   :shape-sha256 (file-sha256 "src/river_city/shape/research.clj")
   :worker-sha256 (file-sha256 "scripts/research_worker.mjs")
   :domain-sha256 (file-sha256 "src/river_city/domain/research.cljc")
   :law-sha256 (file-sha256 "src/river_city/law/research.cljc")})
(defn- json-safe [x]
  (cond (map? x) (into {} (map (fn [[k v]] [(if (keyword? k) (subs (str k) 1) (str k)) (json-safe v)])) x)
        (set? x) (mapv json-safe (sort-by str x))
        (sequential? x) (mapv json-safe x)
        (keyword? x) (subs (str x) 1)
        :else x))
(defn- write-json [path x] (spit (str path) (str (json/generate-string (json-safe x) {:pretty true}) "\n")))
(defn validate-policy! [p]
  (let [p p]
    (when-not (and (m/validate law/Policy p)
                   (<= (:min-points p) (:window p))
                   (every? #(<= 0 (% p) 1) [:flat-relative :noise-relative :abnormal-relative
                                            :divergence-relative :max-relative-uncertainty])
                   (<= (:queries-per-run p) 4) (<= (:timeout-ms p) 30000)
                   (<= (:max-response-bytes p) 1048576) (<= (:max-candidates p) 10)
                   (<= (:max-runs-per-day p) 10) (<= (:max-runs-per-report p) 10))
      (throw (ex-info "Invalid research policy" {:policy p})))
    p))

(defn policy [] (validate-policy! (read-edn "resources/river_city/research.edn")))

(declare retained-evidence?)

(defn history [directory]
  ;; A crash before request publication must block further spending, rather
  ;; than making its reservation disappear from the daily accounting.
  (doseq [path (fs/list-dir directory)
          :when (and (fs/directory? path) (str/starts-with? (str (fs/file-name path)) "research-"))]
    (when-not (fs/exists? (fs/path path "request.edn"))
      (throw (ex-info "Incomplete research reservation; reconcile before new spending" {:path (str path)}))))
  (mapv (fn [file]
          (let [request (read-edn file)
                request-id (:request-id request)
                path (fs/parent file)
                base (assoc (select-keys request [:request-id :id :day]) :path (str path))
                result-path (fs/path path "result.edn")]
            (when-not (and (m/validate law/Reservation request)
                           (= request-id (str (fs/file-name path)))
                           (try (= (:day request) (shape/epoch-day (:as-of request)))
                                (catch Exception _ false)))
              (throw (ex-info "Invalid research reservation" {:request-id request-id :path (str file)})))
            (try (validate-policy! (:policy request))
                 (catch Exception e
                   (throw (ex-info "Invalid research reservation" {:request-id request-id :path (str file)} e))))
            (if-not (fs/exists? result-path)
              (assoc base :status :interrupted)
              (let [result (read-edn result-path)]
                (when-not (and (= request-id (:request-id result))
                               (false? (:production-change? result))
                               (m/validate law/Result result)
                               (every? #(retained-evidence? path (:policy request) %) (:evidence result)))
                  (throw (ex-info "Invalid or unretained research result" {:request-id request-id})))
                (assoc base :status (:status result))))))
        (sort (fs/glob directory "*/request.edn"))))

(defn execute-worker!
  ([directory p] (execute-worker! directory p ["node" "scripts/research_worker.mjs" (str directory)]))
  ([directory p command]
   (let [deadline (+ 5000 (* (:queries-per-run p) (:timeout-ms p)))
         child (apply process/process {:out :inherit :err :inherit} command)
         ^Process proc (:proc child)]
     (try
       (when-not (.waitFor proc (long deadline) TimeUnit/MILLISECONDS)
         (throw (ex-info "Research worker timed out" {:deadline-ms deadline})))
       (when-not (zero? (.exitValue proc))
         (throw (ex-info "Research worker failed" {:exit (.exitValue proc)})))
       (json/parse-string (slurp (str (fs/path directory "worker-result.json"))) true)
       (finally
         ;; Includes descendants; no unbounded deref waits on inherited pipes.
         (process/destroy-tree child))))))

(defn- retained-evidence? [directory policy evidence]
  (let [{:keys [raw-path sha256]} evidence]
    (and (string? raw-path) (re-matches #"query-[0-3]\.json" raw-path)
         (let [path (fs/path directory raw-path)]
           (and (fs/exists? path)
                (<= (fs/size path) (:max-response-bytes policy))
                (= sha256 (apply str (map #(format "%02x" (bit-and (int %) 255))
                              (.digest (MessageDigest/getInstance "SHA-256")
                                       (Files/readAllBytes path))))))))))

(defn- normalize-result [worker request directory]
  (let [result (merge (domain/research-brief request) worker
                      {:status (keyword (:status worker)) :request-id (:request-id request)
                       :evidence (mapv #(update % :evidence-level keyword) (:evidence worker))
                       :tests (into (:tests (domain/research-brief request)) (:tests worker))
                       :proposals (into (:proposals (domain/research-brief request)) (:proposals worker))})]
    (when-not (and (= (:request-id request) (:request-id worker))
                   (false? (:production-change? worker))
                   (m/validate law/Result result)
                   (every? #(retained-evidence? directory (:policy request) %) (:evidence result)))
      (throw (ex-info "Invalid or unretained research result" {:request-id (:request-id request)})))
    result))

(defn- run-request! [directory request worker]
  ;; Directory creation is exclusive; completed dossiers are never rewritten.
  (let [path (fs/path directory (:request-id request))]
    (fs/create-dir path)
    (write-edn (fs/path path "request.edn") request)
    (write-json (fs/path path "request.json") request)
    (let [result (try (normalize-result (worker path (:policy request)) request path)
                      (catch Exception e
                        (merge (domain/research-brief request)
                               {:request-id (:request-id request) :status :failed :evidence []
                                :errors [{:error (ex-message e)}]})))
          ;; Host-owned envelopes/UUIDs/admission remain with Clio. A draft has
          ;; input references and actual parents only, never invented events.
          draft {:draft/type :river-city/research-completed
                 :draft/data {:request request :result result}
                 :causal/parents (:causal/parents request)
                 :causal/input-refs (:input-refs request)
                 :host :foresight :admission :pending}]
      (write-edn (fs/path path "result.edn") result)
      (write-edn (fs/path path "event-draft.edn") draft)
      {:request request :result result :path (str path)})))

(defn run!
  "Single-writer operational mutex protects budgets, not a canonical ledger.
   Retained dossiers are source evidence bundles; Foresight owns admission."
  [{:keys [signals as-of input-refs directory worker policy-value]
    :or {directory root worker execute-worker!}}]
  (let [p (validate-policy! (or policy-value (policy)))]
    (when-not (= (count signals) (count (distinct (map :id signals))))
      (throw (ex-info "Duplicate canonical signal identity" {})))
    (doseq [s signals]
      (when-not (m/validate law/Signal s)
        (throw (ex-info "Invalid canonical signal" {:id (:id s)}))))
    (fs/create-dirs directory)
    (with-open [channel (FileChannel/open (fs/path directory ".budget.lock")
                                         (into-array StandardOpenOption [StandardOpenOption/CREATE StandardOpenOption/WRITE]))]
      (let [lock (.tryLock channel)]
        (when-not lock (throw (ex-info "Research budget is already in use" {})))
        (try
          (let [implementation-id (implementation)
                day (shape/epoch-day as-of)
                assessments (mapv (fn [s]
                                    (assoc (domain/assess p s)
                                           :request-id (str "research-" (digest {:signal s :policy p :implementation implementation-id})))) signals)
                prior (history directory)
                admission (domain/admit p day prior assessments)
                results (mapv (fn [a]
                                (run-request! directory
                                  (assoc a :day day :as-of as-of :policy p :implementation implementation-id
                                         :input-refs (vec input-refs)
                                         :causal/parents (domain/causal-parents a)) worker))
                              (:admitted admission))]
            {:as-of as-of :policy p :assessments assessments :lead (domain/lead assessments)
             :admission admission :results results :history (history directory)
             :lineage {:mode :source-evidence-bundles :ledger-backed? false :event-host :foresight
                       :event-admission :pending :input-refs (vec input-refs)}})
          (finally (.close ^java.lang.AutoCloseable lock)))))))

(defn- xml [x]
  (str/escape (str x) {\& "&amp;" \< "&lt;" \> "&gt;" \" "&quot;" \' "&apos;"}))
(defn chart-svg [{:keys [lead assessments]}]
  (let [kind (:chart-kind lead)
        observations (filter #(domain/finite? (:value %)) (:observations lead))
        values (map :value observations)
        lower (get-in lead [:comparison :lower]) upper (get-in lead [:comparison :upper])
        all (filter domain/finite? (concat [0 lower upper] values))
        lo (if (seq all) (apply min all) 0) hi (if (seq all) (apply max all) 1)
        y (fn [v] (- 240 (* 170 (/ (- (double v) lo) (max 1.0 (- hi lo))))))
        raw-rows (:observations lead)
        start-day (or (:day (first raw-rows)) 0)
        end-day (or (:day (last raw-rows)) (dec (count raw-rows)))
        xpos (fn [i row] (+ 60 (* 620 (/ (double (- (or (:day row) i) start-day)) (max 1 (- end-day start-day))))))
        curve (apply str (map-indexed
                 (fn [i row]
                   (when (domain/finite? (:value row))
                     (let [previous (when (pos? i) (nth raw-rows (dec i)))
                           contiguous? (and (domain/finite? (:value previous))
                                            (or (nil? (:day row))
                                                (= 1 (- (:day row) (or (:day previous) (:day row))))))]
                       (str (if contiguous? "L" "M") (xpos i row) "," (y (:value row)))))) raw-rows))]
    (str "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"800\" height=\"330\" viewBox=\"0 0 800 330\" role=\"img\">"
         "<title>" (xml (:label lead)) "</title><desc>" (xml (:question lead)) "</desc>"
         "<rect width=\"800\" height=\"330\" fill=\"#fafaf7\"/>"
         "<g font-family=\"sans-serif\" fill=\"#182e39\"><text x=\"25\" y=\"30\" font-size=\"19\">" (xml (:label lead)) "</text>"
         (if (= kind :series)
           (str (when (and (domain/finite? lower) (domain/finite? upper))
                  (str "<rect x=\"60\" y=\"" (y upper) "\" width=\"620\" height=\"" (max 0 (- (y lower) (y upper))) "\" fill=\"#d9e4de\"/>"))
                "<path d=\"M60 60V240H700\" fill=\"none\" stroke=\"#687d88\"/>"
                "<path d=\"" curve "\" fill=\"none\" stroke=\"#126477\" stroke-width=\"3\"/>"
                "<text x=\"8\" y=\"70\">" (format "%.1f" (double hi)) "</text><text x=\"8\" y=\"240\">" (format "%.1f" (double lo)) "</text>"
                "<text x=\"60\" y=\"265\">" (xml (:date (first observations))) "</text>"
                "<text x=\"590\" y=\"265\">" (xml (:date (last observations))) "</text>"
                "<text x=\"60\" y=\"290\">" (xml (:unit lead)) "; shaded: latest-date historical IQR; normal regime unapproved</text>")
           (apply str (map-indexed
              (fn [i a]
                (let [age (:source-age-days a) py (+ 70 (* i 52))]
                  (str "<text x=\"30\" y=\"" py "\">" (xml (:id a)) "</text>"
                       "<rect x=\"280\" y=\"" (- py 16) "\" height=\"22\" width=\""
                       (min 360 (* 12 (max 0 (or age 0)))) "\" fill=\"#b47c44\"/>"
                       "<text x=\"650\" y=\"" py "\">" (xml (if (domain/finite? age) (str age " days") "unknown age")) "</text>")))
              (take 4 assessments))))
         "<text x=\"25\" y=\"318\" font-size=\"12\">Source coverage and uncertainty constrain interpretation; see report for testable question.</text></g></svg>")))

(defn retained-links [research]
  (let [history (:history research)
        current (set (concat (map :request-id (get-in research [:admission :admitted]))
                             (map :request-id (get-in research [:admission :suppressed]))))
        required (filter #(contains? current (:request-id %)) history)
        additional (->> history (remove #(contains? current (:request-id %)))
                        (sort-by (juxt :day :request-id)) (take-last 6))]
    (concat required additional)))

(defn report-intro [research]
  (let [lead (:lead research)]
    (str "![" (:label lead) "](../../" (or (:chart-path research) "charts/research/lead.svg") ")\n\n"
         "**Establishes:** " (if (= :series (:chart-kind lead))
                                 (str "The source records " (:latest lead) " " (:unit lead) " at its latest observation; "
                                      (:points lead) " finite observations inform this diagnostic window.")
                                 "Canonical measurement coverage is too old, invalid, or insufficient for a current trend claim.")
         "\n\n**Does not establish:** an approved normal regime, true unobserved traffic, deliverability, or a causal explanation.\n\n"
         "**Question:** " (:question lead) "\n\n"
         "**Diagnostic flags:** " (str/join ", " (map name (sort (:flags lead))))
         ". Source age: " (or (:source-age-days lead) "unknown") " days.\n\n"
         "Research attempts this report: " (count (:results research))
         "; retained attempts: " (count (:history research)) ". "
         "Suppressed: " (pr-str (mapv #(select-keys % [:id :reason]) (get-in research [:admission :suppressed]))) ".\n\n"
         (apply str (for [{:keys [request result path]} (:results research)]
                      (str "- `" (:id request) "`: **" (name (:status result)) "**. "
                           (count (:evidence result)) " retained search responses; "
                           (count (:candidates result)) " candidate references. "
                           "[Dossier](../../" path "/result.edn).\n")))
         (when (seq (:history research))
           (str "\nRetained research: "
                (str/join "; " (for [h (retained-links research)]
                                 (str "[`" (:id h) "` " (name (:status h)) "](../../" (:path h) (if (= :interrupted (:status h)) "/request.edn)" "/result.edn)"))))
                ".\n"))
         "\nLiterature metadata supplies leads, not verified experimental findings. Null searches remain valid; failed searches remain failures. "
         "Proposed indicators/tests do not modify production definitions. Foresight/Clio event admission is explicitly pending.\n\n")))
