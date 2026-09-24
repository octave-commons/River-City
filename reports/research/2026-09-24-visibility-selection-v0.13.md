# Visibility Is Selection — research model v0.13

**Artifact ID:** `RC-UC-2026-09-24-v0.13`  
**Date:** 2026-09-24  
**Status:** candidate analytical model; no fitted correction, forecast, hidden-variable score, normalization rule, or production formula  
**Parent:** `reports/research/2026-09-23-freshness-overlap-v0.12.md` (`RC-UC-2026-09-23-v0.12`)  
**Pinned repository revision:** `octave-commons/River-City@19fd48a2a2fe4358bcba1bd93b03f04883e56b35`

## Baseline and evidence boundary

The pinned River City PortWatch projection was generated at `2026-09-23T17:09:25.363309315Z`, with the upstream source retrieved at `2026-09-23T17:08:46.752271550Z`, and still covers observations only through `2026-09-20`. Its lineage remains `direct-source-snapshot`; `ledger-backed?` remains false. The historical comparator is a same-calendar-day prior-year median/IQR and is explicitly provisional, not an approved normal-regime baseline.

The repository head also contains a model-watch observation at `2026-09-23T17:59:04.706Z`; that later software/model-economics update does not advance the PortWatch evidence clock.

Canonical September 20 PortWatch observations remain:
- Hormuz: 1 visible vessel vs prior-year median 91, IQR 82–99.5, deviation -98.9%.
- Bab el-Mandeb: 26 vs median 62, IQR 46–67.5, deviation -58.1%.

Newer Reuters/Kpler reporting is kept source-scoped rather than merged into the canonical series. The September 23 Reuters-syndicated publication describes September 22 as 3 visible Hormuz commodity vessels and 22 Bab el-Mandeb commodity vessels. It also says the Hormuz count was down from four a day earlier, implying that Kpler's later published state for September 21 is 4, whereas the September 22 Reuters publication had called the September 21 count 2. Both publication states are preserved. Kpler calls the figures preliminary; public reporting again warns that vessels with AIS transponders turned off may not be counted.

## Parent adjudication

**Retained:** v0.12's two-clock distinction. A fresh computation timestamp does not create a fresh observation.

**Retained:** source × lane observation identity and the requirement for coverage overlap before reconciliation.

**Parent next action remains untriggered:** River City's canonical `source-observed-through` is still September 20, so the requested September 21 canonical-vs-Kpler comparison cannot yet be completed. The Kpler September 21 observation now has two preserved publication states: preliminary Hormuz 2 / Bab 26 on September 22, and a later Hormuz value of 4 implied by the September 23 publication's statement that September 22's Hormuz count of 3 was down from four a day earlier.

**Strengthened:** the measurement caveat is not merely technical metadata. Public reporting repeatedly notes that some vessels deliberately disable AIS while traversing Hormuz. This creates a plausible selection mechanism in which observability changes with the same security conditions being measured. Separately, the September 21 Kpler count changed from 2 in one publication to 4 in the next day's publication, strengthening the already-established requirement that publication/revision state remain first-class evidence rather than overwritten history.

**Revised:** the observation model now distinguishes *source contract* from *visibility regime*. A source can apply a stable counting contract to an endogenously changing visible subset.

**Still untested:** whether the visible-vessel subset is materially biased relative to total cargo volume on a given day, how large that bias is, and whether it changes systematically with threat intensity.

## Research increment: endogenous observability

### Candidate mechanism H12V — visibility is selected, not guaranteed random

When attack risk, sanctions exposure, route-control rules, or commercial security incentives rise, some operators may suppress AIS or otherwise reduce public observability. If the probability of being visible is correlated with vessel type, cargo, operator, destination, compliance status, or threat exposure, a visible-vessel count is a selected sample of physical movement rather than a random sample.

This does **not** mean visible counts are useless. It means they are one measurement channel whose relationship to total cargo flow can change across regimes.

The model therefore becomes:

`physical movement -> visibility / disclosure regime -> source × lane observation contract -> coverage window -> publication / revision state -> observed traffic`

