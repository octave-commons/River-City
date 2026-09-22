# Selective Observation - research model v0.10

**Artifact ID:** `RC-UC-2026-09-22-v0.10`  
**Date:** 2026-09-22  
**Status:** candidate analytical model; not a fitted forecast, score, index, correction factor, normalization rule, or production formula  
**Parent:** `RC-UC-2026-09-22-v0.9` - `reports/research/2026-09-22-expected-deliverability-v0.9.md`  
**Pinned quantitative baseline:** `octave-commons/River-City@0550cee444d4ba29525f25e68bcd4420492dff81`  
**Baseline generated-at:** `2026-09-22T13:55:57.984139342Z`  
**PortWatch source retrieved-at:** `2026-09-22T13:55:28.561381597Z`  
**PortWatch source-observed-through:** `2026-09-20`  
**Lineage:** `direct-source-snapshot`  
**Ledger-backed:** `false`

## Question

When two public observation systems agree on one chokepoint but disagree sharply on another on the same date, what does that imply about the observation model River City should preserve before interpreting a traffic move?

## Parent adjudication

**Consumed:** v0.9's sole next action. Existing issue #33 now contains a stage-qualified East-West Pipeline recovery observation: September 13 shutdown; September 22 restart at a reported low rate; 4 million bpd target; current numeric throughput unknown; full-restoration timing uncertain. No guessed throughput was inserted.

**Strengthened but not quantified:** H8E, expected future deliverability can move a commodity benchmark before current physical normalization. Reuters reports Brent November at $98.23/bbl, down 2.1% intraday on September 22, while Saudi's bypass had only restarted at a low unquantified rate and Hormuz visible traffic remained sparse. That timing is consistent with H8E but does not identify a causal coefficient or isolate diplomacy, macro demand, inventories, or other supply expectations.

**Revised:** the earlier observation-identity work is now explicitly **source x lane** rather than provider-global. River City's September 20 IMF-derived counts are Hormuz 1 and Bab el-Mandeb 26. Reuters/Kpler reports September 20 as Hormuz 10 and Bab el-Mandeb 26. Exact agreement on Bab and sharp disagreement on Hormuz on the same date weakens any assumption that a single global provider offset, publication lag, or day-boundary correction is already justified.

**Retained:** H5F/H6I. High-friction workarounds can preserve delivered quantity before costs normalize. Gulf of Oman ship-to-ship loading is reported near 2.5 million bpd in September versus 1.4 million bpd in August, with benchmark Gulf-to-China VLCC freight above $30/bbl.

**Retained:** H7B. Commercial price basis mediates burden incidence. The prior search did not recover a same-cargo numeric producer netback matched to the freight observation, so no burden share is inferred.

**Still untested:** H3, public API price versus useful-compute/self-host cost. River City #14/#15 still block an approved electricity-only or all-in floor.

## Model delta - make observation identity lane-scoped

v0.9 separated current deliverability from expected future deliverability. v0.10 inserts a lane-scoped observation function before River City interprets a measured traffic state:

`physical movement -> source x lane observation contract -> publication/revision state -> observed traffic state`

`observed traffic state + route state -> current usable/delivered service`

`recovery/substitution options -> expected future deliverability -> market benchmark`

`operating friction + contract/price basis -> burden incidence -> allocation -> recipient-specific delivered service`

No coefficient connects these branches in this artifact.

## H9L - measurement disagreement can be lane-specific

### Candidate claim

A source-to-source difference should not be treated as a stable provider correction unless the relevant observation contract is shown to be comparable for the same lane. Geography, day boundary, vessel population, loaded/ballast treatment, AIS visibility, classification, query timestamp, backfill and revision state can differ by lane or event.

### Current evidence

For **2026-09-20**:

