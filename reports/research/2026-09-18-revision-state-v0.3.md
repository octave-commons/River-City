# Usable Capacity — revision state and service continuity v0.3

**Artifact ID:** `RC-UC-2026-09-18-v0.3`  
**Date:** 2026-09-18  
**Status:** candidate analytical model; not a fitted forecast, score, production normalization, or policy  
**Parent:** `RC-UC-2026-09-17-v0.2` (`reports/research/2026-09-17-observation-identity-v0.2.md`)  
**Pinned River City revision:** `b69d9bb9323d042581311ddff79748f32e818136`  
**Pinned PortWatch chart-data blob:** `084c4ee41b4e20f64bcd63e08a9d60447680f286`

## Research question

When a passage series is updated from preliminary shipping data, is **revision state** large enough to change the apparent short-run direction of travel? Separately, can delivered crude service remain partially continuous before the damaged Saudi East-West route is repaired?

## Stable baseline

River City's current PortWatch state was generated at `2026-09-17T17:05:14.108848289Z` from observations through `2026-09-13`. Source retrieval was `2026-09-17T17:04:41.807272364Z`. Coverage spans 2019-01-01 through 2026-09-13, with 5,626 source rows, 360 projection rows, and a 180-day current/history window for Hormuz and Bab el-Mandeb. Lineage remains `direct-source-snapshot`; `ledger-backed? false`.

The latest canonical date remains:
- Hormuz: 8 visible vessels versus descriptive prior-year median 91 (-91.2%).
- Bab el-Mandeb: 31 versus median 61 (-49.2%).

The median and IQR are the same calendar day across prior available years with no conflict exclusions. They are descriptive and provisional, not an approved normal-regime baseline. This model does not change that policy.

## Parent adjudication

### H1R — revision lag can dominate short-run deltas at low counts

**Strengthened, not completed.** The requested seven-day panel is still incomplete, but three consecutive Hormuz observation days now have a preliminary count and a later Reuters/Kpler report:

- 2026-09-14: 4 preliminary -> 7 later reported.
- 2026-09-15: 4 preliminary -> 12 later reported.
- 2026-09-16: 3 preliminary -> 6 later reported.

For Bab el-Mandeb, two matched days revise 21 -> 24 and 22 -> 24; the September 16 value remains 21 in the last report located for that date.

**What is established:** revision provenance can materially alter the apparent size and direction of a daily change in this small low-count sample.

**What is not established:** a general upward bias, a correction factor, or a Kpler-specific error rate. Three Hormuz pairs are not a distribution. Raw vessel identities, exact geofences and internal revision reasons were not obtained.

### September 13 cross-source residual

**Still unresolved.** River City/IMF reports 8 Hormuz / 31 Bab el-Mandeb; Reuters/Kpler later reported 10 / 28 for the same calendar date. This remains cross-source disagreement, not a predecessor/successor revision. No averaging is lawful.

### H2A — substitution can restore service before route repair

**Strengthened.** Trade sources report about 60 million barrels of Saudi crude sold for Ras Tanura-to-Sohar ship-to-ship loading across September and October, corresponding to roughly 1-1.5 million bpd on average. Japan's refining industry separately says adequate crude supplies are secured through November. These observations show adaptation can preserve some downstream service while the East-West Pipeline remains damaged.

**Counterevidence / constraint:** Reuters' September 17 assessment increased the damaged East-West pumping stations from two to three. The pre-attack line moved about 4-5 million bpd. Some repair estimates remain five to six weeks, with earlier partial pumping possible. Record freight and canceled deliveries show the substitution is costly and incomplete.

### H3 — API list price can move independently of useful-compute cost

**Still untested.** Direct list-price observations are current, but River City still lacks the approved workload/hardware/wall-power/PUE/utilization/amortization calibration required for a self-host floor and price-to-cost residual. OpenRouter changes remain cross-checks, not canonical direct-provider repricing.

## New model increment — revision is an observation state

v0.2 separated physical state from the measurement operator. v0.3 makes **observation state over time** explicit:

`physical movement -> source observation -> preliminary publication -> revision/backfill -> later publication`

A dashboard value should therefore be read as a versioned observation with at least source, observation date, publication time, and revision relationship. The preliminary and later-reported values are both evidence about the observation process. Replacing the former with the latter destroys information needed to evaluate reporting latency.

### Candidate mechanism H1D — delayed observability

One plausible explanation for upward revisions at very low visible counts is delayed observability: AIS-dark movement, late vessel classification, day-boundary reconciliation, or backfilled tracking can cause an initial source snapshot to omit movements that later enter the provider's count.

This is **not established**. Alternative explanations include article-level transcription, a changed query time, a changed geofence/population, or provider correction unrelated to delayed AIS visibility.

