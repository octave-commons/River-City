# River City Daily: PortWatch

![Bab el-Mandeb Strait visible vessel passages](../../charts/research/2026-10-25-dc78e5e84427aeb2.svg)

**Establishes:** Canonical measurement coverage is too old, invalid, or insufficient for a current trend claim.

**Does not establish:** an approved normal regime, true unobserved traffic, deliverability, or a causal explanation.

**Question:** Does a newer independent observation confirm or contradict the last measured level?

**Diagnostic flags:** displaced, regime-unknown, stale, unexplained. Source age: 7 days.

Research attempts this report: 0; retained attempts: 2. Suppressed: [{:id "portwatch/chokepoint4", :reason :cooldown} {:id "portwatch/chokepoint6", :reason :cooldown}].


Retained research: [`portwatch/chokepoint4` completed](../../data/research/research-aed82fa1ddd0eb1e7ccb389734511822c94cacad7bd06034554892e68ab4f20e/result.edn); [`portwatch/chokepoint6` completed](../../data/research/research-bba0aaf799f16362442220e4335a1b79e489554f46e80377e30ed38c011ca2fb/result.edn).

Literature metadata supplies leads, not verified experimental findings. Null searches remain valid; failed searches remain failures. Proposed indicators/tests do not modify production definitions. Foresight/Clio event admission is explicitly pending.

Generated: `2026-10-25T16:30:09.483005273Z`

Source coverage through: `2026-10-18`

## Status

| Chokepoint | Date | Visible vessels | Prior-year median | Deviation | Baseline n |
|---|---:|---:|---:|---:|---:|
| Bab el-Mandeb Strait | 2026-10-18 | 32 | 46.0 | -30.4% | 7 |
| Strait of Hormuz | 2026-10-18 | 4 | 87.0 | -95.4% | 7 |

## Stable artifacts

- Projection: [`projections/portwatch/latest.edn`](../../projections/portwatch/latest.edn)
- JSON projection: [`projections/portwatch/latest.json`](../../projections/portwatch/latest.json)
- Passage chart: [`charts/portwatch/passage-vs-history.vl.json`](../../charts/portwatch/passage-vs-history.vl.json)
- Deviation chart: [`charts/portwatch/deviation-from-history.vl.json`](../../charts/portwatch/deviation-from-history.vl.json)
- Vessel mix chart: [`charts/portwatch/cargo-mix.vl.json`](../../charts/portwatch/cargo-mix.vl.json)
- Map spec: [`charts/portwatch/map.vl.json`](../../charts/portwatch/map.vl.json)
- GeoJSON: [`maps/portwatch/latest.geojson`](../../maps/portwatch/latest.geojson)

## Interpretation constraints

- The baseline is the same calendar day across all prior available years, shown as median and IQR. It is descriptive and provisional, not an approved normal-regime policy. See issue #3.
- This repository snapshot is directly derived from the upstream source. Clio/Foresight event hosting remains the intended canonical ledger path; the manifest marks this snapshot as not yet ledger-backed.
- Missing or dark traffic is not converted into zero. Source revisions should be retained by the eventual Clio history.
