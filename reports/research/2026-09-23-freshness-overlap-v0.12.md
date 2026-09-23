# Overlap Before Reconciliation - research model v0.12

**Artifact ID:** `RC-UC-2026-09-23-v0.12`  
**Date:** 2026-09-23  
**Status:** candidate analytical model; no fitted correction, forecast, hidden-variable score, normalization rule, or production formula  
**Parent:** `RC-UC-2026-09-23-v0.11` (`reports/research/2026-09-23-comparability-contract-v0.11.md`)  
**Pinned quantitative baseline:** `octave-commons/River-City@0b5510b6d97b8cb6da5eadfd147261207dece75b`  
**Baseline generated-at:** `2026-09-23T14:01:33.996910146Z`  
**PortWatch source retrieved-at:** `2026-09-23T14:00:56.604711002Z`  
**PortWatch source-observed-through:** `2026-09-20`  
**Lineage:** `direct-source-snapshot`  
**Ledger-backed:** `false`

## Question

When a projection is regenerated today but its underlying observations still end several days earlier, what should River City call "fresh," and when is a cross-source comparison actually eligible for reconciliation?

## Parent adjudication

**Consumed with a bounded null result:** v0.11 asked for a comparable raw-event packet on the first future day where public Kpler reporting names Hormuz vessels. The September 22 Reuters/Kpler report gives September 21 totals and identifies the two visible Hormuz movements only by class/flag (a Panama-flagged Supramax carrying minerals and a Liberia-flagged bulk carrier). It does not publish vessel names or a complete observation contract. More importantly, River City's current IMF-derived source is still observed only through September 20. There is therefore no canonical September 21 row to compare against yet.

**Retained:** H10C, comparable before correctable. The September 20 River City/IMF versus Kpler pair remains Hormuz `1 vs 10` and Bab el-Mandeb `26 vs 26`; the cause of the lane-selective mismatch is unresolved.

**Strengthened:** freshness must distinguish the time a pipeline ran from the time through which source observations exist. River City regenerated the PortWatch state on September 23 and retrieved the source on September 23, but the source coverage still ends September 20. The current regeneration therefore refreshes provenance and confirms the same observation cutoff; it does not create new traffic observations.

**Retained:** route activation, operating-friction/workaround, recipient allocation, and expected-deliverability branches. Saudi Arabia's East-West Pipeline is reported restarted at a reduced unquantified rate with a 4 mb/d target and 7 mb/d nameplate capacity; restart is not full restoration.

**Still untested:** H3, public API price versus useful-compute/self-host cost. Issues #14/#15 still gate any electricity-only or all-in self-host floor.

## Research increment: freshness has an evidence clock

v0.11 specified the observation contract. v0.12 adds a coverage-overlap gate before same-date reconciliation:

`physical movement -> source x lane observation contract -> source coverage window -> publication/revision state -> observed traffic state`

A reporting system therefore has at least two relevant clocks:

1. **process clock** - when River City retrieved, projected, or rendered the data;
2. **evidence clock** - the latest observation date represented by that source for the lane.

A new `generated-at` value demonstrates a new computation. It does not demonstrate a new observation. Cross-source comparison is valid only inside the intersection of the sources' observation coverage and only to the extent their observation contracts are comparable.

This is a conceptual research rule, not a production schema or scoring function. Any production metadata contract belongs in reviewed River City work.

## H11F - source freshness is not generation freshness

**Candidate claim:** when `generated-at` or `source/retrieved-at` advances but `source-observed-through` does not, River City should treat the quantitative state as newly revalidated but not observationally newer. A fresh external report beyond the canonical cutoff is a source-scoped provisional tail, not a missing canonical value and not evidence of zero traffic.

### Current observations

- Current River City PortWatch generated-at: **2026-09-23 14:01:33Z**.
- Current source retrieved-at: **2026-09-23 14:00:56Z**.
- Current source-observed-through: **2026-09-20**.
- Canonical September 20: Hormuz **1** visible vessel; Bab el-Mandeb **26**.
- Reuters/Kpler September 20: Hormuz **10**; Bab **26**.
- Reuters/Kpler September 21 preliminary: Hormuz **2**; Bab **26**.
- River City has no canonical September 21 row in the current source coverage.

