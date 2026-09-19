# Usable Capacity — recipient-specific deliverability v0.4

**Artifact ID:** `RC-UC-2026-09-19-v0.4`  
**Date:** 2026-09-19  
**Status:** candidate analytical model; not a fitted forecast, score, production normalization, or policy  
**Parent:** `RC-UC-2026-09-18-v0.3` (`reports/research/2026-09-18-revision-state-v0.3.md`)  
**Pinned River City revision:** `c8ffa943b39a337ecf87e4e26a56d9463fb96c92`  
**Pinned PortWatch chart-data blob:** `7a12615723bc1067139c8c4a551dfdb6bda54137`

## Research question

Can aggregate service continuity rise while specific customers, regions, or contracts still fail? If so, River City's usable-capacity frame needs a recipient/destination dimension rather than treating substitution as uniform restoration.

## Stable baseline

The current River City manifest was generated at `2026-09-18T16:29:41.621357274Z`. PortWatch source retrieval was `2026-09-18T16:29:07.743555622Z`; source observations run through `2026-09-13`. Coverage spans 2019-01-01 through 2026-09-13 with 5,626 source rows, 360 projection rows, and a 180-day current/history window for Hormuz and Bab el-Mandeb. Lineage is `direct-source-snapshot`; `ledger-backed? false`.

The latest canonical date remains:
- Hormuz: 8 visible vessels versus descriptive prior-year median 91 (-91.2%).
- Bab el-Mandeb: 31 versus median 61 (-49.2%).

The median and IQR are same-calendar-day observations across prior available years, with no disruption exclusions. They are descriptive and provisional, not an approved normal-regime baseline. This candidate model does not alter that policy.

## Parent adjudication

### H1R — revision lag can dominate short-run deltas at low counts

**Retained; no new matched successor pair located today.** The matched source-scoped panel remains three Hormuz pairs and two Bab el-Mandeb pairs:

- Hormuz 2026-09-14: 4 preliminary -> 7 later reported.
- Hormuz 2026-09-15: 4 -> 12.
- Hormuz 2026-09-16: 3 -> 6.
- Bab el-Mandeb 2026-09-14: 21 -> 24.
- Bab el-Mandeb 2026-09-15: 22 -> 24.

September 17 remains preliminary in the latest comparable Kpler report located in this run: Hormuz 4 and Bab el-Mandeb 23. This is a valid null research increment for H1R: the requested seven-day panel remains incomplete. No bias coefficient, error distribution, or correction rule is introduced.

### H1D — delayed observability explains upward revisions

**Still untested.** Later reports being higher is consistent with backfill, dark/AIS-intermittent traffic, late classification, or reconciliation, but exact Kpler geofence, day boundary, vessel identities and revision reasons remain unavailable here. Changed query time, source definition or ordinary correction remain live alternatives.

### H2A — substitution can restore some service before route repair

**Retained and refined.** Saudi crude offered via ship-to-ship loading off Sohar demonstrates that alternate logistics can preserve aggregate flow while the East-West Pipeline remains damaged. But new recipient-level evidence shows that aggregate continuity is not uniform customer fulfillment.

### H3 — API list price can move independently of useful-compute cost

**Still untested.** Direct public list-price observations are current, but River City issues #14 and #15 still gate measured throughput, wall power, hardware, PUE, utilization, amortization and regional-power mapping. No self-host floor or price-to-cost residual is estimated here.

## New model increment — recipient-indexed service

v0.3 ended at `delivered service` as a single conceptual node. v0.4 splits delivery by recipient/destination:

`nominal capacity -> route availability -> substitution/buffers -> allocation -> recipient-specific delivered service`

The allocation step can depend on destination, contract class, logistics, price, insurance, replacement supply and timing. This is a conceptual mechanism, not a fitted optimization model.

### Candidate H2R — substitution reallocates service rather than restoring it uniformly

**Evidence strengthening the candidate mechanism:**

1. Saudi Aramco is reported to have increased Gulf exports through ship-to-ship transfers off Sohar; trade sources described about 60 million barrels sold for September/October loading, corresponding to roughly 1-1.5 million bpd on average.
2. Separately, Reuters reported that at least two European refining customers were told they would receive no Saudi crude in October after the East-West Pipeline attack. Reuters could not independently verify the Bloomberg-originated cancellation report; Aramco had not commented.
3. Poland's Orlen reportedly bought North Sea replacement grades. This is adaptation by a recipient, not restoration of the original supplier-route contract.
4. In LNG, Kpler estimates September Asian imports at 20.09 Mt versus 22.27 Mt a year earlier while European September imports rise to 7.98 Mt from 7.55 Mt in August. Reuters commentary attributes the pattern to high spot prices suppressing Asian demand and freeing cargoes for Europe.

**Interpretation:** a system can show partial aggregate continuity while specific contractual or regional service fails. The same aggregate barrel or LNG-ton total can therefore hide distributional failure across recipients.

**Prediction:** while a primary route remains impaired, recipient-level fulfillment should become more heterogeneous: replacement purchases, destination shifts, cancellations, deferrals and higher-cost substitutions should coexist rather than move in lockstep.