The existing deliverability branch remains separate:

`route activation + substitution/buffers + operating friction -> allocation -> recipient-specific delivered service`

### Why the new node is warranted

- Reuters-syndicated September 23 reporting counts only three visible commodity vessels for September 22 and explicitly says the figure can change because vessels sometimes switch off transponders.
- Reuters' September 22 report carries the same visibility caveat and initially reports September 21 Hormuz traffic as 2; the September 23 successor publication describes the preceding day as 4. This is a publication revision, not a physical event to be averaged away.
- The same September 20 cross-source comparison remains lane-specific: River City/IMF reports Hormuz 1 vs later Kpler 10, while both report Bab 26. This supports preserving lane-specific observation contracts rather than fitting a provider-wide correction.
- An LNG tanker, Al Mafyar, reappeared inside Hormuz on September 22 after last being recorded outside the strait on September 19. Reappearance is evidence about public observability state, not a complete record of its intervening movement.

These observations establish a measurement risk, not its magnitude.

## Rival explanations

The visible-count discrepancy could arise without endogenous AIS selection. Live rivals remain:

1. different chokepoint geofences;
2. different semantic day boundaries or query cutoffs;
3. different commodity-vessel populations or loaded/ballast inclusion;
4. source-specific classification or deduplication;
5. publication/revision lag;
6. missing, late, or corrected records unrelated to deliberate darkness;
7. simple transcription or normalization error.

A dark-traffic explanation must not be promoted merely because it is plausible.

## Discriminating tests

### T12V-1 — visible count vs independent cargo-flow proxy

For overlapping days, compare visible-vessel counts with an independently sourced cargo-volume or port-loading/export series by lane and commodity. Preserve each source's unit and revision state.

**Prediction if H12V matters:** visible counts and cargo volumes sometimes diverge materially during high-risk intervals, with the divergence narrowing when public AIS compliance/visibility improves.

**Would weaken H12V:** independent cargo/export volumes fall approximately in proportion to visible counts across the same dates and commodity populations, or independent evidence shows dark traffic remains small and stable.

### T12V-2 — known reappearance / disappearance receipts

Where public reporting identifies a vessel disappearing from and later reappearing in AIS/LSEG/Kpler visibility, preserve the vessel/date/status sequence as an observation receipt without inferring the unobserved path.

**Prediction:** a non-trivial set of such receipts will cluster in the high-risk Hormuz interval more than in the Bab control lane.

**Would weaken:** events are rare, evenly distributed, or explained by ordinary data outages unrelated to security conditions.

### T12V-3 — source-contract reconciliation before correction

Continue v0.12's pending test when River City's canonical cutoff reaches September 21 or later. Apply the existing comparability checklist first. Do not use a dark-traffic hypothesis as a residual correction term.

## Energy / logistics update

Saudi Arabia restarted the East-West Pipeline on September 22 at a reduced, unquantified rate. Reuters reports about 4 million bpd as the recent bypass/rerouting level and 7 million bpd nameplate capacity, with three pumping stations damaged and full restoration estimated at 6–8 weeks. A restart state is not full throughput.

U.S. EIA on-highway diesel reached $6.529/gal for the September 21 survey, up from $6.285 on September 14 and $5.967 on September 7. This is downstream price evidence, not a decomposition of crude, refinery, inventory, freight, tax, or demand causes.

Ukraine/Russia remains a separate theater. Reuters reported Moscow refinery processing halted after the September 20 drone attack, with two cited crude-unit capacities affected. Those capacities are exposure, not a realized diesel-loss estimate.

## Gas channel

The prior LNG allocation observation remains useful and unrevised in this run: Reuters/Kpler estimated Asia September imports at 20.09 Mt versus 22.27 Mt a year earlier, while Europe September imports were estimated at 7.98 Mt versus 7.55 Mt in August. The comparison bases differ and remain explicit.

## AI / agents / open source / infrastructure