- River City / IMF PortWatch: **Hormuz 1**, **Bab el-Mandeb 26** visible vessels.
- Reuters/Kpler: **Hormuz 10**, **Bab el-Mandeb 26** visible commodity vessels.
- River City describes its historical comparator as provisional observed-history, not an approved normal-regime baseline.
- Reuters explicitly warns that AIS-dark vessels may be absent from Kpler's visible count.
- Provider-specific vessel identities, exact geofences/day boundaries, and revision receipts for the September 20 pair were not recovered in this run.

The disagreement is therefore **observed**; its cause is **unresolved**.

### Discriminating predictions

- If one global publisher/day-boundary offset dominates, the same correction should improve correspondence across both chokepoints over a matched multi-day panel.
- If lane-specific observation contracts dominate, mismatch should cluster by chokepoint/source even when another lane agrees on the same date.
- If a simple stale-source explanation dominates, later revision/backfill should cause the originally divergent lane to converge without an equivalent rule being necessary for the already-matching lane.
- If raw movement identities can be recovered, mismatches should decompose into specific extra/missing movements rather than requiring a scalar correction.

### What would weaken H9L

- A single documented global time-boundary or publication-lag correction reconciles both lanes across a sufficiently long paired panel.
- Later source revisions show that the September 20 Hormuz values were not simultaneously valid observations.
- The providers are shown to use identical vessel populations, geography, day boundaries and revision timing and the mismatch is attributable to a simple data error.

### Countermodels and confounders

- A one-day pair is too small to establish a recurring lane-specific pattern.
- The exact Bab agreement could be coincidental even if the same global timing difference exists.
- AIS-dark traffic affects visibility but does not by itself explain why two visible-vessel providers differ.
- River City and Kpler may classify vessel/cargo categories differently even when both use AIS-derived movement data.

### Confidence

**High confidence that a same-date cross-source discrepancy exists in Hormuz and exact count agreement exists in Bab el-Mandeb. Low confidence in the mechanism causing that selective difference.** No probability, correction factor, reconciliation weight, or source ranking is estimated.

## Regime update from the new canonical baseline

River City's stable state has materially advanced from the parent model's September 13 cutoff to **September 20**:

- **Hormuz:** 1 visible vessel versus descriptive prior-year median 91; -98.9%.
- **Bab el-Mandeb:** 26 versus median 62; -58.1%.

The new canonical tail itself therefore remains an extreme-impairment regime. A later external Kpler value should not silently replace it because the same-date observation contract is not yet reconciled.

## Expected-deliverability branch - retained with stronger staging evidence

Saudi Arabia restarted the East-West Pipeline on September 22 after the September 13 shutdown. Reuters reports a **low rate** and a **4 million bpd target**; current numeric throughput is not public in the cited source. Brent November fell to **$98.23/bbl**, down **2.1%** intraday, while Reuters linked the market move to the improved Gulf supply outlook, including the pipeline restart and a possible Hormuz reopening. This supports preserving `shutdown -> low-rate restart -> target/full-rate` as distinct states. It does not prove the pipeline restart alone caused the price move.

## Operating-friction and gas allocation branches

- Gulf of Oman STS crude loading: about **2.5 million bpd in September** versus **1.4 million bpd in August**, with Gulf-to-China VLCC freight reported above **$30/bbl**. Continuity is being purchased through extra handling and scarce tanker capacity rather than normal-route restoration.
- Kpler estimates **Asia September LNG imports at 20.09 Mt**, down from **22.27 Mt a year earlier**, while Europe is estimated at **7.98 Mt in September** versus **7.55 Mt in August**. That is recipient/region allocation evidence in native tonnage units; it is not a global LNG stress score.

## AI, agents, open source, software infrastructure, defense/security