**Prediction if delayed observability is important:** in a larger source-scoped matched panel, later-reported values should exceed preliminary values more often than they fall, especially when dark routing or intermittent transponder use is documented.

**Would weaken:** a larger panel has roughly symmetric upward/downward revisions, or provider metadata attributes revisions primarily to definition/day-boundary changes rather than delayed detection.

**Current confidence:** low-to-moderate in the mechanism; high that version identity is analytically necessary. These are qualitative assessments, not calibrated probabilities.

## Transmission interpretation

The report keeps theaters and channels separate:

- **Hormuz/Iran:** severe visible traffic impairment plus dark-route/revision uncertainty; sanctions/finance now also affects safe-passage payment infrastructure according to U.S. Treasury allegations.
- **Red Sea/Bab el-Mandeb:** a distinct maritime theater with traffic closer to its recent impaired range, but still below River City's descriptive history.
- **Ukraine/Russia:** refinery attacks feed the refined-fuel channel rather than being merged into Middle East conflict intensity.
- **Crude/logistics:** East-West route damage is partly offset by Sohar ship-to-ship transfers and inventories/supplier diversification.
- **Refined fuels:** YANOS, Syzran, Saratov and Joliet demonstrate conversion/infrastructure constraints that can keep diesel stress high even as crude fear eases.
- **LNG/gas:** high Asian spot prices suppress Asian demand and redirect cargo toward Europe; storage and weather remain separate buffers/risks.
- **AI economics:** public list price is observable; useful-compute cost remains a separate uncalibrated layer.
- **AI infrastructure/security:** grid/interconnect/containment and workflow authority are conditions of usable compute, not model-list-price components by default.

## Countermodels and confounders

1. **Counts are not cargo.** Four visible Hormuz movements can carry more energy than a different four-vessel day; loaded/ballast, vessel size, cargo class and dark traffic matter.
2. **Substitution is not restoration.** Sohar STS flows can preserve service without proving the East-West Pipeline is repaired.
3. **Inventory can mask route failure.** Buyers can maintain refinery runs while drawing stocks or replacing suppliers; service continuity does not imply unchanged cost.
4. **Refinery nameplate is not realized lost output.** A stopped 300 kb/d refinery exposes 300 kb/d of capacity, but product output, stocks and restart paths determine delivered loss.
5. **Gas price is not gas shortage.** High spot LNG can induce demand destruction, fuel switching and cargo reallocation.
6. **API list price is not model cost.** Commercial strategy, batching, cache, routing, regional processing and subsidies can separate list price from production economics.
7. **Media placement is not objective importance.** Homepage position is a sampled editorial-attention proxy only.

## Model delta: v0.2 -> v0.3

1. Converts revision provenance from a metadata caution into an explicit **observation-state trajectory**.
2. Partially adjudicates H1R with three matched Hormuz preliminary/later pairs and two Bab pairs; result strengthens the need for versioned observations but is too small for a bias estimate.
3. Adds candidate H1D, delayed observability, with explicit alternative explanations and a falsifier.
4. Strengthens H2A with a quantified 60-million-barrel Sohar STS adaptation program while preserving the larger 4-5 mbd pre-attack East-West route as a distinct scale.
5. Strengthens the route-damage countercase: three East-West pumping stations are now reported damaged, so adaptation and infrastructure recovery must not be conflated.
6. Leaves H3 and all numeric latent/normalization/self-host formulas unchanged behind existing River City issues.

## Evidence quality notes

- The PortWatch baseline is pinned repository output with explicit lineage and cutoff; it is the stable quantitative baseline for this report.
- Kpler counts are reported through Reuters and remain preliminary/revisable. The provider's raw matched vessel lists/geofence metadata were not available in this run.
- The 60-million-barrel Aramco/Sohar flow is trade-source reporting, not an Aramco public confirmation.
- Refinery outage capacities are nameplate exposure, not a direct measure of diesel barrels lost.
- Reuters LNG import figures are Kpler estimates presented in commentary; the report labels them as market estimates, not official statistics.
- Provider API list prices come from direct official pages where available; River City/OpenRouter price movements are retained only as cross-check alerts.

## Exactly one next action

Under existing River City issue #27, extend the matched preliminary -> later-reported passage panel to seven observation days and preserve publication timestamps; do not wait for raw vessel identities to store the source-scoped revisions, but keep geofence/day-boundary/vessel identity fields explicitly unknown until obtained.

## Bounded missing series / adapter

No duplicate issue is needed. The highest-value missing adapter remains #27's machine-readable, source-scoped, revision-preserving provisional PortWatch tail. Today's result sharpens its acceptance criterion: it must preserve predecessor/successor observation versions rather than only the latest number.
