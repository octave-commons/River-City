# Expected Deliverability - research model v0.9

**Artifact ID:** RC-UC-2026-09-22-v0.9  
**Date:** 2026-09-22  
**Status:** candidate analytical model; not a fitted forecast, score, index, or production formula  
**Parent:** `RC-UC-2026-09-21-v0.8` - `reports/research/2026-09-21-price-basis-v0.8.md`  
**Pinned quantitative baseline:** `octave-commons/River-City@823ead34272a02419b4fbadd599db44571d799ea`  
**Baseline generated-at:** `2026-09-21T18:14:02.161060595Z`  
**PortWatch source retrieved-at:** `2026-09-21T18:13:29.727601009Z`  
**PortWatch source-observed-through:** `2026-09-13`  
**Lineage:** `direct-source-snapshot`  
**Ledger-backed:** `false`

## Question

Can a market benchmark de-stress before the physical route recovers, and if so what observable separates current deliverability from expected future deliverability?

## Parent adjudication

**Consumed with another negative result:** v0.8 asked for one public evidence packet matching Gulf crude cargo/grade, route or destination, pricing period, commercial price basis, and responsibility for freight or insurance. The current search did not recover a new same-cargo matched numeric producer netback or landed-cost observation. The v0.8 result therefore remains `not comparable`; no burden share or netback is inferred.

**Retained:** H7B, burden incidence is contract/price-basis mediated. Public freight, OSP differentials, spot discounts, insurance, buyer landed cost, and producer netback remain distinct observables.

**Strengthened:** H4A, activation state matters. Saudi Arabia's East-West Pipeline moved from shutdown to a reported low-rate restart on September 22, while the stated target is 4 million bpd and full resumption may still take weeks. `restarted` is not equivalent to `restored to target throughput`.

**Retained:** H5F/H6I, workarounds can preserve service while adding operating friction and shifting burden. Reuters reports traders still repositioning tankers for ship-to-ship transfers after the pipeline restart.

**Retained:** H1R/H1D, source/revision identity remains part of maritime evidence. River City's canonical daily series still ends September 13; fresh Kpler observations remain external and AIS-dark movement is explicitly not converted to zero.

**Retained without calibration:** H3, public API price versus useful-compute/self-host cost. River City #14/#15 still block a lawful electricity-only or all-in self-host floor.

## Model delta

v0.8 made commercial price basis explicit before inferring burden incidence. v0.9 adds **expected future deliverability / option state** as a separate observation from current physical deliverability. The market-price branch is no longer treated as a passive mirror of current route flow.

Candidate graph:

`current path state -> current usable/delivered service`

`recovery + substitution options -> expected future deliverability -> market benchmark`

`market benchmark + contract/price basis -> burden incidence -> allocation -> recipient-specific delivered service`

The branches interact, but this artifact defines no coefficient relating them.

## Research increment - H8E: benchmarks can move on expected deliverability before current physical normalization

### Candidate mechanism

Commodity benchmarks are forward-looking prices. A credible restoration, substitution, or reopening option can reduce expected future scarcity before current physical passage returns to normal. Consequently, a lower crude benchmark can coexist with severely impaired visible passage, and downstream refined-product stress can persist when conversion capacity, inventories, or product-specific routes remain constrained.

### Current evidence

1. **Current physical passage:** Kpler reported only **2 visible commodity-vessel crossings through Hormuz on September 21**, down from 10 on September 20. AIS-dark traffic is excluded. Bab el-Mandeb was 26 on both days. These observations are external to River City's canonical September 13 cutoff.
2. **Restoration option:** Saudi Arabia restarted the East-West Pipeline on September 22 at a **low rate** after the September 13 shutdown. Reuters reports a target of **4 million bpd**, a Yanbu cargo scheduled for China, and that full resumption may still take weeks. Current low-rate throughput was not publicly quantified.
3. **Reopening option:** a senior Iranian official told Reuters that Iran could reopen Hormuz within seven days if specified U.S. conditions were met. This is an attributed diplomatic offer, not an observed reopening.
4. **Benchmark response:** Brent November futures were reported at **$98.23/bbl**, down 2.1% intraday on September 22 and at a two-week low. Reuters linked the move to the improved Gulf supply outlook, including the pipeline restart and possible Hormuz reopening.
5. **Product counterevidence:** Reuters reports diesel prices remain at record highs in Europe and the United States; separately, the Moscow refinery halted after a September 20 drone attack and repairs may take several weeks. Crude de-stress therefore does not establish downstream product normalization.

### Discriminating predictions

