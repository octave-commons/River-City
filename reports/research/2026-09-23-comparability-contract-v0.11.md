# Comparable Before Correctable — research model v0.11

**Artifact ID:** `RC-UC-2026-09-23-v0.11`  
**Date:** 2026-09-23  
**Status:** candidate analytical model; no fitted cross-source correction, forecast, hidden-variable score, or production normalization  
**Parent:** `RC-UC-2026-09-22-v0.10` (`reports/research/2026-09-22-lane-specific-observation-v0.10.md`)  
**Pinned quantitative baseline:** `octave-commons/River-City@a090baa205258c6fc45d68d2c73c3c7a01e5698e`

## Question

When two maritime observation systems disagree on the same lane and date, what must be known before the difference can be interpreted as measurement error, revision lag, or physical traffic divergence?

## Baseline boundary

The pinned River City manifest was generated at `2026-09-22T17:01:39.680645349Z`. Its PortWatch source was retrieved at `2026-09-22T17:00:56.118549068Z` and is observed through `2026-09-20`. Coverage is 2019-01-01 through 2026-09-20, with 5,640 source rows and 360 projection rows over a 180-day historical window. Lineage is `direct-source-snapshot`; `ledger-backed? false`.

The September 20 canonical checkpoint is Hormuz **1** visible vessel versus prior-year median **91** (-98.9%) and Bab el-Mandeb **26** versus **62** (-58.1%). The median/IQR is descriptive prior-year calendar-day history; no normal-regime exclusion policy is approved. Issue #3 remains the policy gate.

## Parent adjudication

**Consumed in part:** v0.10's next action asked for provider-specific geography/day boundary, vessel classes, publication/revision timestamp and, where public, vessel identities for the September 20 Hormuz `1 vs 10` pair, with Bab `26 vs 26` as control.

**Newly established on the River City / IMF side:** River City's adapter queries IMF PortWatch's `Daily_Chokepoints_Data` ArcGIS FeatureServer for Hormuz/Mandeb and requests `ObjectId,date,portid,portname,n_container,n_dry_bulk,n_general_cargo,n_roro,n_tanker,n_cargo,n_total,capacity`, with `returnGeometry=false`, ordered by date/port/source record. River City preserves source-record identity and fails on conflicting duplicate records. IMF methodology identifies AIS from the UN Global Platform as the underlying source and warns that revisions can arise from AIS updates, methodology changes, and vessel/port coverage changes.

**Still unresolved:** the public Kpler/Reuters report identifies September 21's two visible Hormuz ships and says AIS-dark traffic may be omitted, but it does not publish a complete September 20 vessel-identity list, exact Kpler geofence, or exact semantic day boundary. River City's UTC date normalization is a representation step and does not prove the IMF upstream day boundary. Therefore the September 20 pair remains *not comparable enough for a correction coefficient*.

**Retained:** source x lane identity, revision state, operating-friction/workaround, allocation, activation-state and expected-deliverability branches from prior models.

**Still untested:** API list price versus useful-compute/self-host cost (H3). No electricity-only or all-in self-host range is produced.

## Research increment: the comparability contract

A cross-source delta is not yet a physical delta. Before correcting or interpreting it, compare the observation contracts along at least these conceptual dimensions:

1. **Unit:** vessel count, transit call, cargo tonnage, capacity, or another measure.
2. **Population:** vessel classes and loaded/ballast or commodity inclusion rules.
3. **Geography:** chokepoint/geofence definition and direction semantics.
4. **Time window:** semantic day boundary and observation cutoff.
5. **Visibility:** AIS reception, AIS-dark treatment, and source coverage.
6. **Classification:** category mapping and deduplication rules.
7. **Publication state:** preliminary, revised, backfilled, or method-reprocessed.
8. **Source identity:** provider, dataset/version, query path, and source record where available.

This is a research checklist, not a production schema or numeric score. Any code/config contract belongs in reviewed River City work.

## H10C — Comparable before correctable

**Candidate claim:** a same-lane, same-date provider difference should not be converted into a correction factor unless the relevant observation-contract dimensions are comparable or the residual is shown to persist after aligning them.

### Supporting observations

- September 20 River City/IMF versus later Kpler reporting: Hormuz **1 vs 10**, Bab el-Mandeb **26 vs 26**.
- September 21 Kpler preliminary reporting: Hormuz **2**, Bab **26**; the report identifies two visible Hormuz bulk ships and warns that AIS-dark traffic may be excluded.
- IMF documentation says PortWatch is AIS-derived and revisable. River City exposes the aggregate class-count fields but not vessel identities or source geometry in this adapter.

