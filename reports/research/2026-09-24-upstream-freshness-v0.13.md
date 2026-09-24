# Freshness Is a Supply-Chain State — research model v0.13

**Artifact ID:** `RC-UC-2026-09-24-v0.13`  
**Date:** 2026-09-24  
**Status:** candidate analytical model; no fitted correction, forecast, latent score, normalization rule, or production formula  
**Parent:** `RC-UC-2026-09-23-v0.12` (`reports/research/2026-09-23-freshness-overlap-v0.12.md`)  
**Pinned quantitative baseline:** `octave-commons/River-City@db5d29466611df1d24b7480e070de9f229fd0431`  
**Projection generated-at:** `2026-09-24T13:49:01.253381443Z`  
**Source observed-through:** `2026-09-20`  
**Lineage:** `direct-source-snapshot`; `ledger-backed? false`

## Question

When River City successfully retrieves and regenerates PortWatch state but the observation cutoff does not advance, where is the freshness bottleneck most likely to sit?

## Parent adjudication

### Retained and strengthened — H11F: process freshness is not evidence freshness

The September 24 run advances local retrieval and projection time while the PortWatch observation cutoff remains September 20. A newly generated projection therefore represents a successful revalidation of the same source-observation horizon, not a new physical observation.

### Prior next action — still pending, but now better diagnosed

v0.12 required a comparison between a newly available September 21 River City row and the preserved Kpler September 21 observation. River City's canonical source still stops at September 20, so that comparison is not yet eligible. This run does not backfill September 21 from Kpler or treat an external observation as a replacement for the canonical source.

### Weakened — simple local adapter date-filter explanation

Inspection of the pinned adapter shows that the current ArcGIS query selects Hormuz/Mandeb by `portname`, requests the full configured field set, orders by date/port/ObjectId, and paginates in 1,000-row pages. It does not set a date ceiling. The September 24 generated-state commit records 5,640 source features and 5,640 normalized rows. This weakens the narrow hypothesis that a local date filter or an obvious row-count loss is the reason the cutoff remains September 20.

It does **not** prove that the upstream ArcGIS dataset itself is stale. An independent direct probe of the upstream layer's current `max(date)` was not available in this run, and a different ArcGIS layer/query semantic could still expose newer records.

## Research increment — three freshness clocks

v0.12 separated the process clock from the evidence clock. v0.13 adds an upstream-availability clock:

`physical event time -> upstream publication/availability -> local retrieval -> normalization/projection -> report generation`

A dashboard can therefore be computationally fresh while its source is observationally stale. The difference is not merely metadata: it determines whether a newer external observation is a provisional tail, a same-source revision, or a canonical continuation.

### Candidate hypothesis H13U — upstream publication lag is the leading explanation

**Claim.** Given the current no-date-ceiling full-history query, successful pagination, and equal source-feature/normalized-row counts, the unchanged September 20 cutoff is more consistent with the queried upstream layer not yet publishing September 21+ rows than with a simple local date-filter or normalization-drop bug.

**Evidence that raises confidence:**
- September 24 retrieval completed and the generated projection advanced in time.
- The adapter query has no explicit date upper bound.
- The adapter paginates the result set.
- The generated-state commit reports `source/feature-count = 5640` and `row-count = 5640`.
- External Kpler reporting has source-scoped observations for September 21–23, showing that physical activity continued after River City's source cutoff.

**Rival explanations still open:**
1. The ArcGIS service has newer data in a different layer or query path than the one River City uses.
2. Upstream records exist but are omitted by the `portname` selection or use names outside the current target matcher.
3. ArcGIS query/cache semantics return an older snapshot even though the underlying service contains newer rows.
4. An upstream publication/revision process intentionally lags several days.

**Would weaken H13U:** a direct receipt from the exact ArcGIS layer/query shows a `max(date)` later than September 20 while the same successful River City retrieval still produces a September 20 cutoff.

**Would strengthen H13U:** a direct upstream `max(date)` receipt also shows September 20, followed by a later upstream advance that is reflected by River City on the next successful full retrieval without a code change.

**Confidence:** medium in the narrow diagnosis that a simple local date ceiling is not the blocker; low-to-medium that upstream publication lag specifically is the cause, because the upstream max-date could not be independently queried in this run. No numerical probability is implied.

## Fresh external tail and revision state

