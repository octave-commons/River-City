# River City Ledger

The River City briefing is a **reader of accumulated state**, not the authority that invents new state on every scheduled run.

## Contract

River City separates four things:

1. **Code and law** — schemas, adapters, transforms, scoring rules, and projection logic. These change through reviewed code changes.
2. **Ledger events** — immutable source-backed observations written by background collectors. Data jobs may append these directly without a PR.
3. **Projections** — deterministic derived state rebuilt from ledger events by version-controlled code. `latest.edn` files may be replaced by jobs because they are reproducible views, not authority.
4. **Briefings/charts** — presentation over projections plus explicitly labeled breaking-news corroboration. The briefing must not silently change formulas, weights, schemas, or normalization rules.

The rule is simple:

> Background jobs execute code. They do not write new code.

A scheduled job may fetch observations, append events, rebuild projections, and commit generated data. A PR is required when schemas, adapters, scoring formulas, chart definitions, or other executable interpretation changes.

## Layout

```text
ledger/
  events/
    YYYY/MM/DD/<source>/<event-id>.edn
projections/
  maritime/latest.edn
  energy/latest.edn
  defense/latest.edn
  ai-economics/latest.edn
reports/
  daily/YYYY-MM-DD.md
charts/
  ...
```

`ledger/events` is append-only. Corrections are new events that supersede earlier events; collectors must never rewrite historical event files.

`projections/*/latest.edn` is derived and replaceable. Its meaning is fixed by the producer code revision recorded in the projection.

## Event identity and idempotency

Each event must contain enough provenance to answer:

- What source produced this observation?
- What source record or query did it come from?
- When was the underlying fact observed?
- When did River City ingest it?
- Which repository revision produced the event?
- Has this exact normalized payload already been recorded?

Event IDs are content-addressed from stable source identity plus normalized payload. A repeated fetch of unchanged source data therefore becomes a no-op. If a source corrects a record, the normalized payload changes and a new event is appended.

## Projection determinism

A projection is a pure function of:

```text
ledger events + config + code revision -> projection
```

Projection output must not depend on model prose, wall-clock randomness, or hidden conversational state. When judgment is required—weights, priors, baseline exclusions—it lives in reviewed config/code and is surfaced as `input:decision` work.

## Briefing behavior

The Daily Signal Briefing should first read the latest River City projections and use them as its stable quantitative base. Web/news search is then used for:

- breaking events newer than the last successful projection,
- source corroboration,
- missing-series diagnosis,
- qualitative context and competing interpretations.

If the briefing disagrees with a projection, it should state the discrepancy instead of silently recomputing the metric differently.

## Commit policy

Generated ledger/projection commits may land directly from the bot when only generated data changes. Code or config changes should use a branch/PR.

Suggested generated-data commit prefix:

```text
ledger: update observations
```

Suggested code-change prefix:

```text
feat: ...
fix: ...
```

## Initial collector

The first automatic collector is IMF PortWatch because it is keyless and directly supports the Hormuz and Bab el-Mandeb maritime series. Additional collectors should implement the same event contract rather than adding ad-hoc files.