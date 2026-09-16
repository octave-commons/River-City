# Usable Capacity — research model v0.1

**Artifact ID:** RC-UC-2026-09-16-v0.1  
**Date:** 2026-09-16  
**Status:** candidate analytical model; not a fitted forecast or canonical score  
**Semantic parent:** the September 16 morning River City briefing in the supplied conversation. This is the first formal model artifact in this publication series; no earlier formal model identifier is asserted.  
**Pinned quantitative baseline:** `octave-commons/River-City@abb3996f26866ec8809ae573a9403904c83097ca`.

## Question

When does nominal capacity fail to become delivered service, and which observations distinguish physical constraint from measurement, buffering, substitution or price strategy?

## Baseline and evidence boundary

The repository's projection was generated at 2026-09-16T17:04:46.315618502Z from source observations through September 13. Its lineage remains a direct-source snapshot, not ledger-backed history. On that date Hormuz records 8 visible vessels against a prior-year calendar-day median of 91 (-91.2%); Bab el-Mandeb records 31 against 61 (-49.2%). These are the repository's reported values, not recalculated quantities. The median/IQR is descriptive historical context, not an approved normal-regime policy.

The PDF reuses five raster chart attachments from the morning report. Its current checkpoint and external evidence were rechecked, but every historical plotted point was not independently reconstructed in this publication pass. Exact input-image hashes accompany the source bundle. This is a reproducible publication, not a completed replay of all data ingestion and analysis.

## Parent adjudication and actual research increment

**Retain:** nominal capacity and useful delivered service are different observables.

**Weaken:** an extreme shortfall in visible vessel counts directly measures an equally extreme shortfall in delivered energy. The count has no cargo-volume unit. Visibility, vessel population, loaded/ballast status, direction and time boundaries can change its relationship to delivered cargo.

**Add:** an explicit observation model between the physical system and its dashboard; separately identified buffers and substitutions; three rival-aware prospective tests.

**Distinguish:** cross-source disagreement from a revision within one source. The inherited September 13 chart pairs (River City / later Kpler reporting) are Hormuz 8 / 10 and Bab el-Mandeb 31 / 28. This artifact does not declare either provider wrong or promote one over the other. Their counting universes have not been reconciled here.

**Defer:** numerical causal effects, a compound usable-capacity score, quantitative forecast skill and the API/self-host residual. No evidence supports claiming those were estimated in this run.

**Parent action status:** the morning briefing proposed adding INES gas-storage observations to #25. This edition rechecks the primary INES comparison and includes it as attributed report evidence; it does not claim to have implemented the gas adapter or appended canonical Clio events. The adapter action remains unfulfilled.

## Candidate mechanism

The conceptual chain is nominal capacity -> conditions of use -> delivered service. The arrows are hypotheses, not fitted effects. Conditions differ by domain:

- Maritime: route access, vessel/cargo compatibility, loading availability, service restrictions, inventories and substitution.
- Refined fuels: operational refining throughput, product yields, stocks and downstream distribution.
- Gas: storage, injection/withdrawal, import availability and seasonal demand. Gas is not folded into the crude or refined-fuels channel.
- AI: usable power, accelerator/interconnect throughput, workload, authorization/containment and other operating overhead; public API prices additionally reflect contracts, efficiency, competition and strategic absorption.

Hormuz/Iran, Red Sea/Bab el-Mandeb and Ukraine/Russia remain separate theaters. They may contribute to shared channels, but this artifact does not invent a single conflict intensity score. Defense demand, sanctions and financing are unquantified candidate pressures. Public contract ceilings, obligations, expenditure, deployment and classified work must remain distinct.

## Hypotheses and discriminating tests

### H1 — Observation definitions explain some apparent movement

Claim: part of the cross-source difference may arise from counting universes, timing or visibility rather than a change in the underlying physical flow.

Test: for September 13, obtain source-specific geography, day boundary, vessel classes, direction, loaded/ballast coverage and revision metadata before comparing counts. Use known values as a paired evidence packet, not a newly normalized series.

Would weaken the proposed explanation: definitions match, yet independent movement records establish a substantive missing-event or normalization error. Distinguishing provider error from source revision requires the corresponding receipts.

Current status: a measurement limitation is established; attribution of the specific discrepancy remains untested. Confidence in a particular explanation is low. There is no numerical probability estimate.