- If a credible recovery option is economically meaningful, crude benchmarks can fall **before** visible Hormuz passage or bypass throughput returns to prior levels.
- If the option is only rhetorical or operationally ineffective, the benchmark effect should reverse or fade when the expected stage transition fails, even without a new deterioration in current passage.
- Refined-product prices can remain stressed while crude de-stresses when refinery capacity, inventories, or product-specific logistics remain constrained.
- A bypass restart should be represented as staged state transitions (shutdown -> low-rate -> target/full-rate) rather than as a binary restoration flag.

### What would weaken H8E

- Across repeated disruptions, benchmark prices track contemporaneous delivered quantity closely and show no systematic response to credible restoration/reopening information before physical flow changes.
- The September 22 price move is better explained by unrelated demand, macro, inventory, or financial factors and does not persist alongside the cited restoration signals.
- The East-West Pipeline immediately reaches target throughput before the observed benchmark adjustment, eliminating the claimed timing distinction.

### Countermodels and confounders

- Broad macro risk, interest rates, currency moves, demand expectations, inventories, strategic-stock releases, and unrelated supply changes can move Brent independently.
- The Saudi/Oman ship-to-ship workaround may already have reduced scarcity before the pipeline restart.
- Diplomatic claims can be strategically framed and may not produce operational access.
- Visible vessel counts are not cargo volume and omit AIS-dark traffic.

### Confidence

**Mechanism confidence: moderate. Event-level causal attribution and cross-domain generality: low.** The current evidence clearly separates current passage state from announced/reported recovery options and shows a same-day crude-price move in the direction Reuters attributes to improved supply expectations. It does not identify how much of that price move each factor caused.

No price-response coefficient, event-study alpha, stress index, probability of reopening, or cross-domain normalization is estimated.

## Current quantitative baseline and freshness

River City's pinned September 13 PortWatch state remains:

- **Bab el-Mandeb Strait:** 31 visible vessels versus descriptive prior-year median 61; -49.2%.
- **Strait of Hormuz:** 8 visible vessels versus descriptive prior-year median 91; -91.2%.

The comparator is `provisional-observed-history`, not an approved normal-regime policy. The projection is a direct-source snapshot and is not ledger-backed. Missing or dark traffic is not zero.

Fresh Reuters/Kpler observations are source-scoped and not merged into the canonical daily series: September 20/21 Hormuz = 10/2; September 20/21 Bab el-Mandeb = 26/26.

## Separate theaters and transmission channels

- **Hormuz/Iran:** current visible passage impairment, attacks on two vessels with attribution unconfirmed, possible diplomatic reopening, and military/policy conditions belong to the Hormuz theater. Crude/logistics and finance are transmission channels, not theater scores.
- **Red Sea/Bab el-Mandeb:** current visible traffic is materially higher than Hormuz and steady at 26 in the fresh observation; the Red Sea remains a separate route-risk theater.
- **Ukraine/Russia:** the Moscow refinery shutdown is a refined-fuel conversion-capacity observation. No headline-derived strike count is created.
- **Saudi East-West/Yanbu:** the pipeline is a transmission-route and critical-infrastructure observation, not a new geopolitical theater.

## AI, agents, open source, software infrastructure, defense/security

- **Agent/cyber commercialization:** Palo Alto Networks announced Unit 42 Continuous Frontier AI Defense using Anthropic Claude Mythos 5, OpenAI GPT-5.6-Cyber, and open-weight models for continuous testing of web apps, APIs, and cloud infrastructure. Pricing is by selected model mix. This is observable productization of frontier-model security work, not evidence that one model is operationally superior.
- **Defense-specific capability:** Dassault said it flight-tested two AI algorithms on Rafale and considers them mature enough for possible future upgrades. The company did not disclose their operational roles. This is an observable capability-development proxy; no classified deployment or revenue is inferred.
- **Government AI-defense cooperation:** UK and U.S. defense AI organizations announced collaboration on trusted/interoperable capabilities and critical-infrastructure protection. No contract ceiling was reported in the source used here.
- **AI infrastructure burden:** Victoria proposed requiring new data centers to secure their own renewable supply and storage and bear connection/network-upgrade costs. Separately, Reuters/Goldman reported AI-related investment-grade spreads around 115 bp versus 78 bp for the broader market and forecast $420B of hyperscaler gross debt issuance in 2027. These are distinct financing/regulatory burden observations, not one AI-cost coefficient.
- **Open-weight/public pricing:** direct-provider pages currently list GPT-6 Astra at $10 input/$50 output per 1M tokens, Claude Sonnet 5 at $2/$10, Z.ai GLM-5.3 at $1.4/$4.4, GLM-5.3-Flash at $0.15/$0.50, and Mistral Large 3 at $0.50/$1.50. River City's OpenRouter cross-check is not canonical price authority. Kimi K3's current official model page is fresh, but the numeric price table was not extractable in this run; it is therefore not silently replaced with OpenRouter pricing.
- **Software engineering deadline:** GitHub Actions removes Node 20 from runners on September 23, 2026. GitHub also plans selected Copilot model deprecations on October 19. These are documented lifecycle deadlines, not general ecosystem speculation.

