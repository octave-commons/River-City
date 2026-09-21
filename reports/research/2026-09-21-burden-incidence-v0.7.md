# Burden Incidence — research model v0.7

**Artifact ID:** RC-UC-2026-09-21-v0.7  
**Date:** 2026-09-21  
**Status:** candidate analytical model; not a fitted forecast, score, index, or production formula  
**Parent:** `RC-UC-2026-09-21-v0.6` — `reports/research/2026-09-21-operating-friction-v0.6.md`  
**Pinned quantitative baseline:** `octave-commons/River-City@af12070661be67e43bd3fc61aa1a4900ae957289`  
**Baseline generated-at:** `2026-09-21T13:33:44.691709871Z`  
**PortWatch source-observed-through:** `2026-09-13`  
**Lineage:** `direct-source-snapshot`  
**Ledger-backed:** `false`

## Question

When workaround capacity preserves aggregate service, who bears the operating friction, and can burden ownership migrate before aggregate delivered quantity changes?

## Parent adjudication

**Consumed:** v0.6's single next action. Existing issue #35 now carries the September 21 source-scoped Gulf-of-Oman STS observations: about 2.5 million bpd expected in September versus 1.4 million bpd in August, benchmark Gulf-to-China VLCC freight above $30/barrel, and Reuters' qualitative observation that producers are offering deeper discounts and absorbing part of increased transport cost. No composite friction score or normalization was created.

**Strengthened:** H5F, the parent friction-lag hypothesis. Workaround continuity is directly co-observed with unusually high freight and extra handling. The mechanism is stronger; its causal magnitude and duration are still unidentified.

**Retained without update:** H1R/H1D observation-revision work. No new matched preliminary-to-later Kpler pair was established in this run. River City's canonical PortWatch cutoff remains September 13; fresh weekend Kpler observations are kept external to that series.

**Retained:** H2R/H2A substitution and allocation. Rerouting can preserve aggregate exports while destination-, buyer-, or contract-specific service differs.

**Retained:** H4A activation state. Announced, financed, permitted/interconnected, energized, and operational capacity remain different states.

**Still untested:** H3, API price versus useful-compute cost. River City's model-watch remains current across watched providers, but the approved self-host calibration inputs needed by issues #14/#15 are still absent.

## Model delta

v0.6 modeled operating friction between substitution and allocation. v0.7 adds an explicit **burden-incidence** layer. Friction is not only a quantity; it has an owner or absorber.

Candidate chain:

`headline/nominal capacity -> activation state -> usable capacity -> substitution & buffers -> operating friction -> burden incidence -> allocation -> recipient-specific delivered service`

Potential burden bearers are observed only when a source identifies them: producer, carrier/trader, buyer/consumer, state/insurer, or local community/ratepayer. These are descriptive actor classes, not a scoring taxonomy. Unknown burden ownership remains unknown.

## Research increment — H6I: friction has an owner

### Candidate mechanism

A system can maintain physical throughput while changing the distribution of costs and risks. The same workaround that preserves barrels, power, or compute may move economic burden between counterparties before the aggregate quantity series changes.

Current source-backed examples:

1. **Hormuz oil logistics.** Reuters reports Gulf-of-Oman STS loading around 2.5 million bpd in September versus 1.4 million bpd in August, Gulf-to-China VLCC freight above $30/barrel, and producers offering deeper discounts while absorbing part of higher transportation costs. The observation supports producer-side incidence of at least part of the friction; it does not quantify netback loss.
2. **Tanker capital structure.** Trafigura announced Volare with six operating VLCCs and eight newbuild VLCCs due 2026–2028, alongside a planned $500 million private placement before a proposed Oslo listing. Reuters notes that trading houses historically charter vessels and are exposed to high freight rates. This is consistent with a response to freight volatility, but asset-cycle opportunity, financing conditions, and fleet strategy are live rival explanations.
3. **AI infrastructure externalities and cost allocation.** The European Commission proposed efficiency disclosures for data centres above 500 kW; EU data-centre capacity is expected to rise from about 12 GW last year to 28 GW by 2030. Separately, U.S. legislation considered in September would ask state utility regulators to consider whether large users such as data centres should bear incremental grid-infrastructure costs. These are policy/disclosure observations, not evidence that a specific project already pays a newly allocated charge.

