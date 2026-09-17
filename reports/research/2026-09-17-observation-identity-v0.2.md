# Usable Capacity — observation identity v0.2

**Artifact ID:** `RC-UC-2026-09-17-v0.2`  
**Date:** 2026-09-17  
**Status:** candidate analytical model; not a fitted forecast, score, or production policy  
**Parent:** [`RC-UC-2026-09-16-v0.1`](https://github.com/octave-commons/River-City/blob/2019945011c68ae3880110fccffabd3959fa0f85/reports/research/2026-09-16-usable-capacity-v0.1.md)  
**Pinned quantitative baseline:** `octave-commons/River-City@2019945011c68ae3880110fccffabd3959fa0f85`

## Research question

When a dashboard shows a very low passage count, how much of the change belongs to the physical system and how much belongs to the observation process? When physical route capacity is damaged, which substitutions preserve downstream service before infrastructure is repaired?

## Baseline boundary

River City's current PortWatch manifest was generated at `2026-09-16T17:23:11.706793255Z` from source observations through `2026-09-13`. The lineage remains `direct-source-snapshot`, `ledger-backed? false`. At the last canonical date, Hormuz is 8 visible vessels against a provisional prior-year median of 91 (-91.2%); Bab el-Mandeb is 31 against 61 (-49.2%). The historical median/IQR remains descriptive, not an approved normal-regime model.

The source adapter identifies IMF PortWatch and requests `n_total`, `n_cargo`, `n_tanker` and component vessel classes for ports matching Hormuz or Mandeb. River City's displayed quantity is therefore an upstream observation product, not a direct measure of delivered barrels, tonnes, insurance availability or service continuity.

## Parent adjudication

**Retain — stronger:** nominal capacity and delivered service are different observables. Today's combination of very low Hormuz movement counts and active Saudi/Omani substitution is direct evidence that route impairment and downstream supply adaptation can coexist.

**Strengthen H1 in general:** observation revision state is now demonstrated to matter materially. A September 16 Reuters/Kpler report described September 15 Hormuz traffic as 4 and Bab el-Mandeb as 22. The September 17 report described the same prior day as 12 and 24. The Hormuz change is large enough to reverse the apparent day-to-day story if a dashboard overwrites the preliminary value.

**H1 specific September 13 discrepancy remains unresolved:** River City/IMF records 8 Hormuz and 31 Bab el-Mandeb on September 13; Reuters/Kpler reported 10 and 28. The River City side is now better specified: AIS-derived IMF PortWatch, completed daily chokepoint observations, vessel-class fields, and a canonical source record. This run did not obtain Kpler's raw September 13 vessel list, exact geofence, day boundary, or revision receipt. The difference therefore cannot yet be attributed to provider error, timezone, geofence, vessel population, or revision timing.

**Revise H2:** shared route constraints still reduce resilience, but physical route diversity is only one component of resilience. Saudi ship-to-ship loadings off Oman and Orlen's purchase of 16 additional cargoes demonstrate an adaptive substitution layer. This can preserve service for some buyers before a damaged pipeline returns. It does not establish complete global replacement.

**H3 remains untested:** public API prices are observable, but River City's electricity-only and all-in self-host floor remains blocked by the calibration and accounting decisions in issues #14 and #15. No provider internal cost is inferred.

## New model increment — observation identity before residuals

The model now separates the **physical system** from a **measurement operator**.

`physical state -> delivered service`

`physical state -> measurement operator -> dashboard observation`

The measurement operator includes, where applicable: source population, vessel class, loaded/ballast treatment, AIS visibility, geofence, day boundary, publication time and revision state. These are not causal explanations for the physical system. They determine what the dashboard has actually observed.

A comparison is **not yet physically interpretable** when these identities are materially incompatible or unknown. This is a conceptual gate, not a score or production normalization rule.

A second branch represents **adaptive substitution**: inventory, alternative suppliers, alternate load points, ship-to-ship transfers and demand response can feed delivered service without restoring the original route. Those mechanisms must be observed separately rather than treated as proof that the original infrastructure has recovered.

## Evidence packet

### Same-source revision

- September 16 Reuters/Kpler report for September 15: Hormuz 4, Bab el-Mandeb 22.
- September 17 Reuters/Kpler report for the same September 15 day: Hormuz 12, Bab el-Mandeb 24.
- September 17 preliminary September 16: Hormuz 3, Bab el-Mandeb 21.

**Implication:** preliminary-to-revised movement can be large at low counts. Preserving the first report and its later replacement is analytically useful.

### Cross-source comparison

- River City/IMF September 13: Hormuz 8; Bab el-Mandeb 31.
- Reuters/Kpler September 13: Hormuz 10; Bab el-Mandeb 28.

**Known:** both sources describe commercial/commodity maritime movement around the same chokepoints. River City's fields and upstream source are inspectable.

**Unknown:** exact Kpler geofence, UTC/local day boundary, raw vessel identities, revision state for the September 13 value, and one-to-one match against PortWatch records.

**Conclusion:** the discrepancy is preserved as unresolved measurement disagreement. It is not averaged.

### Adaptation counterevidence

Saudi Arabia offered more crude via ship-to-ship transfer off Sohar, Oman, after East-West Pipeline disruption. Orlen separately bought 16 additional cargoes from other suppliers and stated its refinery demand remained covered. These observations weaken a simple model in which one disabled route maps one-for-one into buyer shortage.

Crude prices also fell on September 17 as markets incorporated substitution and possible pipeline recovery, while diesel remained unusually stressed. That divergence is consistent with conversion/product constraints and substitution acting at different layers; it is not by itself proof of any one mechanism.

## Prospective tests

### H1R — revision lag can dominate short-run deltas at low counts

**Prediction:** if provisional counts are routinely revised, a revision-preserving seven-day panel will contain non-zero same-day changes large enough to alter some apparent day-to-day deltas.

**Would weaken:** a source-scoped panel shows nearly all preliminary values identical to subsequent values, and raw vessel identities explain the rare changes as obvious transcription errors rather than normal revision behavior.

**Current confidence:** moderate that revision state matters; low confidence about its typical magnitude because only a small number of revision pairs are available.

### H2A — substitution can restore service before route repair

**Prediction:** for buyers with diversified supplier and loading options, replacement cargo or inventory receipts can remain adequate before the East-West Pipeline's operational restart.

**Would weaken:** documented buyer cancellations, stock drawdowns or refinery run cuts persist despite advertised alternate cargoes and load points.

**Current confidence:** moderate that substitution is active; low-to-moderate that it is sufficient across regions and products.

### H3 — API list price can move independently of useful-compute cost

**Status:** untested this run. River City recorded Kimi/GLM/Mistral cross-check and lifecycle changes, but cross-check prices are not canonical provider rates. Required hardware/workload calibration remains unresolved.

## Countermodels and confounders

- A lower vessel count can coincide with larger average cargoes; count and tonnage need not move proportionally.
- AIS-dark movement can make visible traffic understate actual movement.
- A buyer can be supplied through inventory or alternative suppliers even while the original route is impaired.
- Lower crude prices can reflect expectations, demand, inventory and policy as well as physical supply.
- High diesel margins can reflect refinery/product constraints rather than crude scarcity alone.
- Low gas storage does not determine winter shortage without demand, weather, import and injection paths.
- AI list prices reflect commercial strategy and packaging as well as production economics.

## Model delta summary

**v0.1 -> v0.2**

1. Adds an explicit observation/measurement layer between physical state and dashboard values.
2. Elevates revision provenance from a source note to a first-class research variable.
3. Preserves the September 13 IMF/Kpler disagreement as unresolved rather than interpreting the residual.
4. Revises route resilience to include adaptive substitution, not only alternate physical paths.
5. Leaves numeric scores, normalization rules and self-host residuals unmodified and behind existing River City issues.

## Exactly one next action

Under existing River City issue #27, accumulate a seven-day paired **preliminary -> later-reported** Kpler passage panel with publication timestamps and, where lawfully available, vessel identities/geofence/day-boundary metadata; then adjudicate H1R without overwriting the preliminary observations.

## Bounded missing adapter

The missing adapter is already tracked by #27: a machine-readable, source-scoped, revision-preserving provisional PortWatch tail. No duplicate issue is needed.
