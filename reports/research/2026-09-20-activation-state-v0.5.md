# Usable Capacity — activation-state v0.5

**Artifact ID:** `RC-UC-2026-09-20-v0.5`  
**Date:** 2026-09-20  
**Status:** candidate analytical model; not a fitted forecast, production score, normalization rule, or schema  
**Parent:** `RC-UC-2026-09-19-v0.4` (`reports/research/2026-09-19-recipient-deliverability-v0.4.md`)  
**Pinned River City revision:** `f2a13e19821189b7aab7dbcf865436f76d1d199a`  
**Pinned PortWatch chart-data blob:** `6784510b7e6dc47b13af475fd81a2606838ddf8b`

## Research question

When a system advertises spare route, power, compute, or financing capacity, what has to happen before that capacity can actually support recipient-specific service?

## Stable baseline

River City's current manifest was generated at `2026-09-19T15:56:14.832864914Z`; PortWatch source retrieval was `2026-09-19T15:55:55.695623380Z`, with observations through `2026-09-13`. Coverage remains 2019-01-01 through 2026-09-13 with 5,626 source rows and 360 projection rows. Lineage remains `direct-source-snapshot`; `ledger-backed? false`. The same-calendar-day median/IQR is descriptive and provisional, not an approved normal-regime baseline.

The latest canonical values remain Hormuz 8 visible vessels versus median 91 (-91.2%) and Bab el-Mandeb 31 versus median 61 (-49.2%). Fresh Kpler observations remain source-scoped rather than overwriting this projection.

## Parent adjudication

### Parent action — seed issue #36

**Consumed.** On 2026-09-20, the connected GitHub user posted a source-scoped seed comment to issue #36 with: (1) about 60 million barrels of Saudi September/October Ras Tanura crude sold for Sohar ship-to-ship transfer, reported by Reuters from trade sources; (2) the Reuters-relayed, not independently verified report that at least two European refining customers were told they would receive no Saudi crude in October; and (3) Orlen's 16 additional crude cargoes from diversified suppliers. The comment preserves attribution and unknown customer/volume fields. This is issue evidence, not canonical ledger ingestion.

### H1R — revision lag can dominate short-run deltas at low counts

**Retained; null increment again.** No new matched successor report was located for September 17, so the matched panel remains three Hormuz pairs and two Bab el-Mandeb pairs. No correction coefficient or error distribution is inferred.

### H1D — delayed observability explains upward revisions

**Still untested.** AIS-dark movement and reconciliation remain plausible, but source geofence, vessel identities, day boundary and revision reasons remain unavailable.

### H2R — substitution reallocates service rather than restoring it uniformly

**Strengthened, still bounded.** The parent observations now persist on issue #36. Saudi Gulf-side adaptation is reported at roughly 1.0–1.5 million b/d through Sohar STS across a 60-million-barrel September/October program, while at least two European term customers were reportedly cancelled and Orlen bought replacement cargoes. This supports recipient heterogeneity without proving a stable market-allocation law.

### H3 — API list price can move independently of useful-compute cost

**Still untested, with fresh provenance evidence.** River City's September 19 model watch saw OpenRouter cross-check moves for Kimi K3 and GLM-4.6. Direct Z.ai pricing currently lists GLM-4.6 at $0.60 input, $0.11 cached input and $2.20 output per million tokens, while the OpenRouter cross-check is $0.50/$0.10/$2.00. Aggregator motion therefore remains evidence about the routing market, not canonical provider cost. Issues #14/#15 still gate self-host throughput, wall power and accounting.

## New model increment — activation state

v0.4 ended with recipient-specific delivery but still treated the capacity entering the mechanism too generically. v0.5 adds an explicit **activation state** between headline capacity and usable service:

`headline / nominal capacity -> activation state -> usable capacity -> substitution/buffers -> allocation -> recipient-specific delivered service`

Activation can include repair completion, loadability, interconnection, permitting, financing, equipment delivery, insurance/service access, or other domain-specific gates. These are not interchangeable and are not combined into a score.

### Candidate H4A — headline capacity overstates near-term usable capacity when activation gates are binding

**Evidence supporting the candidate:**

1. Nscale's IPO filing describes a 10-GW *power pipeline* across 14 regions, more than $103 billion in contracted revenue, $3.1 billion in convertible financing, $140.6 million H1 revenue, a $1.02 billion H1 loss and 52% of current revenue from one customer. The 10 GW is not reported as energized capacity.
2. Anthropic's Australian lease covers a planned 2.16-GW campus intended to begin coming online in 2027 and remains subject to foreign-investment approval. A lease and planned GW therefore precede operational compute.
3. Virginia's new data-center framework adds transparency and energy-policy constraints for projects at or above 25 MW, showing that permission and cost allocation can change the path from project announcement to operation.
4. Nippon Life's planned roughly $12.75 billion of U.S. infrastructure financing, including data centers, is a financing input rather than operating capacity.
5. In energy logistics, the damaged East-West Pipeline's pre-attack 4–5 million b/d throughput and the 1–1.5 million b/d Sohar STS adaptation are different stages/pathways. Reported restoration timelines are not themselves throughput observations.

