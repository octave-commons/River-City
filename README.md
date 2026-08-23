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

`IMF PortWatch -> normalized chokepoint observations -> seasonal baseline -> maritime pressure compound -> passage-vs-normal chart -> daily report`

## Commands

```bash
bb validate
bb test
bb ingest
bb normalize
bb score
bb render
bb daily
```

`nbb` is reserved for Node/JS interop where it is materially useful, especially chart rendering and APIs with mature JS clients.

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