### Competing explanations

- The September 20 Hormuz mismatch is a provider revision/backfill difference.
- The providers use different geofences or day boundaries.
- Vessel-class or commodity-population rules differ.
- AIS coverage/dark-vessel handling differs by lane.
- One source has a source-specific data or query error.
- The exact Bab count agreement is coincidental and does not imply contract equivalence.

### Discriminating predictions

- **Boundary/lag explanation:** aligning observation windows or later revisions should reduce the Hormuz gap without requiring a stable multiplicative offset.
- **Population/geofence explanation:** raw event/vessel lists should show systematic inclusion/exclusion differences concentrated in one lane or class.
- **Provider-global correction hypothesis:** comparable metadata should produce a similar directional discrepancy across both lanes. The current 1/10 versus 26/26 pattern is weak evidence against assuming that now, but one date is insufficient to reject it.

### Disconfirming evidence

H10C's practical importance would weaken if a provider publishes a stable, documented mapping to IMF's unit/population/geography/time/visibility contract and repeated same-day comparisons reconcile without contract metadata affecting interpretation. A single numerical match does not provide that evidence.

**Confidence:** high that comparability metadata is required before fitting a cross-source correction; low on the cause of the September 20 Hormuz discrepancy. No numerical probability is asserted.

## Cross-domain check: API prices also have contracts

The same conceptual distinction appears in AI economics. GPT-6 Astra, Claude Opus 5.5, Mistral Large 3 and MiMo-V2.6-Pro have directly published token prices, but a price comparison still depends on processing lane, cache state, context tier, region, batch/priority mode, provider/host and model version. Equality or difference in $/M tokens is not automatically equality or difference in useful-compute cost. River City's model-watch rule that direct-provider facts are canonical and OpenRouter is only a cross-check is consistent with H10C.

## Current system evidence retained outside H10C

- Hormuz remains severely impaired in River City's canonical September 20 snapshot; Kpler preliminary September 21 traffic was two visible vessels, while Bab remained 26.
- Saudi Arabia restarted the East-West Pipeline on September 22 at a reduced, unquantified rate; Reuters reports approximately 4 mb/d rerouting use before the attack and 7 mb/d full capacity, with three pumping stations damaged and 6–8 weeks cited for full restoration. Restart state is not full restoration.
- The Moscow refinery halted crude processing after the September 20 drone attack. Reuters reports 21,400 t/day for AVT-6 and 18,800 t/day for EURO+; those are exposed crude-unit capacities, not a calculated diesel loss.
- Kpler estimates September Asian LNG imports at 20.09 Mt versus 22.27 Mt a year earlier, while European imports rise to 7.98 Mt from 7.55 Mt in August. Gas allocation remains separate from crude/refined-fuel channels.
- Claude Opus 5.5 launched at $4/M input and $20/M output. GitHub added Opus 5.5 and GPT-6 Sol/Luna to Copilot on September 22. These are product/lifecycle facts; vendor benchmark claims are not promoted to independent performance conclusions here.
- Dassault reports two AI algorithms flight-tested on Rafale and eligible for possible future upgrades. This is an observable defense-capability-development proxy, not evidence of classified deployment, effectiveness, or revenue.

## One next action

Under existing River City issue #27, capture a **single comparable raw-event packet** on the first future day where a public Kpler report names Hormuz vessels: preserve the named vessel identities/classes and publication timestamp, then compare them with the River City/IMF same-date class totals and source-record IDs. If the IMF aggregate cannot expose vessel identities or the Kpler contract remains incomplete, record the result as `not comparable`; do not infer missing identities or fit a correction.

## Bounded missing series

A revision-preserving **provider x lane observation-contract panel**: date, provider, lane, unit, class population, geography/geofence identity when public, semantic day boundary when public, visibility/AIS policy, publication/revision timestamp, source record, and raw count. Existing #27 owns the observation/freshness problem; no new issue is required for today's model.

## Implementation boundary

This artifact is generated research output. It does not create a production schema, a correction factor, a hidden-variable score, a new baseline rule, or analytical code. Rendering scripts in the publication bundle only plot source values and do not smooth, impute, normalize, fit, or score them.
