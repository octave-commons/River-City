# River City ledger integration

River City is a **domain consumer** of an event ledger, not the owner of a second event-sourcing kernel.

The stable split is:

1. **River City** owns source-specific data shapes, normalization, scoring, and projections.
2. **Clio** (from `open-hax/eta-mu/packages/clio`) owns immutable event identity, schema-history validation, append admission, physical ledger concurrency, causal/stream ordering, partition union, canonicalization, and projection replay order.
3. **Katamorph** owns the resource-manifest grammar used to discover River City ledgers and projections inside a Foresight workspace.
4. **Foresight** is the host constellation. It pins River City, eta-mu, and Katamorph as sibling sources and owns the `.ημ/river-city/` provenance material plus deterministic background-job wiring.
5. **Daily Signal Briefing** reads accumulated River City projections. It may add breaking-news context, but it does not invent a new ledger implementation or scoring formula on each run.

This is intentionally parallel to Foresight architecture archaeology: both workloads use Katamorph resources to reference Clio ledger partitions, but they record different event catalogs and derive different projections.

## Authority

The authority chain is:

```text
external source
  -> River City normalization/data law
  -> Clio event(s)
  -> Clio canonical history
  -> River City pure projection
  -> chart/report/briefing
```

Physical ledger files and resource manifests are not semantic ordering authorities. A correction to an upstream source record is another Clio stream revision, causally linked to the previous revision. Historical revisions remain facts; River City's current projection selects the newest revision while preserving all contributing event ids.

## PortWatch contract

`river-city.shape.portwatch` defines the normalized PortWatch observation shape and provider-field boundary. `river-city.domain.portwatch` defines stable stream/subject identity and the pure current-record projection.

River City deliberately does **not** implement:

- canonical EDN hashing;
- event UUID generation;
- append locking or filesystem publication;
- historical schema storage;
- duplicate/collision admission;
- causal DAG validation;
- physical partition union;
- deterministic topological replay.

Those are Clio responsibilities.

## Foresight layout

The intended host layout mirrors archaeology:

```text
Foresight/
  river-city/                 # octave-commons/River-City submodule
  eta-mu/packages/clio/       # shared event-sourcing kernel
  katamorph/                  # shared resource contracts
  .ημ/
    archaeology/              # code-archaeology Clio ledgers/resources
    river-city/               # news/market/PortWatch Clio ledgers/resources
```

A River City Katamorph resource references ledger paths, schema catalog/history, and projection identity. Accumulating observations live only in Clio newline-delimited EDN ledgers; manifests remain reference/index objects.

## Background jobs

Background jobs belong to the Foresight host because that checkout supplies all three sibling dependencies. A job may:

- fetch external observations;
- normalize them with River City code;
- construct/append events with Clio;
- canonicalize the referenced partitions;
- rebuild River City projections;
- commit generated `.ημ/river-city` data/projection artifacts when changed.

A background job must not generate or edit schemas, adapters, normalization rules, scoring code, or projection logic. Those changes require a reviewed PR.

## Review consequence

The earlier PR implementation included a River City-local filesystem ledger. That duplicated Clio responsibilities and attracted valid review findings around canonical serialization, exclusive writes, read validation, path safety, source correction selection, schema identity, and workflow hardening. The integration direction removes that duplicate layer rather than independently repairing it.