### Discriminating predictions

- **Upstream-lag explanation:** repeated successful daily retrievals may advance retrieval/generation timestamps while the source cutoff remains fixed; when the upstream dataset advances, new dated rows should appear without changing prior values unless the source also revised history.
- **Adapter-lag explanation:** a direct upstream response already contains September 21 or later while River City's normalized/projection coverage remains September 20. That would shift the diagnosis from upstream freshness to ingestion behavior.
- **External-revision explanation:** Kpler's preliminary September 21 value may later change; the provisional tail should preserve both publication states rather than overwrite the first observation.
- **Contract-difference explanation:** once both systems overlap on September 21, a residual mismatch may remain because population, geofence, day boundary, AIS visibility, or classification differs.

### What would weaken H11F

- `source-observed-through` is shown to be a misleading metadata field that routinely lags while the actual included records are newer.
- River City's current aggregate source already contains September 21 records and the missing projection row is only a local processing defect.
- External post-cutoff reports are shown to refer to a different semantic date such that apparent non-overlap is an artifact of time labeling.

**Confidence:** high that process freshness and observation freshness are distinct in the current River City state; low on whether the September 20 cutoff is caused by upstream publication lag versus an upstream/adapter contract detail not exposed here. No probability or lag coefficient is estimated.

## Transmission channels retained

### Maritime and crude

Hormuz remains an extreme visible-traffic impairment in River City's canonical September 20 state. The East-West Pipeline restart creates additional expected route capacity, but current throughput is not public in the cited report. September 23 Reuters reporting put Brent near two-week lows while attributing market attention to improving Gulf supply and diplomacy; that is an observed market response, not proof of one causal channel.

### Refined fuels and Ukraine/Russia

U.S. EIA weekly on-highway diesel reached **$6.529/gal on September 21**, from **$6.285 on September 14** and **$5.967 on September 7**. Separately, Reuters reports the Moscow refinery halted crude processing after the September 20 drone attack, with exposed crude-unit capacities of **21,400 t/day** and **18,800 t/day**. Those capacities are not converted into a fabricated diesel-loss estimate.

### LNG/gas

Kpler estimates September Asian LNG imports at **20.09 Mt** versus **22.27 Mt a year earlier**, while Europe is estimated at **7.98 Mt** in September versus **7.55 Mt** in August. These remain native-unit regional allocation observations, not a combined gas stress score.

## AI, agents, open source, software infrastructure, defense/security

- River City's latest model-watch was observed September 22 and flagged **21 material changes** while keeping official-source coverage fresh across GPT/OpenAI, Claude/Anthropic, Kimi/Moonshot, GLM/Z.ai, Mistral, and MiMo/Xiaomi. OpenRouter price movements remain cross-checks requiring direct-provider verification.
- Direct official price observations used in the report include GPT-6 Astra, GPT-6 Sol, GPT-6 Luna, Claude Opus 5.5, Claude Sonnet 5, Mistral Large 3, Mistral-hosted GLM 5.3, and MiMo-V2.6-Pro. Kimi K3 is retained in its native CNY price basis rather than converted with an ad-hoc FX rate.
- GitHub added enterprise-managed OpenTelemetry export for Copilot agent sessions, exposing model/tool execution flow while excluding prompt/response content by default. This is an observability surface, not evidence that agent behavior is safe.
- Palo Alto Networks announced a continuous AI security-testing service using frontier and open-weight models. Dassault reported two AI algorithms flight-tested on Rafale. These are observable commercial/defense capability-development proxies; no classified deployment, effectiveness, or undisclosed revenue is inferred.

## Counterframes

- A stale canonical cutoff does not imply conditions are unchanged after the cutoff.
- A newer external observation does not automatically supersede the canonical source.
- Exact count agreement on Bab el-Mandeb does not prove identical observation contracts.
- A pipeline restart does not equal full pipeline throughput, and nameplate capacity is not realized flow.
- High diesel prices do not isolate the contribution of any single theater or refinery event.
- API token price is not useful-compute cost; cache/batch/context/region/provider lanes remain distinct.

