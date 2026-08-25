# Refined-fuels stress series

The refined-fuels series tracks downstream fuel stress separately from crude-price stress and maritime throughput.

## Components

All component inputs are normalized to `0..100` before aggregation.

| Component | Provisional weight | Intended evidence |
| --- | ---: | --- |
| Distillate inventory stress | 0.35 | inventories relative to seasonal baseline |
| Refined-product flow gap | 0.25 | imports/exports or observed product flows relative to normal |
| Refining-margin stress | 0.20 | crack/refining-margin deviation from baseline |
| Refinery-outage stress | 0.20 | confirmed/corroborated refinery capacity outages |

Weights are modeling assumptions and require explicit approval before they are treated as stable River City policy.

## Missing data

Missing components are dropped and the remaining weights are renormalized. Missing evidence is never coerced to zero stress. If no components are available for a period, the series value is unknown.

## Presentation

The daily report renders `:chart/refined-fuels-stress` beside the chokepoint passage chart. Product-level series should include at least diesel/distillates, jet fuel, and gasoline when evidence coverage permits.

Every point should preserve source coverage, confidence, and whether the underlying observation is confirmed or provisional.