River City's September 23 model-watch recorded 14 material changes: 5 in GPT/OpenAI, 1 in Claude/Anthropic, and 8 in GLM/Z.ai. Direct official verification in this run supports new GPT-6 Sol and Luna list prices; Anthropic's Opus 5.5 is $4/M input and $20/M output; Z.ai lists GLM-5.3 at $1.40/M input and $4.40/M output; Kimi K3 lists $3/M cache-miss input and $15/M output; Mistral Large 3 is $0.50/$1.50; Xiaomi MiMo-V2.6-Pro is $0.435/$0.87. Cache/batch/regional/priority/subscription lanes remain separate.

No API-vs-self-host residual is produced. Issues #14 and #15 still gate the measured throughput, wall power, hardware/workload, PUE, utilization, amortization, and regional-energy assumptions.

GitHub's Copilot app added enterprise-managed OpenTelemetry for agent sessions, while JetBrains Copilot added organization/enterprise skills, Codex plan mode, and per-tool MCP controls. These are concrete observability and authority surfaces; they are not evidence that autonomous agent behavior is safe.

Anthropic's Opus 5.5 launch adds stricter cyber/biology/anti-distillation safeguards and lower list pricing than Opus 5. Provider claims about performance and safety are not independently reproduced here.

## AI-national-security lens

Publicly observable evidence remains separated from classified inference. Recent public evidence includes commercial frontier-model security products and military AI flight testing reported earlier this week. This model assigns no classified revenue, deployment probability, or effectiveness estimate.

## Missing views and policy gates

- Strike tempo by theater/side: unavailable; issue #6 still owns structured ACLED/UCDP/FIRMS ingestion.
- Approved normal-regime maritime band: unavailable; issue #3 remains the policy gate.
- Numeric hidden-variable heatmap: unavailable; issue #10 remains the definition/evidence/prior gate.
- API-vs-open-weight self-host floor/residual: unavailable; issues #14/#15 remain the calibration/accounting gates.
- Media-attention trend: unavailable without accumulated sampling; issue #34 remains the adapter.
- Total-vs-visible maritime flow comparator: not implemented. Existing issue #27 is the closest owner for source-scoped provisional maritime observations and reconciliation; this run extends its evidence requirements rather than creating a duplicate issue.

## Confidence and limits

- High confidence: River City's current canonical cutoff remains September 20 and latest counts are 1 Hormuz / 26 Bab.
- High confidence: the September 23 Reuters-syndicated/Kpler publication reports September 22 visible counts of 3 Hormuz / 22 Bab and explicitly warns that AIS-dark vessels may be absent.
- High confidence: the publicly reported September 21 Hormuz state changed from 2 in the September 22 Reuters publication to 4 in the September 23 successor publication. This establishes revision risk, not the cause of revision.
- Moderate confidence: endogenous observability is an important mechanism worth testing because the caveat is repeated and directly linked to operator behavior under risk.
- Low confidence: any quantitative dark-traffic share, direction of bias by commodity, or correction factor. None is estimated.

## Exactly one next action

Under existing River City #27, add a source-scoped **visibility comparator evidence packet** for September 20-22: preserve canonical visible counts, both Kpler September 21 publication states (2 then 4), September 22 Kpler visible counts, publication/revision dates, the explicit AIS-dark caveat, any named disappear/reappear vessel receipts, and one independent cargo-volume/loading proxy when a lawful source is available. Do not compute a correction factor; record `not comparable` where units or populations differ.

## Bounded missing series

A revision-preserving `visible traffic ↔ independent cargo/loadings` comparator by lane, commodity, source, observation date, publication state, and known visibility limitation. Its purpose is to test H12V; it is not a new risk score or production normalization.

## Added evidence references for v0.13

- Reuters-syndicated, 2026-09-23, *Strait of Hormuz transits remain sluggish, hovering below 10-day average* (World Ports Organization mirror): https://www.worldports.org/strait-of-hormuz-transits-remain-sluggish-hovering-below-10-day-average/
- Reuters, 2026-09-22, *Hormuz vessel traffic falls to two, data shows*: https://www.reuters.com/world/middle-east/hormuz-vessel-traffic-falls-two-data-shows-2026-09-22/
