# Operating Friction — research model v0.6

**Artifact ID:** RC-UC-2026-09-21-v0.6  
**Date:** 2026-09-21  
**Status:** candidate analytical model; not a fitted forecast, score, or canonical production formula  
**Parent:** `RC-UC-2026-09-20-v0.5` (`reports/research/2026-09-20-activation-state-v0.5.md`)  
**Pinned quantitative baseline:** `octave-commons/River-City@c930dedfb0612065e809f2c178e44e9563d694e6`.

## Research question

When a system preserves delivered service by routing around a damaged or unready primary path, what evidence distinguishes genuine normalization from continuity purchased through extra operating friction?

## Stable baseline and evidence boundary

River City's current projection was generated at `2026-09-20T16:12:45.366567657Z` from source observations through 2026-09-13. The PortWatch lineage remains `direct-source-snapshot` and `ledger-backed? false`. The canonical 2026-09-13 observations remain Hormuz 8 visible vessels against a prior-year same-calendar-day median of 91 (-91.2%), and Bab el-Mandeb 31 against 61 (-49.2%). The historical median/IQR remains descriptive context, not an approved normal-regime policy.

Fresh reporting is not spliced into that projection. Reuters reports 17 commodity-vessel transits through Hormuz across the September 19–20 weekend, versus 37 the previous weekend and a pre-war reference around 125 large commercial vessels per day. Bab el-Mandeb had 51 weekend transits versus 57 the previous weekend. Those are separate source-scoped observations; exact daily splits were not published in the cited report and AIS-dark traffic remains outside visible counts.

## Parent adjudication

**Consumed:** v0.5's sole next action. Existing issue #31 was seeded during this run with stage-qualified Nscale and Anthropic observations. The stronger Nscale primary source is its September 18 SEC S-1: the named Monarch campus has a runway scalable to over 8 GW gross / over 6.5 GW IT-load power, with an initial 2 GW gross / 1.37 GW IT-load expected online in H1 2028 and expansion planned toward 2031. Anthropic's planned 2.16-GW Australian inference campus remains subject to FIRB approval, with first capacity planned from 2027 and energized MW unknown.

**Retained:** H1R revision provenance; H1D delayed-observability explanation remains untested. No new matched preliminary→later Kpler pair was located today.

**Strengthened:** H2R substitution/reallocation. Reuters commentary reports STS transfers near Oman rising from about 1.4 mb/d in August to about 2.5 mb/d in September as producers route around damaged or risky infrastructure.

**Strengthened:** H4A activation state. The Nscale S-1 provides a primary-source decomposition of capacity runway versus expected-online stages rather than treating a headline GW number as already energized capacity.

**Still untested:** H3 public API price versus useful-compute cost. River City still lacks the approved open-weight throughput/wall-power and all-in accounting calibration governed by issues #14 and #15.

## Model delta: add operating friction

The candidate chain becomes:

`headline / nominal capacity -> activation state -> usable capacity -> substitution & buffers -> operating friction -> allocation -> recipient-specific delivered service`

"Operating friction" is deliberately **not** a score. It is a family of separately observed burdens attached to the path that actually delivers service: freight per barrel, war-risk insurance, discounts, escort/security requirements, extra handling/STS operations, inventory draw, backup-generation commitments, water/energy intensity, or similar source-native units. The model predicts that delivered quantity can stabilize before these burdens normalize.

The new layer explains a recurring failure mode in dashboards: a recovering service quantity may look like normalization even when the system is expending much more money, capacity, coordination, or resilience margin to produce it.

## H5F — workaround continuity can precede cost normalization

**Candidate claim:** when substitute routes or resources are absorbing a primary-path failure, service quantity can stabilize or recover while route-specific operating burdens remain elevated.

**Evidence today:** Reuters reports STS crude transfers near Oman around 2.5 mb/d in September versus 1.4 mb/d in August, while VLCC freight costs are reported above $30/bbl. Reuters also reports only 17 visible commodity-vessel Hormuz transits across the September 19–20 weekend. The combination is consistent with concentrated, high-friction workarounds carrying material cargo despite very sparse visible movements.

