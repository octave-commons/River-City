;; River City - geopolitical, energy, and AI infrastructure observability
;; Copyright (C) 2026 River City contributors
;; SPDX-License-Identifier: GPL-3.0-or-later

(ns river-city.infra.cli
  (:require [river-city.infra.portwatch :as portwatch]))

(defn- report-result [command result]
  (println (pr-str {:river-city/command command
                    :river-city/status :ok
                    :result result})))

(defn -main [& [command]]
  (case command
    "ingest" (report-result :ingest (select-keys (portwatch/ingest!)
                                                  [:retrieved-at :page-count :feature-count]))
    "normalize" (report-result :normalize (select-keys (portwatch/normalize!)
                                                        [:generated-at :row-count]))
    "score" (report-result :project (select-keys (portwatch/project!)
                                                  [:generated-at :coverage :baseline]))
    "render" (report-result :render (portwatch/render!))
    "daily" (report-result :daily (portwatch/daily!))
    (throw (ex-info "Unknown River City command"
                    {:river-city/error :unknown-command
                     :command command
                     :supported ["ingest" "normalize" "score" "render" "daily"]}))))
