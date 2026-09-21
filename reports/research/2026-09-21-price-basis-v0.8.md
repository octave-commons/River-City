# Price Basis Mediates Burden - research model v0.8

**Artifact ID:** RC-UC-2026-09-21-v0.8  
**Date:** 2026-09-21  
**Status:** candidate analytical model; not a fitted forecast, score, index, or production formula  
**Parent:** `RC-UC-2026-09-21-v0.7` - `reports/research/2026-09-21-burden-incidence-v0.7.md`  
**Pinned quantitative baseline:** `octave-commons/River-City@425c0c71a9f8b3bf55405d33adfd15bdc403081e`  
**Baseline generated-at:** `2026-09-21T13:59:58.588387909Z`  
**PortWatch source retrieved-at:** `2026-09-21T13:59:25.738422311Z`  
**PortWatch source-observed-through:** `2026-09-13`  
**Lineage:** `direct-source-snapshot`  
**Ledger-backed:** `false`

## Question

When a workaround keeps physical service moving at higher operating cost, what evidence is actually sufficient to say which actor bears that cost?

## Parent adjudication

**Consumed with a negative result:** v0.7's single next action asked for one matched same-route/date evidence packet linking quoted Gulf-to-China VLCC freight to an independently reported producer discount or netback. The current search recovered a route-specific freight observation above $30/barrel and a qualitative Reuters statement that producers are offering deeper discounts and absorbing some transportation cost. It did **not** recover a comparable numeric producer netback for the same route, cargo, contract basis, and pricing period.

**Rejected as a substitute:** Saudi Arab Light official selling-price differentials cannot be treated as the missing matched netback. Public October pricing includes Arab Light to East Asia at $2/barrel below Oman/Dubai, Western Europe at $2.15 below ICE Brent, and Mediterranean Europe at $2.35 below ICE Brent. Those are monthly benchmark differentials, not same-route realized netbacks, and their commercial basis differs from a quoted Gulf-to-China freight cost. Subtracting one from the other would manufacture an incidence estimate.

**Strengthened:** H6I, that operating friction has an owner or absorber, remains plausible because Reuters directly says producers absorb part of the higher transportation cost. The evidence is qualitative on incidence and quantitative on freight, not quantitative on the amount borne by producers.

**Retained:** H5F, operating friction can rise while workaround throughput rises. Gulf-of-Oman ship-to-ship loading is reported around 2.5 million bpd for September versus 1.4 million bpd in August while benchmark Gulf-to-China VLCC freight is above $30/barrel.

**Retained:** H2R/H2A, allocation and substitution can preserve aggregate service while recipient outcomes differ. Russia's August diesel shipments to selected Central Asian recipients rose sharply even as domestic deliveries fell from 5.0 to 4.7 million tonnes.

**Retained:** H4A, activation state remains distinct from announced capacity. Qatar says 17% of its LNG capacity is offline and later North Field East expansion phases may be delayed by equipment-delivery disruption; EU data-centre capacity is projected to grow from about 12 GW to 28 GW by 2030 while new disclosure policy focuses on facilities above 500 kW.

**Retained without new calibration:** H3, API price versus useful-compute cost. Direct-provider public prices can be observed, but River City #14/#15 still block a lawful self-host electricity/all-in floor because measured throughput, wall power, hardware/workload calibration, PUE, utilization, amortization, and regional power inputs are incomplete or undecided.

## Model delta

v0.7 placed `burden incidence` after operating friction. v0.8 inserts the **commercial price/contract basis** that determines whether a public price observation can identify the burden bearer at all.

Candidate chain:

`headline/nominal capacity -> activation state -> usable capacity -> substitution & buffers -> operating friction -> contract / price basis -> burden incidence -> allocation -> recipient-specific delivered service`

This layer is not a formula. It is an evidence boundary. Examples of distinct bases that must not be collapsed without source support include freight quoted per barrel, FOB/CIF/DES terms, official selling-price differentials to a benchmark, spot discounts, insurance premiums, realized producer netbacks, buyer landed cost, state subsidy, and regulated grid-interconnection obligations.

## Research increment - H7B: incidence is contract-mediated

### Candidate mechanism

A physical workaround can impose a real extra cost while public price observations leave its incidence ambiguous. Who pays depends on contract terms, benchmark construction, timing, freight responsibility, insurance, hedging, and explicit state or counterparty absorption. Therefore a freight spike plus a benchmark differential is not by itself a producer-margin estimate or buyer-cost estimate.

### Current evidence

