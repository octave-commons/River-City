# River City

River City is a data-driven observability and inference system for tracking how geopolitical conflict propagates through energy markets, defense demand, infrastructure constraints, and AI economics.

It separates **observations** from **interpretations** and **latent estimates**. The intended daily output is a source-backed briefing plus chart-ready data and Vega-Lite specifications.

## Architecture

Four high-level namespaces:

- `river-city.law` — Malli schemas, validators, invariants, evidence requirements.
- `river-city.shape` — normalization and structural transformations.
- `river-city.domain` — scoring, baselines, compounds, latent inference.
- `river-city.infra` — API adapters, persistence, rendering, scheduled execution.

The DSL is EDN-first. No macro language is required to describe sources, signals, compounds, charts, or reports.

## Core modeling rule

Do not collapse geopolitical theaters into pressure channels.

- **Theaters**: Hormuz / Iran, Ukraine / Russia, Red Sea, Taiwan Strait, etc.
- **Transmission channels**: energy, defense, logistics, finance, sanctions, AI infrastructure, AI public pricing.

A theater can contribute to multiple channels; a channel can aggregate evidence from multiple theaters.

## First vertical slice

`IMF PortWatch -> normalized chokepoint observations -> descriptive historical projection -> Vega-Lite charts + GeoJSON map -> daily report`

## Commands

```bash
bb validate
bb test
bb ingest
bb normalize
bb score
bb render
bb daily
bb model-watch
```

`nbb` is reserved for Node/JS interop where it is materially useful, especially chart rendering and APIs with mature JS clients.

## Stable generated state

The scheduled PortWatch vertical slice writes repository-readable state for downstream briefings and tools:

- `projections/manifest.edn` and `projections/manifest.json` — read these first. They identify the latest successful projection, coverage, lineage mode, artifacts, and unresolved series.
- `projections/portwatch/latest.edn` and `.json` — the current 180-day projection for Hormuz and Bab el-Mandeb, including source coverage, latest values, and a clearly provisional observed-history band.
- `charts/portwatch/data/latest.json` — chart-ready rows.
- `charts/portwatch/*.vl.json` — Vega-Lite passage, deviation, vessel-mix, and map specifications.
- `maps/portwatch/latest.geojson` — map-ready latest chokepoint state using approximate reference anchors.
- `reports/daily/latest.md` and dated reports — concise generated status and artifact links.

The direct source snapshot is operational scaffolding, not a replacement for Clio/Foresight event hosting. `projections/manifest.edn` states whether a projection is ledger-backed. Source laws, normalization, baseline policy, and scoring semantics remain reviewed code/config changes; generated source observations and disposable projections may advance through the scheduled job.

The observed-history band is not the approved normal-regime baseline. It compares each date with the same calendar day across all prior available years and exposes median/IQR plus sample count. River City issue #3 remains the policy gate for conflict-period exclusion rules.

## Model economics watch

The daily model-economics workflow monitors GPT, Claude, Kimi, GLM, Mistral, and MiMo. It keeps official provider pages authoritative, uses OpenRouter only as a machine-readable cross-check, and follows official Hugging Face organizations for open-weight releases. The first run seeds a baseline; later runs comment on issue #13 only for material pricing, packaging, lifecycle, capability-frontier, or open-weight changes.

See [`docs/model-watch.md`](docs/model-watch.md) for the evidence hierarchy, materiality law, generated state, and alert contract.

## License

GNU GPL v3 or later. See `LICENSE`.

## Issue labels

Every issue has exactly one architecture lane:

- `lane:law` — schemas, validators, invariants, contracts.
- `lane:shape` — normalization and structural transformations.
- `lane:domain` — baselines, scoring, compounds, inference.
- `lane:infra` — adapters, persistence, rendering, automation.

Issues that require user input are explicitly marked:

- `input:decision` — modeling/policy choice needs user approval.
- `input:api-key` — an API key or token must be provisioned.
- `input:account` — an external account/registration step is required.
- `input:hardware` — calibration needs representative hardware or access.
- `input:repo` — repository creation/authorization is required.

No `input:*` label means the issue should be executable without user intervention under the current design.

Create labels before materializing issues:

```bash
bb labels -- owner/repo
bb issues -- owner/repo
```

The actionable queue is also stored in `backlog/user-input.edn` and can be printed with:

```bash
bb user-input
```