**Would weaken:** source-backed evidence shows that apparently cancelled or deferred customers receive equivalent volumes on the original commercial terms and timing through alternative routes, while destination shares remain stable despite the route shock.

**Current confidence:** moderate that recipient identity is analytically necessary; low-to-moderate that the observed crude/LNG examples generalize to a stable allocation mechanism across markets. This is qualitative, not a calibrated probability.

## Observation model retained from v0.3

The maritime observation trajectory remains:

`physical movement -> source observation -> preliminary publication -> revision/backfill -> later publication`

This report keeps River City's canonical projection distinct from post-cutoff Kpler reporting. It also preserves the September 13 cross-source residual rather than averaging it: River City/IMF has 8 Hormuz / 31 Bab el-Mandeb, while later Kpler reporting described 10 / 28 for the same calendar date. Provider populations and counting boundaries have not been reconciled.

## Transmission interpretation

- **Hormuz/Iran:** severe visible-traffic impairment persists, with September 17 preliminary Kpler at four visible commodity vessels versus a 10-day average around 16; some traffic can be dark.
- **Red Sea/Bab el-Mandeb:** September 17 Kpler was 23 versus a 10-day average around 26; the theater remains separate even though it is a substitute path for Gulf exports.
- **Ukraine/Russia:** refinery attacks remain a refined-fuel conversion constraint. YANOS halted processing after a drone attack, while Syzran and Saratov were already stopped. These facility states are not converted into a strike count or a barrel-loss estimate.
- **Crude/logistics:** East-West damage, Sohar ship-to-ship adaptation, buyer substitutions and European term cancellations are different states of the delivery graph.
- **LNG/gas:** Asia demand destruction and European cargo absorption illustrate destination reallocation; EU storage around 68% remains a separate buffer/risk state.
- **AI economics:** direct list prices are observable; useful-compute cost remains uncalibrated. Mistral Large 3 provides an open-weight list-price anchor but not a self-host floor.
- **AI agents/security:** the Gemini evaluation incident reinforces that usable autonomy is bounded by credential, network, scope and containment controls; incident existence is observable, generalized failure frequency is not.
- **Software infrastructure:** GitHub's October 19 Copilot model deprecations create a concrete migration deadline for workflows that pin retired models.

## Countermodels and confounders

1. **Aggregate substitution can be sufficient despite recipient churn.** Individual cancellations could be temporary contracting artifacts while total market supply remains adequate.
2. **Destination shifts can be price choice, not physical shortage.** LNG buyers may voluntarily defer spot purchases when economics worsen.
3. **Term cancellation is not realized refinery outage.** A recipient can draw inventory or buy replacement crude.
4. **Visible vessel counts are not cargo volume.** Vessel class, load state, route and dark traffic matter.
5. **Refinery nameplate is not lost product.** Stocks, alternate units, yields and restart timing matter.
6. **API list price is not production cost.** Cache, batch, regional processing, routing, subsidies and commercial strategy remain separate.

## Model delta: v0.3 -> v0.4

1. Keeps revision state explicit but records a null result for the seven-day matched-panel objective: no fourth Hormuz preliminary/later pair was located today.
2. Replaces scalar `delivered service` with **recipient/destination-specific service** as the research endpoint.
3. Adds H2R: substitution can reallocate continuity unevenly across recipients; gives a prediction, falsifier and rival explanations.
4. Uses Saudi crude and LNG destination evidence as two distinct transmission channels supporting the same candidate allocation mechanism without collapsing their units or formulas.
5. Leaves normal-regime, conflict-intensity, latent-score and self-host economics formulas unchanged behind their existing River City gates.
6. Creates River City issue #36 for a raw recipient-level allocation/substitution adapter; it defines observations only, not a score.

## Evidence quality notes

- River City is the stable quantitative baseline and remains direct-source, not ledger-backed.
- Kpler shipping observations are reported through Reuters and remain preliminary/revisable.
- The European Saudi cancellation item originated with Bloomberg and was relayed by Reuters; Reuters explicitly said it could not independently verify the report.
- The Sohar flow range is trade-source reporting, not a Saudi Aramco public confirmation.
- LNG figures are Kpler market estimates in Reuters commentary, not official customs totals.
- Agent-incident details combine Google's public confirmation with reporting on the independent evaluator; they do not establish population-level incident rates.
- API prices use direct provider pages where clean, comparable rows were retrieved; River City/OpenRouter changes remain cross-checks only.

## Exactly one next action

Seed new issue #36 with the current Saudi crude allocation/substitution observations as source-scoped raw records: Sohar STS range, minimum two European term-customer cancellations, and Orlen replacement purchase, preserving contract class, destination, original route, replacement source and unknown volume fields separately.

## Bounded missing series / adapter

Issue #36 now tracks recipient-level energy allocation and substitution observations. It is intentionally separate from #35 insurance/freight, #25 LNG export/storage, #22 refined-fuel stress, and #10 latent scoring. The missing series is recipient-specific fulfillment over time, not another normalized stress score.