### H2 — Shared constraints erode alternate-route resilience

Claim: an alternate route contributes less resilience when its use shares binding physical, economic or policy constraints with the primary route.

Test: after the next operator-confirmed East-West Pipeline restart, inspect the first subsequent dated loading observation alongside inventories and buyer replacement receipts. Do not infer restart from an official forecast alone.

Would weaken the case that this bypass is currently binding: downstream service is independently shown to remain sustained before restart through substitution or stock draw. Orlen's replacement-cargo response is a concrete rival mechanism; it is not evidence that all exposed supply has been replaced.

Current status: plausible mechanism supported by outage exposure, not an identified causal effect. The current magnitude and duration are uncertain. A single post-restart rebound cannot eliminate weather, demand, storage or routing confounders.

### H3 — Useful-compute cost and public API price have mediators

Claim: the cost of delivering the same AI workload can change without an immediate equivalent list-price change.

Test: once approved calibration exists, compare repeated same-workload, specified-hardware throughput and wall-power observations with independently dated public price and packaging changes. Maintain cache, batch, context and regional modes separately.

Would weaken it: a compatible repeated panel shows prompt and stable pass-through after controlling for workload, hardware and service class. No such panel was assembled here.

Current status: candidate hypothesis, not validated. Current official list-price observations cannot by themselves test this mechanism or reveal a closed provider's margin. Issues #14 and #15 still govern the necessary calibration and accounting decisions.

## Evidence and counterevidence used

- **S1: River City pinned report/manifest.** The current source cutoff, latest counts, historical comparator and lineage status constrain all current quantitative statements. [Report](https://github.com/octave-commons/River-City/blob/abb3996f26866ec8809ae573a9403904c83097ca/reports/daily/latest.md).
- **S4: Pipeline restoration expectation.** September 15 reporting quotes an expectation of restoration within days, not proof of operational restart. [Reuters](https://www.reuters.com/business/energy/us-energy-chief-says-saudi-arabia-oil-pipeline-should-be-back-within-days-2026-09-15/).
- **S5: Refinery exposure.** Industry-source reporting distinguishes plant stoppages from known delivered-product loss; nameplate is not foregone diesel. [Reuters](https://www.reuters.com/business/energy/russias-syzran-saratov-oil-refineries-hold-after-drone-attacks-sources-say-2026-09-16/).
- **S6: Conditional gas exposure.** INES's early-September comparison and winter scenarios motivate treating demand and buffers as mediators rather than inevitable outcomes. [INES](https://energien-speichern.de/en/ines-gas-scenarios-historically-low-storage-levels-window-for-sufficient-refill-is-closing/).
- **S7: Adaptation counterevidence.** Orlen's additional sourcing is evidence that buyers respond to a route disruption. [Reuters](https://www.reuters.com/business/energy/polands-orlen-buys-16-extra-crude-cargoes-amid-saudi-disruption-2026-09-16/).
- **S8–10: Official price observations.** [OpenAI](https://developers.openai.com/api/docs/models/gpt-6-astra), [Anthropic](https://www.anthropic.com/claude/sonnet), [Mistral](https://docs.mistral.ai/inference/pricing). Prices are not workload-normalized performance or internal costs.
- **S11: Announced versus operating compute.** The Australian campus agreement motivates preserving planned campus size, contracted load, approval and actual operation as different states. [Reuters](https://www.reuters.com/world/asia-pacific/anthropic-signs-first-australia-data-centre-agreement-2026-09-16/).

## Next action and successor contract

**Exactly one next action:** create the September 13 Hormuz source-comparison packet under existing #27, then adjudicate H1. Record the result even if it is not-comparable, unchanged or insufficient metadata.

A successor must cite this artifact as its parent, consume at least one pending test/action, retain adverse evidence, and specify its model delta. A new version need not assert a new mechanism if the evidence does not warrant one. Research continues across runs, not as a claimed unobserved process between runs.

## Implementation boundary

This Markdown is generated research output. It does not create a production schema, a new event store, new weights, new baseline rules, a fitted score or changes to analytical code. LaTeX publication integration belongs in existing #18, scheduling in #19, and durable event/substrate integration in #23. Any corresponding code/config changes remain reviewed issue/PR work. A dated model artifact is not evidence that those integrations are implemented.