1. **Hormuz workaround and freight:** Reuters reports Gulf-of-Oman STS loading around 2.5 million bpd in September versus 1.4 million bpd in August, Gulf-to-China VLCC freight above $30/barrel, and producers offering deeper discounts while absorbing part of increased transportation cost. This establishes some producer-side incidence qualitatively, but no matched numeric netback.
2. **Saudi OSP counterexample:** October Arab Light pricing is published as regional differentials to distinct benchmarks. Those values are commercially meaningful but cannot be paired arithmetically with the current quoted freight without contract and timing evidence.
3. **Recipient reallocation:** Russian diesel shipments to Mongolia, Kyrgyzstan, Tajikistan, and Kazakhstan rose substantially in August while domestic deliveries declined. This demonstrates allocation movement, not who bears the economic burden.
4. **AI infrastructure policy:** EU data-centre disclosure rules can reveal energy and water efficiency while leaving actual grid-cost incidence project-specific. Transparency is not the same thing as a charge or subsidy.
5. **AI API pricing:** OpenAI, Anthropic, and Mistral public list prices are observable commercial offers. They are not internal cost disclosures. Cache, batch, priority, regional inference, negotiated commitments, and infrastructure externalities can change economic incidence without changing a headline token rate one-for-one.

### Discriminating predictions

- A source that reports the **same cargo/grade, route or destination, pricing period, and contract basis** should allow a directionally meaningful burden inference where separate freight and seller/buyer price observations do not.
- If producers bear a material part of the workaround cost, sources should explicitly show weaker seller realization, wider seller discount, or stated producer absorption while the buyer's relevant delivered benchmark moves less than freight alone would imply.
- If buyers bear most of the friction, landed cost or buyer-paid freight/insurance should rise while seller realization remains comparatively stable.
- If a state, insurer, utility, or other intermediary absorbs the burden, an explicit guarantee, subsidy, regulated allocation, premium structure, or project obligation should appear without requiring the physical quantity series to change first.

### What would weaken H7B

- Repeated matched transactions show that public benchmark differentials plus route freight reliably identify incidence without additional contract information.
- Contract terms are stable and known, yet burden shifts materially for reasons unrelated to price basis or freight responsibility.
- In AI infrastructure, project-level evidence shows grid and externality costs are economically immaterial relative to compute cost across the relevant deployments, weakening the need to track incidence separately.

### Confidence

**Mechanism confidence: moderate. Quantitative magnitude and cross-domain generality: low.** The negative result is informative: the sought matched pair was not found, and the most obvious public substitute is not comparable. The model therefore becomes more explicit about what evidence is missing rather than creating a proxy coefficient.

No numerical probability, burden index, netback estimate, cross-domain normalization, or causal coefficient is estimated.

## Current quantitative baseline and freshness

River City's pinned September 13 PortWatch state remains:

- **Bab el-Mandeb Strait:** 31 visible vessels versus descriptive prior-year median 61; -49.2%.
- **Strait of Hormuz:** 8 visible vessels versus descriptive prior-year median 91; -91.2%.

The baseline is `provisional-observed-history`, not an approved normal-regime policy. The projection is a direct-source snapshot and is not ledger-backed. Dark or missing traffic is not zero.

Fresh Reuters/Kpler reporting remains external to that canonical daily series: 17 visible commodity-vessel movements across Hormuz on September 19-20 versus 37 the previous weekend; 51 through Bab el-Mandeb versus 57 the previous weekend. Those are two-day aggregates and are not interpolated or averaged into the River City daily series.

## Separate theaters and transmission channels

- **Hormuz/Iran:** extreme visible passage impairment, STS workaround, high freight, military-protected routing, and producer discounting belong primarily to logistics/crude and financial-friction channels.
- **Red Sea/Bab el-Mandeb:** visible traffic remains impaired but materially higher than Hormuz; it remains a separate theater and route-risk problem.
- **Ukraine/Russia:** refinery damage, export restrictions, and destination-specific diesel reallocations belong to the refined-fuel channel. No headline-derived strike count is created.
- **Qatar LNG:** 17% of capacity reported offline and possible expansion delay belong to the LNG/gas and activation-state channels, not crude.

## AI, agents, open source, software infrastructure, defense/security

- **Open source / weights:** Qwen released Qwen-Image-2.1 on September 20 with downloadable weights and day-zero support in Diffusers, ComfyUI, vLLM-Omni and SGLang. The repository calls it open-source, but the weights are under the Qwen Research License Agreement rather than Apache/MIT; this artifact therefore describes it as weight-available/research-licensed instead of assuming permissive open source.
- **Agent security:** AIR Security's Plugin4Shell report demonstrates a failure mode where a nominal SHA pin did not guarantee the resolved plugin content. Anthropic and OpenAI report patched agent versions through the disclosure; vendor states for other products should be checked before operational use. This is supply-chain/identity evidence, not an incident-rate statistic.
- **Developer infrastructure:** GitHub plans to deprecate Gemini 3.7 Flash, GPT-5.5, GPT-5.4, GPT-5.4 mini, GPT-5 mini and Grok 4.5 across Copilot on October 19, 2026. Pinned integrations should be audited before that date; this is a documented lifecycle deadline.
- **AI national security:** U.S. and Chinese officials agreed to continue a formal AI-safety dialogue and establish an incident line, with public discussion including uncontrollable agents and cyber threats. This is observable governance activity, not evidence about classified deployments, budgets, or operational effectiveness.
- **Model economics:** River City's September 20 watcher reports fresh official-page coverage across GPT/OpenAI, Claude/Anthropic, Kimi/Moonshot, GLM/Z.ai, Mistral and MiMo/Xiaomi. Its six material changes are OpenRouter GLM cross-check movements requiring direct-provider verification; they are not canonical provider price changes.

