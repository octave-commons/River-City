(ns river-city.infra.cli)

(defn -main [& [cmd]]
  (case cmd
    "ingest" (println "TODO: ingest configured sources")
    "normalize" (println "TODO: normalize raw observations")
    "score" (println "TODO: compute signals, compounds, latents")
    "render" (println "TODO: render Vega-Lite + daily briefing")
    (throw (ex-info "Unknown command" {:cmd cmd}))))