### Discriminating predictions

- If producers absorb a meaningful portion of maritime workaround costs, route-specific producer discounts or netbacks should deteriorate relative to destination benchmarks while physical export volume can remain comparatively resilient.
- If high freight is a persistent strategic driver, traders/producers should increase owned or long-controlled shipping capacity or other contractual hedges. A one-off fleet transaction is insufficient to establish this pattern.
- If grid-cost-allocation rules shift incidence toward AI infrastructure operators, project interconnection or infrastructure obligations should change without requiring an immediate change in the announced MW/GW headline.

### What would weaken H6I

- Contract-level evidence shows buyers bear nearly all additional route costs while producer and carrier economics remain stable.
- Tanker ownership grows after freight normalizes for reasons unrelated to logistics exposure, and no broader ownership/charter shift appears.
- Grid-cost rules or disclosures do not alter project obligations, capex, rates, or development behavior despite implementation.

### Confidence

**Mechanism confidence: moderate. Magnitude, timing, and cross-domain generality: low.** Oil provides a direct qualitative incidence statement plus native-unit freight evidence. Tanker ownership provides a plausible strategic response with major confounders. AI infrastructure currently supplies proposed disclosure and allocation policy rather than realized project-level incidence.

No numerical probability, burden index, cross-domain score, or causal coefficient is estimated.

## Current quantitative baseline and freshness

River City's canonical September 13 PortWatch state remains:

- Bab el-Mandeb Strait: 31 visible vessels versus descriptive prior-year median 61; -49.2%.
- Strait of Hormuz: 8 visible vessels versus descriptive prior-year median 91; -91.2%.

The baseline band is `provisional-observed-history`, not an approved normal-regime policy. The projection is a direct-source snapshot and is not ledger-backed. Dark or missing traffic is not zero.

Fresh September 21 Reuters/Kpler reporting is kept outside that canonical series: 17 trackable Hormuz commodity-vessel movements across September 19–20 versus 37 the previous weekend; 51 Bab el-Mandeb movements across the same weekend versus 57 the previous weekend. Reuters also reports transponder-off tanker traffic, so these visible counts are not total physical throughput.

## Separate theaters and transmission channels

- **Hormuz/Iran:** impaired visible passage, STS workaround, high freight, military-protected routing, and producer discounts belong to the logistics/crude channel.
- **Red Sea/Bab el-Mandeb:** impaired but much higher visible traffic than Hormuz; no visible Yanbu oil loading since September 16 in the cited Kpler observation. It remains a separate theater, not part of a combined intensity score.
- **Ukraine/Russia:** refinery damage and diesel export restrictions/reallocation belong to the refined-fuel channel. September reporting says August Russian diesel shipments to Kazakhstan, Kyrgyzstan, Mongolia and Tajikistan exceeded 370,000 tonnes and more than doubled July, while domestic deliveries fell from 5.0 to 4.7 million tonnes. These are destination and allocation observations, not a strike count.

## AI, agents, open source, software infrastructure, defense/security

- River City's September 20 model-watch has fresh official watched coverage across GPT/OpenAI, Claude/Anthropic, Kimi/Moonshot, GLM/Z.ai, Mistral and MiMo/Xiaomi. Its six material changes are OpenRouter GLM cross-check movements requiring direct-provider verification; they are not canonical provider price changes.
- GitHub's current Copilot/Actions changes continue to move agent execution toward more explicit policy surfaces, review automation and workflow protections. These are developer-infrastructure observations, not evidence of autonomous-system safety by themselves.
- U.S. and Chinese officials announced a formal AI-safety dialogue and an incident communications line, including discussion of uncontrollable agents and cyber misuse. This is publicly observable governance/security coordination. It is not evidence about classified deployments or intelligence effectiveness.
- I found no new, confirmed September 21 public AI-defense contract ceiling suitable for a quantitative procurement chart. Absence of a public announcement is not evidence about classified work.

