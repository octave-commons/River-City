# Missingness Has a Type — synthesis research model v0.14

**Artifact ID:** `RC-UC-2026-09-24-v0.14`  
**Date:** 2026-09-24  
**Status:** candidate analytical model; no fitted correction, forecast, hidden-variable score, normalization rule, or production formula  
**Parents:**
- `reports/research/2026-09-24-upstream-freshness-v0.13.md` — upstream publication/freshness branch
- `reports/research/2026-09-24-visibility-selection-v0.13.md` — endogenous visibility branch

**Pinned quantitative baseline:** `octave-commons/River-City@30eb62a41ffe0bf023254506ad234de57705927e`  
**Projection generated-at:** `2026-09-24T14:17:24.579483274Z`  
**Source retrieved-at:** `2026-09-24T14:16:59.256225202Z`  
**Source observed-through:** `2026-09-20`  
**Coverage:** 2019-01-01 through 2026-09-20; 5,640 source rows; 360 projection rows  
**Lineage:** `direct-source-snapshot`; `ledger-backed? false`

## Why this is a synthesis run

Two September 24 research artifacts were independently persisted with the same `RC-UC-2026-09-24-v0.13` identifier and the same v0.12 parent while developing different mechanisms. This artifact does not overwrite either branch or pretend the collision did not happen. It treats both as explicit parents and advances the line to v0.14.

The branches are complementary rather than interchangeable:

- **upstream-freshness branch:** successful local retrieval/projection can be computationally fresh while the upstream observation cutoff remains September 20;
- **visibility-selection branch:** even inside a published date, the visible AIS-backed vessel population can be a selected subset of physical movement, and same-provider publications can revise prior-day counts.

## Question

When a traffic series is missing, stale, low, or later revised, can River City distinguish *which observation mechanism failed* before interpreting the physical system?

## Parent adjudication

### Retained and strengthened — process freshness is not evidence freshness

The September 24 River City projection was generated at 14:17:24Z after a source retrieval at 14:16:59Z, but source observations still stop on September 20. The current adapter has no explicit date ceiling, paginates in 1,000-row pages, and the generated-state commit records 5,640 source features and 5,640 normalized rows. This continues to weaken a simple local date-filter or obvious row-drop explanation.

### Retained and strengthened — visibility is a measurement mechanism

Reuters/Kpler reporting continues to state that tracked counts exclude vessels that transit with AIS/transponders disabled. Same-provider publication state also changes: public Reuters-syndicated reports first described September 21 Hormuz as 2 and later as 4; September 22 was first described as 3 and the September 24 report describes it as 7. Bab el-Mandeb September 22 similarly moved from 22 in the September 23 report to 24 in the September 24 report. These are revision-state observations, not physical movements to average together.

### Consumed — both v0.13 next actions

The upstream-freshness branch requested a direct receipt for the exact IMF ArcGIS layer/query `max(date)`. An independent direct query receipt could not be obtained in this run; the field remains **unavailable**, not inferred.

The visibility-selection branch requested a revision-preserving visible-traffic comparator and an independent cargo/loading proxy. The Kpler publication-state panel was extended through September 23, but no lawful independent same-lane cargo/loadings series with matching daily population and units was obtained in this run. That field also remains **unavailable**.

The null result is useful: neither missing field is silently backfilled from the other mechanism.

## Research increment — typed missingness

v0.14 treats three observation failures as distinct candidate states:

1. **coverage missingness** — the newest available upstream observation date does not advance even though retrieval and projection succeed;
2. **visibility missingness** — physical movement may occur but fall outside the visible AIS-backed sample or source population;
3. **revision missingness** — the provider later changes a previously published same-date value as records arrive, classifications change, or its own reconciliation proceeds.

The candidate observation chain is now:

`physical movement -> visibility/disclosure selection -> upstream publication/availability -> source × lane observation contract -> local retrieval/projection -> publication/revision state -> observed traffic`

The existing deliverability branch remains separate:

`route activation + substitution/buffers + operating friction -> allocation -> recipient-specific delivered service`

No arrow above is a fitted causal coefficient.

### Candidate hypothesis H14T — missingness mechanisms leave different signatures

**Claim.** Coverage lag, visibility selection, and revision lag can often be *classified as competing explanations* from their evidence signatures even when their magnitudes cannot be estimated.

**Expected signatures:**
- coverage missingness: repeated successful retrievals with an unchanged upstream/latest observation date;
- visibility missingness: a published date exists, but independent vessel/cargo evidence or disappear/reappear receipts indicate that the publicly visible set may be incomplete;
- revision missingness: the same provider/date/lane has multiple preserved publication states.

**Current evidence:**
- River City repeatedly regenerates successfully while its canonical cutoff remains September 20;
- Kpler/Reuters explicitly warns that transponder-off vessels can be absent from visible counts;
- Kpler public publication states changed for September 21 and 22;
- September 24 reporting gives September 23 at 10 visible Hormuz vessels and 27 Bab el-Mandeb vessels, while River City's stable canonical series still ends September 20.

**Rival explanations:** source geofence differences, different semantic day boundaries, vessel-class populations, loaded/ballast rules, classification/deduplication, caching, alternate ArcGIS layers/query paths, ordinary data outages, and transcription or normalization error.