## Countermodels and checks against misreading

- Higher STS throughput can represent successful adaptation while still raising cost and fragility.
- Lower crude prices on a given day do not prove route friction disappeared; product inventories, substitution, demand and diplomacy can change the benchmark independently.
- An OSP discount may reflect regional benchmark conditions, market share strategy, crude quality, customer feedback, or demand, not just freight absorption.
- A buyer can be protected by a term contract while a seller absorbs freight, or the reverse; the public benchmark alone does not reveal the contract.
- Increased regional diesel exports do not imply domestic demand was fully served, nor do lower domestic deliveries prove a single causal mechanism.
- A data-centre efficiency disclosure requirement is not a cost-allocation rule and does not prove a particular operator pays a new grid charge.
- Weight availability under a research license is not the same legal condition as Apache/MIT open-source licensing.

## Missing views / explicit blockers

- **Conflict/strike tempo:** River City #6 still owns ACLED/UCDP structured-event adapters plus FIRMS corroboration. Required source access is unresolved; headline counts are not substituted.
- **Approved normal-regime maritime band:** #3 still owns disruption-exclusion policy. The historical IQR remains descriptive/provisional.
- **AI API versus open-weight/self-host cost floor:** #14/#15 still need measured tokens/sec, wall power, hardware/workload calibration and approved accounting assumptions. Public list-price charts therefore remain one side of the comparison.
- **Numeric hidden-variable heatmap:** #10 still requires approved definitions, evidence levels and judgment-bearing priors/thresholds. Qualitative interpretation is not converted to a score.
- **Media-attention trend:** #34 exists, but no canonical accumulated snapshot series is present in the pinned baseline. One manually sampled homepage is not plotted as a trend.

## Evidence

- River City pinned baseline at `425c0c71a9f8b3bf55405d33adfd15bdc403081e`: `projections/manifest.edn`, `projections/portwatch/latest.edn`, `charts/portwatch/data/latest.json`, `reports/daily/latest.md`.
- Parent model: `reports/research/2026-09-21-burden-incidence-v0.7.md`.
- Reuters, 2026-09-21, *Hormuz shuttles keep oil flowing, but at a high cost*: https://www.reuters.com/commentary/reuters-open-interest/hormuz-shuttles-keep-oil-flowing-high-cost-2026-09-21/
- Reuters, 2026-09-21, *Vessels trickle through Strait of Hormuz as Middle East conflict persists*: https://www.reuters.com/world/middle-east/vessels-trickle-through-strait-hormuz-mideast-tension-persists-2026-09-21/
- Reuters, 2026-09-21, *Russia boosted diesel exports to Central Asia in August, traders say*: https://www.reuters.com/business/energy/russia-boosted-diesel-exports-central-asia-august-traders-say-2026-09-21/
- Reuters, 2026-09-21, *QatarEnergy says Hormuz crisis may delay LNG expansion projects*: https://www.reuters.com/business/energy/qatarenergy-says-hormuz-crisis-may-delay-lng-expansion-projects-2026-09-21/
- Reuters, 2026-09-21, *EU to require data centres to disclose energy and water efficiency*: https://www.reuters.com/business/environment/eu-require-data-centres-disclose-energy-water-efficiency-2026-09-21/
- Reuters, 2026-09-21, *US, China to meet again on AI safety in two months in Shenzhen, Bessent says*: https://www.reuters.com/world/asia-pacific/us-china-meet-again-ai-safety-two-months-shenzhen-bessent-says-2026-09-21/
- AIR Security, 2026-09-17, *Plugin4Shell*: https://www.air.security/blog-posts/plugin4shell
- QwenLM, 2026-09-20, *Qwen-Image-2.1*: https://github.com/QwenLM/Qwen-Image-2.1
- GitHub Changelog, 2026-09-18, *Upcoming deprecation of selected GitHub Copilot models in mid-October*: https://github.blog/changelog/2026-09-18-upcoming-deprecation-of-selected-github-copilot-models-in-mid-october/
- OpenAI, Anthropic, and Mistral direct model/pricing pages captured in the publication source register.
- Argaam, 2026-09-03, *Saudi Aramco sets Arab crude OSP for October*: https://www.argaam.com/en/article/articledetail/id/1934034

## Exactly one next action

Under existing issue #35, add one evidence packet only when a source explicitly states a Gulf crude cargo/grade, destination or route, pricing period, commercial price basis, and who is responsible for freight or insurance. Pair it with the route freight observation in native units; if those fields cannot be established from public evidence, record the comparison as **not comparable** rather than inferring a netback or burden share.