- **Public API price observation:** direct/official sources currently list GPT-6 Astra at $10 input/$50 output per 1M tokens; Claude Sonnet 5 at $2/$10; Mistral Large 3 at $0.50/$1.50; Mistral-hosted Z.ai GLM 5.3 at $1.40/$4.40. These are list-price observations, not useful-compute cost or provider margin.
- **Open-weight calibration lane:** Mistral Large 3 is an open-weight reference point, but the report does not calculate an electricity-only self-host floor without measured throughput/wall power and the approved #14/#15 accounting decisions.
- **Coverage gaps:** direct numeric Kimi K3, MiniMax PAYG, and MiMo pricing are not silently substituted with aggregator or subscription-unit proxies in this run.
- **Agent/cyber productization:** Palo Alto Networks announced Unit 42 Continuous Frontier AI Defense using frontier and open-weight models. This is observable commercial deployment of AI security testing, not evidence of comparative operational superiority.
- **Defense-specific capability:** Dassault reported flight-testing two AI algorithms on Rafale; operational roles were not disclosed. No classified deployment, effectiveness or revenue is inferred.
- **Software deadline:** GitHub removes Node 20 from Actions runners on 2026-09-23. Workflows/actions that still require Node 20 need compatibility verification; this is a lifecycle fact, not a claim that River City itself is affected.

## Missing views / explicit blockers

- **Conflict/strike tempo:** #6 remains open for ACLED/UCDP structured events plus FIRMS corroboration; headline counting is not substituted.
- **Approved maritime normal-regime band:** #3 remains the baseline-policy gate.
- **API versus open-weight/self-host floor:** #14/#15 still require measured tokens/sec, wall power, representative hardware/workload, PUE, utilization, amortization and regional power mapping.
- **Numeric hidden-variable heatmap:** #10 remains the definitions/evidence/prior policy gate.
- **Media-attention trend:** #34 remains the accumulation adapter; a one-snapshot homepage classification is not promoted into a trend.
- **Source x lane observation comparison:** existing #27 owns the provisional/revision-preserving source-comparison adapter; no duplicate issue is needed.

## Evidence

- River City baseline: `projections/manifest.edn`, `projections/portwatch/latest.edn`, `charts/portwatch/data/latest.json`, `reports/daily/latest.md` at `0550cee444d4ba29525f25e68bcd4420492dff81`.
- Parent model: `reports/research/2026-09-22-expected-deliverability-v0.9.md`.
- Reuters, 2026-09-22, *Hormuz vessel traffic falls to two, data shows*: https://www.reuters.com/world/middle-east/hormuz-vessel-traffic-falls-two-data-shows-2026-09-22/
- Reuters, 2026-09-22, *Saudi Arabia restarts East-West oil pipeline, sources say*: https://www.reuters.com/business/energy/saudi-arabia-restarts-east-west-oil-pipeline-resume-exports-yanbu-sources-say-2026-09-22/
- Reuters, 2026-09-22, *Oil falls to two-week low as Gulf supply outlook improves*: https://www.reuters.com/business/energy/oil-rises-slightly-ahead-potential-us-iran-talks-2026-09-22/
- Reuters, 2026-09-21, *Hormuz shuttles keep oil flowing, but at a high cost*: https://www.reuters.com/commentary/reuters-open-interest/hormuz-shuttles-keep-oil-flowing-high-cost-2026-09-21/
- Reuters, 2026-09-17, *LNG spot price surge deters Asian buyers, but saves Europe*: https://www.reuters.com/commentary/reuters-open-interest/lng-spot-price-surge-deters-asian-buyers-saves-europe-2026-09-17/
- OpenAI GPT-6 Astra: https://developers.openai.com/api/docs/models/gpt-6-astra
- Anthropic Claude Sonnet 5: https://www.anthropic.com/claude/sonnet
- Mistral pricing: https://docs.mistral.ai/inference/pricing
- Mistral-hosted Z.ai GLM 5.3: https://docs.mistral.ai/models/zai-glm-5-3
- GitHub Node 20 runner removal: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/

## Exactly one next action

Under existing River City **#27**, reconcile the **September 20 Hormuz pair (River City/IMF 1 vs Kpler 10)** by collecting provider-specific geography/day boundary, vessel classes, publication/revision timestamp and, where publicly available, vessel identities; perform the same metadata capture for the **Bab 26 vs 26** control lane. Record `not comparable` for unavailable fields rather than fitting a correction factor.