The provisional Hormuz tail remains source-scoped rather than merged into the River City series. Reuters/Kpler first reported September 21 at 2 visible commodity vessels; the following day's report described Monday as 4. Reuters/Kpler first reported September 22 at 3; a September 24 attributed report describes Tuesday as 7. September 23 is reported at 10 and remains preliminary in this evidence packet. These observations reinforce the prior model's rule that revision state belongs to observation identity.

The September 20 River City/IMF value of 1 and the Kpler later-reported value of 10 remain a cross-source disagreement, not a same-source revision. No provider-wide correction coefficient is fitted.

## Transmission-channel updates

### Oil logistics

Saudi Arabia's East-West pipeline has resumed at a reduced, unquantified operating rate after the September 11 attack; reported pre-attack flow was about 4 million bpd and design capacity about 7 million bpd, with full restoration expected to take weeks. Ship-to-ship transfers near Oman were reported around 2.5 million bpd in September versus 1.4 million bpd in August, with Gulf-to-China VLCC freight above $30/bbl. These are adaptation and operating-friction observations, not proof of normalized service.

### Refined fuels / Ukraine-Russia

The Moscow refinery halted crude processing after the September 20 drone attack. Two affected units were reported at 21.4 and 18.8 thousand metric tons/day capacity. Unit capacity measures exposure; it is not an estimate of lost diesel output.

### LNG/gas

Kpler estimates September Asian LNG imports at 20.09 Mt, below both August 2026 (22.25 Mt) and September 2025 (22.27 Mt), while European September imports rise to 7.98 Mt from 7.55 Mt in August. This remains evidence of regional allocation under price/supply pressure, not a universal priority rule.

### AI economics

River City's September 23 model-watch added GPT-6 Luna and Sol and preserved direct-provider versus routing-market provenance. Current direct/current-provider observations span GPT-6 Luna/Sol, Claude Sonnet 5/Opus 5.5, Mistral Medium 3.5, Mistral-hosted open GLM 5.3, and Xiaomi MiMo V2.6 Pro/Flash. A self-host electricity floor is not generated because issues #14/#15 still lack the required measured throughput, wall power, hardware/workload and accounting decisions. Kimi and MiniMax are not promoted into today's direct-provider chart because a clean current first-party numeric rate card was not retrieved in this run.

## AI / agents / software / national-security lens

- GitHub retired Node 20 from Actions runners on September 23; JavaScript actions now run on Node 24 and the temporary insecure opt-out is gone. This is an immediate compatibility check for pinned/old actions and self-hosted runners.
- GitHub added local sandboxing and OpenTelemetry support for Copilot agent activity. The combination makes agent authority and observability configurable surfaces rather than purely behavioral assumptions.
- Ornith-1.0 was released as an MIT-licensed open-source family for agentic coding with a self-improving training framework; benchmark claims remain first-party model-card claims until independently replicated.
- The UK and U.S. announced a defense AI collaboration between their respective rapid-delivery/digital-AI offices. This is observable institutional/procurement-direction evidence, not evidence of classified deployments, revenue, or operational effectiveness.

## Countermodels and checks

1. **Fresh-tail optimism:** visible Kpler traffic after September 20 does not imply River City's canonical source is wrong; it may be measuring a different population and it is revisable.
2. **Upstream-lag overreach:** equal input/output row counts do not prove the upstream layer is complete; they only reduce confidence in an obvious normalization-drop explanation.
3. **Pipeline restart equals normalization:** reduced-rate restart is an activation-stage change, not full 7 mbd restoration or confirmed downstream delivery.
4. **Refinery capacity equals product loss:** affected crude-unit capacity is not realized diesel/gasoline loss.
5. **API price equals useful-compute cost:** token list price is not performance-normalized cost and reveals nothing reliable about a closed provider's internal margin.

## Exactly one next action

Under existing River City issue #27, capture a direct source receipt for the exact IMF ArcGIS layer/query containing retrieval timestamp, feature count, and `max(date)`, then compare it with River City's same-run source feature count and projection cutoff. If the upstream maximum cannot be obtained, record that field as unavailable rather than inferring it.

## Bounded missing series

**Upstream publication-latency receipts:** per successful fetch, persist source endpoint/layer identity, exact query identity, retrieval time, feature count, source `max(date)`, and resulting projection cutoff. This belongs to existing #27; no duplicate issue is needed.

## Implementation boundary

This artifact is generated research output. It does not alter the PortWatch query, production schemas, baseline policy, scoring rules, normalization, or self-host economics. Any production implementation change remains reviewed issue/PR work.
