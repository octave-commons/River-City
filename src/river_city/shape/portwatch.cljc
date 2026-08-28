(ns river-city.shape.portwatch
  (:require [clojure.string :as str]
            [malli.core :as m]))

(def source-id :source/imf-portwatch)
(def event-type :river-city/portwatch-observed)

(def endpoint
  "https://services9.arcgis.com/weJ1QsnbMYJlCHdG/arcgis/rest/services/Daily_Chokepoints_Data/FeatureServer/0/query")

(def out-fields
  ["ObjectId" "date" "portid" "portname"
   "n_container" "n_dry_bulk" "n_general_cargo" "n_roro"
   "n_tanker" "n_cargo" "n_total"
   "capacity_container" "capacity_dry_bulk" "capacity_general_cargo"
   "capacity_roro" "capacity_tanker" "capacity_cargo" "capacity"])

(def nonblank-string
  [:and string? [:fn #(not (str/blank? %))]])

(def source-record-id
  [:or int? string?])

(def number-map
  [:map {:closed true}
   [:container {:optional true} number?]
   [:dry-bulk {:optional true} number?]
   [:general-cargo {:optional true} number?]
   [:roro {:optional true} number?]
   [:tanker {:optional true} number?]
   [:cargo {:optional true} number?]
   [:total {:optional true} number?]])

(def observation-data
  [:map {:closed true}
   [:source/id [:= source-id]]
   [:source/record-id source-record-id]
   [:source/date [:or int? string?]]
   [:port/id [:or int? string?]]
   [:port/name nonblank-string]
   [:vessels number-map]
   [:capacity number-map]])

(defn- attr
  [attrs & names]
  (some (fn [name]
          (or (get attrs name)
              (get attrs (keyword name))))
        names))

(defn- compact-map
  [m]
  (into {} (remove (comp nil? val)) m))

(defn target-chokepoint?
  [port-name]
  (boolean
   (and port-name
        (re-find #"(?i)(hormuz|bab[ -]?el[ -]?mandeb|mandeb)" port-name))))

(defn normalize-attributes
  "Convert one ArcGIS PortWatch attribute map into River City's portable data
   shape. Provider spelling and flat field layout stop at this boundary."
  [attrs]
  {:source/id source-id
   :source/record-id (attr attrs "ObjectId" "OBJECTID" "objectid")
   :source/date (attr attrs "date")
   :port/id (attr attrs "portid")
   :port/name (attr attrs "portname")
   :vessels
   (compact-map
    {:container (attr attrs "n_container")
     :dry-bulk (attr attrs "n_dry_bulk")
     :general-cargo (attr attrs "n_general_cargo")
     :roro (attr attrs "n_roro")
     :tanker (attr attrs "n_tanker")
     :cargo (attr attrs "n_cargo")
     :total (attr attrs "n_total")})
   :capacity
   (compact-map
    {:container (attr attrs "capacity_container")
     :dry-bulk (attr attrs "capacity_dry_bulk")
     :general-cargo (attr attrs "capacity_general_cargo")
     :roro (attr attrs "capacity_roro")
     :tanker (attr attrs "capacity_tanker")
     :cargo (attr attrs "capacity_cargo")
     :total (attr attrs "capacity")})})

(defn valid-observation?
  [value]
  (m/validate observation-data value))

(defn assert-observation!
  [value]
  (when-not (valid-observation? value)
    (throw (ex-info "Invalid PortWatch observation"
                    {:river-city/error :invalid-portwatch-observation
                     :observation value
                     :explain (m/explain observation-data value)})))
  value)
