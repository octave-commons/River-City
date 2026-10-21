# Adaptive research

River City evaluates canonical PortWatch signals **before** composing a daily
analytical report. `bb daily` retains ingest → normalize → project → render;
render now evaluates → admits → researches → retains evidence → selects a chart
→ composes. Model-watch remains an operational alert stream with its existing
materiality law; no API pricing or cost-floor definition changes here.

## What the diagnostic establishes

`river-city.domain.research` is portable, pure `.cljc`. It measures a configurable
recent window, source observation age, missingness, relative range, oscillating
movement, descriptive displacement, comparison uncertainty, available regime
context, and common-basis source disagreement. It never estimates a causal
coefficient or approves a normal regime. A monotonic large movement is not
classified as noise merely because its successive changes are large.

A flat displaced level is explicitly different from a flat level near its
comparison. Both can motivate research. Unit/observation-key mismatches and
nonoverlapping dates remain incomparable/insufficient overlap rather than
becoming manufactured divergences. Fresh download timestamps do not reset
measurement age. Missing daily dates break the plotted line.

The current adapter binds the two canonical vessel-passage series. The generic
signal contract accepts comparable counterpart observations when an existing
adapter can supply them; it does not fabricate a provisional feed. Adding other
canonical indicators requires an explicit source adapter and reviewed mapping.

## Admission and budgets

`resources/river_city/research.edn` is reviewed diagnostic configuration, separate
from analytical baselines, compounds and latents. Initial routing defaults:

| Bound | Default |
| --- | ---: |
| Diagnostic window / minimum points | 14 / 5 |
| Source age considered stale | more than 3 days |
| Cooldown per signal | 7 days |
| Attempts per UTC day / report | 2 / 2 |
| Queries per attempt | 2 |
| Per-query deadline | 15 seconds |
| Per-query retained response limit | 256 KiB |
| Candidate references per attempt | 4 |
| Paid inference or search cost | $0; none invoked |

Hard adapter ceilings reject policy values that exceed four queries, 30 seconds,
1 MiB per response or ten candidate references. The process deadline also bounds
worker execution. UTC, rather than local wall time, defines budget days.

Admission sorts by diagnostic informativeness then stable signal identity.
Dossier identity hashes normalized signal inputs and policy, excluding volatile
retrieval timestamps. Observation age is an evaluation input, so an unchanged
measurement can be reconsidered after its cooldown as its freshness degrades.
Nulls, failures and interrupted reservations consume budget. Identical inputs
are suppressed; a changed signal still respects cooldown and day budget.
An OS mutex serializes the operational budget calculation. An orphan reservation
or malformed history fails closed and requires reconciliation; it never frees
spend silently. Completed dossiers are never rewritten or deleted by this loop.

## Research execution and evidence

The default executor is a bounded literature investigation, not a vendor's
unlimited “deep research” product. It searches Crossref, inspects available
abstracts and titles, follows discovered measurement vocabulary in its second
query, and retains candidate alternative indicators and discriminating tests.
It does not assert experimental results from bibliographic metadata. Peer review
is **not verified** by a `journal-article` type. Full-text appraisal and broader
news/data-source discovery remain extensions of this executor, not claimed
capabilities.

Each dossier under `data/research/research-<digest>/` retains:

- input observations, diagnostic flags, exact policy, questions and causal refs;
- exact search URLs/queries, retrieval timestamps, raw response bytes and SHA-256;
- inspected-text scope, candidate DOI references, proposed indicators/tests;
- successful, null, failed or interrupted outcome and explicit limitations;
- a data-only event draft for the Foresight host.

Provider responses are untrusted. Request identity, result shape and retained
byte digests are validated before report use. HTTP/parse/size/timeout failures
remain failures. A successful empty search is a null result; it does not prove
absence. A search result is a lead, not an approved observation or production
indicator. The pure comparison-countermodels/tests remain available on nulls.

## Report composition

Reports begin with the highest-ranked supported signal visualization. When the
available signals are stale/invalid/insufficient, the lead shows coverage or
source age and asks a concrete evidence-recovery question. A rendered SVG is
embedded directly, with numeric axes, dates, units and descriptive-band scope.
Each lead states what it establishes, what it does not, and a falsifiable
question. Flags and source age are visible; no significance probability is
invented. Existing detailed Vega-Lite specs remain available.

Every report run gets a dated content identity and immutable SVG; old report
images cannot drift when `lead.svg` changes. Latest manifest/projection/report
paths remain disposable compatibility views. Research suppressed on replay stays
linked to its retained dossier. No normal-regime exclusion, scoring weight,
compound, latent definition or model input is promoted automatically.

## Foresight and Clio boundary

Dossiers are evidence bundles, **not** a River City event ledger. Source record
references remain visible, and existing Clio event IDs are preserved through
snapshot/diagnostic shaping. No event UUID or schema hash is invented here.

`river-city.infra.research-host` is a JVM adapter using the extracted
`open-hax/clio` API pinned in the `:host` / `:host-test` aliases. The Foresight
host supplies schema-directory, ledger-path, complete parent-ledgers,
dossier-path and its real actor. Example from a host-configured working directory:

```sh
clojure -M:host -m river-city.infra.research-host host-options.edn
clojure -M:host-test
```

The adapter uses Clio's schema catalog, UUID/time allocation, append admission,
durability, canonical history and explicit recovery token contract. It refuses
missing causal parents and changed admitted dossiers; exact replay returns the
original event. Keep `:clio/append-recovery` from a failed append for explicit
Clio retry instead of generating another identity.

Current Foresight `main@cffc34448aa6807fc51135d7f90f0f9fe6ba7efe` does not declare a
River City submodule or an active research collector. Therefore this PR's daily
repository snapshots remain explicitly **not ledger-backed**, with host admission
pending. The host-adapter integration test proves real Clio admission/replay in
an isolated host; it does not prove a deployed Foresight collector. Issue #37
remains open for that activation and broader research coverage.

## Verification

`bb test` runs JVM domain laws, actual Clio host integration, Node adapter tests,
and Babashka orchestration/report tests. Network fixtures are deterministic;
PR checks make no live research requests and carry read-only credentials.
The non-PR daily publisher runs only after verification.

See [the stories](stories/adaptive-research.md) and the session verification
receipt in `.ημ/sessions/` for observed results and environment limitations.