**Interpretation:** resilience depends not only on nominal spare capacity but on whether that capacity is financeable, permitted, repaired, interconnected, loadable and serviceable at the relevant time. The same conceptual distinction applies across domains, but the activation evidence and units remain domain-specific.

**Prediction:** when activation gates bind, large announced/requested capacity should coexist with smaller observed operational delivery and with visible financing, permitting, interconnection, repair or equipment milestones. Stage transitions should predict changes in usable service better than headline capacity alone.

**Would weaken:** repeatedly observed cases in which announced/requested capacity becomes operational on schedule without identifiable activation constraints, or where stage milestones add no explanatory value over simple nominal capacity. In the energy case, immediate restoration of route throughput before reported repair/loadability changes would weaken the route-activation mechanism.

**Current confidence:** moderate that stage identity is analytically necessary; low that one cross-domain stage vocabulary can be standardized without domain-specific review. No numeric probability is estimated.

## Observation model retained

Maritime observations continue to use:

`physical movement -> source observation -> preliminary publication -> revision/backfill -> later publication`

September 13 remains a cross-source residual: River City/IMF records Hormuz 8 / Bab 31, while later Kpler reporting described 10 / 28. Those values are not averaged.

## New evidence / context

- On September 20, Russian officials reported a large Ukrainian drone attack on the Moscow region and damage at a Moscow oil-refinery facility. Ukraine had not issued a formal statement in the cited Reuters report. The event is a fresh refined-fuel infrastructure observation, not a strike-count series or quantified lost-output estimate.
- Spain's data-protection authority disclosed an investigation into a reported breach allegedly carried out by an AI agent with minimal human intervention. This is an observed incident report under review, not an incident-rate estimate.
- The U.S. Federal Register briefly used an Alibaba Qwen open-weight model for search before removing it. Experts cited by Reuters said the public-data use did not necessarily pose an immediate security risk; the episode is evidence about procurement/governance boundaries, not classified capability or effectiveness.
- GitHub plans to remove Node 20 from Actions runners on September 23, 2026. Workflows or self-hosted runners that still depend on Node 20, old macOS, or ARM32 support have an immediate migration deadline.

## Countermodels and confounders

1. **Pipeline/lease labels may already encode high readiness.** A developer's “power pipeline” can include materially different maturity stages; treating every pipeline MW as equally speculative would be wrong.
2. **Financing can follow demand rather than constrain it.** Large financing commitments may reflect already-secured projects rather than a binding activation bottleneck.
3. **Regulation can improve readiness.** Transparency, cost allocation or clean-power requirements may reduce long-run political/reliability risk even if they lengthen early-stage development.
4. **Recipient churn can coexist with adequate aggregate supply.** Allocation heterogeneity does not prove system-wide shortage.
5. **Visible vessel counts are not cargo volume.** AIS-dark traffic, vessel size, load state and route matter.
6. **API list price is not provider cost.** Routing markets, commercial strategy, cache/batch modes and subsidies remain separate.

## Model delta: v0.4 -> v0.5

1. Consumes the parent's #36 seeding action and records the persistence boundary: issue evidence exists; canonical adapter ingestion does not yet.
2. Retains recipient-specific delivery and revision state.
3. Adds **activation state** between nominal/headline capacity and usable capacity.
4. Adds H4A with domain-specific evidence from AI infrastructure and energy logistics, plus prediction, falsifier and rival explanations.
5. Treats large AI power-pipeline/lease/financing numbers as stage-qualified observations rather than energized compute.
6. Leaves normal-regime, conflict-intensity, latent-score and self-host-cost formulas unchanged behind existing River City gates.

## Exactly one next action

Seed existing issue #31 with two stage-labeled AI-infrastructure observations — Nscale's 10-GW *power pipeline* and Anthropic Australia's planned 2.16-GW campus — preserving financing/permitting/online-date qualifiers and explicitly leaving energized MW unknown.

## Bounded missing series / adapter

Existing issue #31 already owns the missing **AI infrastructure activation-stage transition series**: requested/planned -> financed/contracted -> permitted/interconnected -> energized/operational, with evidence dates and units kept separate. No duplicate issue is needed.