## Countermodels and checks against misreading

- High freight plus stable exports can reflect successful adaptation, not worsening physical scarcity.
- Producer discounts may redistribute cost without reducing end-user price one-for-one; contract structure and destination matter.
- Vertical integration can be opportunistic capital allocation rather than a defensive response to freight risk.
- Efficiency disclosure can improve information without changing actual burden incidence.
- Visible vessel counts can fall while dark traffic or larger cargoes preserve more volume than the count implies.
- Nominal refinery capacity exposure is not realized lost product output.

## Missing views / explicit blockers

- **Conflict/strike tempo:** no reconciled ACLED/UCDP/FIRMS event stream is available; existing issue #6 owns the adapter boundary. Headline counts are not substituted.
- **Approved normal-regime maritime band:** issue #3 still owns conflict-period exclusion policy; descriptive prior-year IQR remains provisional.
- **AI API versus open-weight/self-host cost floor:** issues #14/#15 still need measured tokens/sec, wall power, hardware/workload calibration, PUE, utilization, amortization, and regional power inputs. No improvised range is plotted.
- **Numeric latent heatmap:** issue #10 still requires approved definitions, evidence levels, and any priors/thresholds. Qualitative assessments are not converted to scores.
- **Media-attention trend:** there is no stable accumulated sampling adapter in the current baseline; one homepage snapshot is not treated as a trend.

## Evidence

- River City current baseline at `af12070661be67e43bd3fc61aa1a4900ae957289`: `reports/daily/latest.md`, `projections/manifest.edn`, `charts/portwatch/data/latest.json`.
- Parent model: `reports/research/2026-09-21-operating-friction-v0.6.md`.
- Reuters, 2026-09-21, *Hormuz shuttles keep oil flowing, but at a high cost*: https://www.reuters.com/commentary/reuters-open-interest/hormuz-shuttles-keep-oil-flowing-high-cost-2026-09-21/
- Reuters, 2026-09-21, *Vessels trickle through Strait of Hormuz as Middle East conflict persists*: https://www.reuters.com/world/middle-east/vessels-trickle-through-strait-hormuz-mideast-tension-persists-2026-09-21/
- Reuters, 2026-09-21, *Trafigura launches Volare tanker arm, plans Oslo listing*: https://www.reuters.com/business/energy/trafigura-launches-volare-tanker-arm-plans-oslo-listing-2026-09-21/
- Reuters, 2026-09-21, *Russia boosted diesel exports to Central Asia in August, traders say*: https://www.reuters.com/business/energy/russia-boosted-diesel-exports-central-asia-august-traders-say-2026-09-21/
- Reuters, 2026-09-21, *EU to require data centres to disclose energy and water efficiency*: https://www.reuters.com/business/environment/eu-require-data-centres-disclose-energy-water-efficiency-2026-09-21/
- Reuters, 2026-09-16, *US House to vote on data center power cost bill*: https://www.reuters.com/legal/litigation/us-house-vote-data-center-power-cost-bill-2026-09-16/
- Reuters, 2026-09-21, *US, China to meet again on AI safety in two months in Shenzhen, Bessent says*: https://www.reuters.com/world/asia-pacific/us-china-meet-again-ai-safety-two-months-shenzhen-bessent-says-2026-09-21/

## Exactly one next action

Under existing issue #35, add one matched same-route/date evidence packet linking quoted VLCC freight to an independently reported producer discount or netback observation, preserving both instruments in native units and leaving the relationship uninterpreted if contract comparability is unknown. This is the smallest test that can begin to discriminate **where** workaround friction is absorbed without inventing a burden index.