**Would weaken H14T:** evidence that these signatures do not distinguish mechanisms in practice — for example, a direct upstream receipt shows dates beyond September 20 while River City's identical query still stops there (moving the diagnosis local), or independent cargo/loadings track visible counts proportionally through the high-risk interval (weakening visibility-selection relevance).

**Would strengthen H14T:** a direct ArcGIS `max(date)` receipt remains September 20 while local retrieval succeeds, followed by a later upstream advance that River City ingests without code change; and separately, an independent cargo/loadings or vessel-identity series shows movement during intervals where visible-AIS counts understate activity.

**Confidence:** high that the three states are conceptually distinct and that revision state must be preserved; medium that current River City staleness is upstream publication lag rather than another upstream contract/query path; low on the magnitude or direction of visibility bias. No numerical probabilities are implied.

## Fresh external tail — preserve revisions, do not splice

Current Reuters-syndicated Kpler reporting on September 24 states:
- **September 23:** Hormuz 10 visible commodity vessels; Bab el-Mandeb 27;
- **September 22 (successor publication state):** Hormuz 7; Bab el-Mandeb 24;
- the September 23 publication had reported September 22 as Hormuz 3; Bab el-Mandeb 22.

The September 24 report again says the counts exclude vessels crossing with transponders off. These values are source-scoped and provisional; they are not merged into River City's canonical IMF series.

The September 20 overlap remains River City/IMF 1 Hormuz / 26 Bab versus later Kpler 10 / 26. The lane-specific pattern still rules out treating a single provider-wide multiplier as established.

## Transmission-channel updates

### Oil logistics

Saudi Arabia's East-West Pipeline restarted September 22 at a reduced, unquantified rate after the September 11 attack. Reuters reports roughly 4 million bpd as recent pre-attack/rerouting throughput and 7 million bpd nameplate capacity, with three pumping stations damaged and full restoration estimated at 6–8 weeks. Restart is an activation-state transition, not proof of full delivered capacity.

### Refined fuels / Ukraine-Russia

U.S. EIA on-highway diesel rose from $5.967/gal on September 7 to $6.285 on September 14 and $6.529 on September 21. Separately, Reuters reports Moscow refinery crude processing halted after the September 20 drone attack, affecting units with 21.4 and 18.8 thousand metric tons/day crude-processing capacity. Capacity exposure is not a realized diesel-loss estimate.

### LNG/gas

Kpler estimated September Asian LNG imports at 20.09 Mt versus 22.27 Mt in September 2025, while European September imports were estimated at 7.98 Mt versus 7.55 Mt in August 2026. Different comparison bases remain explicit; this is regional allocation evidence, not a common stress score.

### AI economics

River City's latest model-watch is observed September 23 and reports 14 material changes with fresh official-source coverage across GPT/OpenAI, Claude/Anthropic, Kimi/Moonshot, GLM/Z.ai, Mistral and MiMo/Xiaomi. Current direct/direct-host list-price observations include GPT-6 Astra/Sol/Luna, Claude Sonnet 5 and Opus 5.5, Mistral Large 3, Mistral-hosted open GLM 5.3, and Xiaomi MiMo-V2.6-Pro. Kimi K3's official platform exposes a CNY-native rate card. No self-host electricity-only floor or price-to-cost residual is produced because issues #14/#15 still lack the required measured throughput, wall power, workload/hardware and accounting inputs.

## AI / agents / software / national-security lens

- GitHub removed Node 20 from Actions runners on September 23; JavaScript actions now execute on Node 24 and the temporary insecure opt-out is gone. This is an immediate compatibility boundary for older actions and unsupported self-hosted platforms.
- GitHub added local sandboxing to the Copilot app and enterprise-managed OpenTelemetry for agent sessions. Filesystem/network/credential authority and execution traces are now first-class operator-configurable controls; local sandboxing is off by default and fails closed when the OS cannot enforce it.
- The UK and U.S. announced collaboration between their rapid-delivery/digital-AI defense offices. This is public institutional/procurement-direction evidence only; no classified deployment, revenue, or operational-effectiveness inference is made.

## Missing views and policy gates

- Conflict/strike tempo by theater/side: unavailable; issue #6 owns structured ACLED/UCDP/FIRMS ingestion.
- Approved normal-regime maritime band: unavailable; issue #3 remains the policy gate.
- Numeric hidden-variable heatmap: unavailable; issue #10 remains the definition/evidence/prior gate.
- API-vs-open-weight self-host floor/residual: unavailable; issues #14/#15 remain the calibration/accounting gates.
- Media-attention trend: unavailable; issue #34 owns accumulation of timestamped attention-proxy observations.

## Exactly one next action

Under existing River City issue #27, capture one **typed-missingness evidence packet** for September 22–23 containing: (a) an exact upstream ArcGIS layer/query receipt with retrieval timestamp, feature count and `max(date)` if obtainable; (b) preserved Kpler publication states for both lanes; and (c) one independent cargo/loading or named-vessel proxy where lawfully available. Any unavailable field remains explicitly unavailable. Do not compute a correction factor.

## Bounded missing series

A revision-preserving **observation-state provenance series** by lane/date/source that carries upstream availability cutoff, retrieval timestamp, publication/revision state, visibility limitation, and an optional independent cargo/loading comparator. This belongs to existing #27; no duplicate issue is needed.

## Implementation boundary

This artifact is generated research output. It does not alter the PortWatch query, production schemas, baseline policy, scoring rules, normalization, or model-economics formulas. Any production implementation change remains reviewed issue/PR work.