## Missing views / explicit blockers

- **Conflict/strike tempo:** issue #6 still owns ACLED/UCDP structured events plus FIRMS corroboration; headlines are not counted as strikes.
- **Approved maritime normal-regime band:** issue #3 remains the policy gate.
- **API versus open-weight/self-host floor:** issues #14/#15 still require measured tokens/sec, wall power, representative hardware/workload, PUE, utilization, amortization, and regional power mapping.
- **Numeric hidden-variable heatmap:** issue #10 remains the definitions/evidence/prior policy gate.
- **Media-attention trend:** issue #34 remains the accumulation adapter; a new one-off homepage classification is not substituted for a trend.
- **Provider x lane provisional tail and freshness:** issue #27 already owns the needed revision-preserving source-scoped adapter; no duplicate issue is warranted.

## Exactly one next action

Under existing River City **#27**, when the canonical `source-observed-through` first advances to **2026-09-21 or later**, compare the newly available September 21 Hormuz/Bab aggregate row and source-record IDs against the preserved Kpler preliminary observation (**2 / 26**) and its class/flag metadata. If observation-contract fields remain incomplete, record `not comparable`; do not backfill or fit a correction.

## Bounded missing series

A revision-preserving **provider x lane coverage-overlap panel**: provider, lane, source-observed-through, publication/retrieval timestamp, semantic observation date, publication state, unit/population metadata, and raw observation. Existing issue #27 owns this problem.

## Evidence

- River City current baseline: `projections/manifest.edn`, `projections/portwatch/latest.edn`, `charts/portwatch/data/latest.json` at `0b5510b6d97b8cb6da5eadfd147261207dece75b`.
- Parent model: `reports/research/2026-09-23-comparability-contract-v0.11.md`.
- River City model watch: `reports/model-watch/latest.md` and alert `reports/model-watch/alerts/2026-09-22-7427fb77082b.md`.
- Reuters, 2026-09-22, *Hormuz vessel traffic falls to two, data shows*: https://www.reuters.com/world/middle-east/hormuz-vessel-traffic-falls-two-data-shows-2026-09-22/
- Reuters, 2026-09-22, *Saudi Arabia restarts East-West oil pipeline, sources say*: https://www.reuters.com/business/energy/saudi-arabia-restarts-east-west-oil-pipeline-resume-exports-yanbu-sources-say-2026-09-22/
- Reuters, 2026-09-21, *Moscow oil refinery output halted after Sunday drone attack, sources say*: https://www.reuters.com/business/energy/moscow-oil-refinery-output-halted-after-sunday-drone-attack-sources-say-2026-09-21/
- U.S. EIA, Gasoline and Diesel Fuel Update, released 2026-09-22: https://www.eia.gov/petroleum/gasdiesel/
- Reuters, 2026-09-17, *LNG spot price surge deters Asian buyers, but saves Europe*: https://www.reuters.com/commentary/reuters-open-interest/lng-spot-price-surge-deters-asian-buyers-saves-europe-2026-09-17/
- GitHub Changelog, 2026-09-22, *OpenTelemetry in the GitHub Copilot app*: https://github.blog/changelog/2026-09-22-opentelemetry-in-the-github-copilot-app/
- OpenAI API pricing: https://developers.openai.com/api/docs/pricing
- Anthropic Opus: https://www.anthropic.com/claude/opus
- Anthropic Sonnet: https://www.anthropic.com/claude/sonnet
- Mistral pricing: https://docs.mistral.ai/inference/pricing
- Xiaomi MiMo V2.6 Pro: https://mimo.mi.com/models/en-US/mimo-v2.6-pro
- Kimi K3: https://www.kimi.com/news/kimi-k3

## Implementation boundary

This artifact is generated research output. It does not create a production schema, source-correction factor, freshness score, hidden-variable score, baseline rule, or analytical code. Rendering scripts in the report bundle only plot source values; they do not smooth, impute, normalize, fit, or score them.