## Missing views / explicit blockers

- **Conflict/strike tempo:** River City #6 remains open for ACLED/UCDP structured events plus FIRMS corroboration; required account/API access remains unresolved. Headlines are not substituted.
- **Approved normal-regime maritime band:** #3 remains open and requires a decision on conflict/disruption exclusions.
- **AI API versus open-weight/self-host cost floor:** #14/#15 remain open; measured tokens/sec, wall power, representative hardware/workload, PUE, utilization, amortization, and regional power mapping are incomplete or undecided.
- **Numeric hidden-variable heatmap:** #10 remains open and requires approved latent definitions and any judgment-bearing priors/thresholds.
- **Media-attention trend:** #34 remains open; no canonical accumulated timestamped homepage series exists in the pinned baseline, so a one-snapshot proxy is not promoted into a trend.

## Evidence

- River City baseline at `823ead34272a02419b4fbadd599db44571d799ea`: `projections/manifest.edn`, `projections/portwatch/latest.edn`, `charts/portwatch/data/latest.json`, `reports/daily/latest.md`.
- Parent model: `reports/research/2026-09-21-price-basis-v0.8.md`.
- Reuters, 2026-09-22, *Hormuz vessel traffic falls to two, data shows*: https://www.reuters.com/world/middle-east/hormuz-vessel-traffic-falls-two-data-shows-2026-09-22/
- Reuters, 2026-09-22, *Saudi Arabia restarts East-West oil pipeline, sources say*: https://www.reuters.com/business/energy/saudi-arabia-restarts-east-west-oil-pipeline-resume-exports-yanbu-sources-say-2026-09-22/
- Reuters, 2026-09-22, *Oil falls to two-week low as Gulf supply outlook improves*: https://www.reuters.com/business/energy/oil-rises-slightly-ahead-potential-us-iran-talks-2026-09-22/
- Reuters, 2026-09-21, *Moscow oil refinery output halted after Sunday drone attack, sources say*: https://www.reuters.com/business/energy/moscow-oil-refinery-output-halted-after-sunday-drone-attack-sources-say-2026-09-21/
- Reuters, 2026-09-22, *Palo Alto Networks unveils AI-powered cybersecurity service using Claude, GPT models*: https://www.reuters.com/technology/palo-alto-networks-unveils-ai-powered-cybersecurity-service-using-claude-gpt-2026-09-22/
- Reuters, 2026-09-22, *Dassault Aviation tests AI algorithms on Rafale fighter jet*: https://www.reuters.com/business/aerospace-defense/dassault-aviation-tests-ai-algorithms-rafale-fighter-jet-2026-09-22/
- Reuters, 2026-09-22, *Victoria proposes mandating new data centres source their own green power*: https://www.reuters.com/business/energy/australias-victoria-proposes-new-data-centres-must-secure-their-own-renewable-2026-09-22/
- Reuters, 2026-09-22, *Corporate bond buyers get picky with flood of AI debt*: https://www.reuters.com/legal/transactional/corporate-bond-buyers-get-picky-with-flood-ai-debt-2026-09-22/
- GitHub Changelog, Node 20 removal: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/
- GitHub Changelog, Copilot model deprecations: https://github.blog/changelog/2026-09-18-upcoming-deprecation-of-selected-github-copilot-models-in-mid-october/
- OpenAI GPT-6 Astra: https://developers.openai.com/api/docs/models/gpt-6-astra
- Anthropic Claude Sonnet 5: https://www.anthropic.com/news/claude-sonnet-5
- Z.ai pricing: https://docs.z.ai/guides/overview/pricing
- Mistral pricing: https://docs.mistral.ai/inference/pricing

## Exactly one next action

Under existing issue #33, add a stage-qualified East-West Pipeline restoration observation: September 13 shutdown, September 22 low-rate restart, target 4 million bpd, current rate unknown, and full-resumption timing uncertain. Preserve source/event timestamps and do not convert `low rate` into a guessed throughput value.