**Prediction:** if H5F is useful, a later recovery in physical route availability should be followed by some observable reduction in workaround intensity and/or route-specific costs before the system is reasonably described as normalized. Quantity recovery without burden relief should be labeled continuity, not normalization.

**Disconfirming evidence:** freight/insurance/handling burdens normalize while the primary route remains impaired and no hidden subsidy/cost transfer is found; or delivered service recovers without measurable reliance on substitute routing/buffers. A single lower freight quote is not sufficient because vessel class, route, contract timing and source unit matter.

**Confidence:** moderate as a qualitative mechanism, low for magnitude or timing. The current evidence is source-specific and does not identify a universal elasticity or threshold.

## Countermodel and mitigation evidence

The strongest counterweight is portfolio diversity. Reuters reports a roughly 36 Mt Middle East LNG reduction this year but only about 5 Mt net global LNG supply loss, attributing the gap to new capacity and flexible shipping. This shows that substitution can genuinely absorb a large origin shock at the aggregate level. It does not prove friction is absent: buyers are changing suppliers/routes and price exposure can shift across regions.

For AI infrastructure, today's EU proposal to require large data centers to disclose energy/water efficiency and local-water-stress information illustrates a different operating-condition channel. Planned European capacity is reported to rise from about 12 GW to 28 GW by 2030; the proposal does not cap use. The observation supports keeping resource intensity and operating constraints separate from headline GW. It does not establish that EU rules will reduce or raise delivered compute.

Amazon's multi-year Generac agreement is another source-native example of explicit resilience spending, not a defense/national-security contract. It supports the general idea that usable service can require dedicated backup capacity, but no cross-domain cost coefficient is inferred.

## Theater and transmission separation

- **Hormuz/Iran:** visible maritime traffic remains severely impaired while oil is partly rerouted through STS operations and AIS-dark movement.
- **Red Sea/Bab el-Mandeb:** a separate theater with materially higher visible traffic than Hormuz, but still exposed to route/security constraints. It is not merged into a Middle East intensity score.
- **Ukraine/Russia:** the September 20 Moscow refinery strike is a separate theater feeding the refined-fuel conversion channel. Russian authorities reported the refinery was damaged; Ukraine had not formally commented in the cited Reuters report. Nameplate/refinery exposure is not equated with realized diesel loss.

Shared transmission channels can couple the theaters without collapsing them: refined fuels, shipping/freight, insurance, inventories and replacement sourcing.

## AI / agents / software / national-security lens

The September 18 Gemini security-evaluation incident remains evidence about agent authority boundaries, credential access and external network scope, not an incident-rate estimate. Separately, GitHub says Node 20 will be removed from Actions runners on **2026-09-23**; pinned actions and self-hosted environments that still depend on Node 20 are therefore a concrete software-engineering deadline.

I found no new public AI-defense contract ceiling today that warrants a procurement chart. Public AI-policy and national-security discussions are reported separately from classified work; no classified revenue or effectiveness is inferred from silence.

## Dashboard gaps preserved

- **Conflict/strike tempo:** unavailable until River City #6's structured theater/side event adapter is operating; headlines are not counted as strikes.
- **Approved maritime normal-regime band:** issue #3 still owns the baseline-policy decision; the current median/IQR is descriptive history.
- **Open-weight self-host floor / all-in range:** #14/#15 still require measured tokens/sec, wall power, hardware/workload specification, PUE, utilization and amortization decisions.
- **Numeric hidden-variable heatmap:** issue #10 remains the policy gate for definitions/evidence/priors.
- **Operating-friction series:** issue #35 already owns maritime insurance/P&I/freight observations; no duplicate issue is needed.

## Exactly one next action

Under existing River City **#35**, add the September 21 Reuters source-scoped workaround observation: Oman-area STS transfers about **2.5 mb/d in September versus 1.4 mb/d in August**, and the reported **>$30/bbl VLCC freight** burden, preserving route, vessel class, source date, original units and the fact that this is a freight observation rather than a universal energy-cost index.

A successor should cite this v0.6 artifact as parent, adjudicate H5F with new route/freight/insurance evidence or a null result, and preserve adverse evidence. No work is claimed between scheduled executions.
