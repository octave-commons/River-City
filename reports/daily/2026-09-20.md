# River City Daily: PortWatch

Generated: `2026-09-23T14:18:23.210349986Z`  
Source coverage through: `2026-09-20`

## Status

| Chokepoint | Date | Visible vessels | Prior-year median | Deviation | Baseline n |
|---|---:|---:|---:|---:|---:|
| Bab el-Mandeb Strait | 2026-09-20 | 26 | 62.0 | -58.1% | 7 |
| Strait of Hormuz | 2026-09-20 | 1 | 91.0 | -98.9% | 7 |

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
