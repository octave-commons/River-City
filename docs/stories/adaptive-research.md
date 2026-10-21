# RC-A: Evidence-backed questions from canonical charts

Canonical lifecycle: [River City #37](https://github.com/octave-commons/River-City/issues/37).

As a River City reader, I want recurring measurements to provoke useful,
testable investigations, so a report helps me decide what evidence to seek next.

## Reconciliation before creation

Inspected all 31 issue results and all five PR results on 2026-10-21. There were
no open River City PRs. PRs #24/#26 supplied the observation/projection/Clio
boundary; #28/#29/#30 supplied the independent model-watch lane and recovery
behavior. Those PRs are merged and are not reopened or duplicated.

| Existing owner | Relationship to this work |
| --- | --- |
| #27 | Source freshness and provisional-tail acquisition; source-age diagnostics advance its first requirement, while a new feed remains unimplemented |
| #18 | Daily facts/interpretations/report composition; chart-first output advances this work |
| #20 | Deterministic fixtures and replay; new suites extend it |
| #19 | Daily scheduling; verification gates the existing daily publisher |
| #23 / merged #24 | Shared Clio history architecture; extracted Clio adapter respects it |
| #3 / #9 / #10 | Reviewed baseline/compound/latent policy gates; unchanged |
| #31–#36 | Source-specific adapters; research leads may inform them, but this work does not implement or replace them |

## RC-A1 — Diagnose before composing

**Story / why:** As a reader, I need to distinguish a flat abnormal level from
normal stability, and measurement uncertainty from real disagreement, so weak
measurements do not masquerade as explanatory charts.

**Acceptance:** flat normal/flat abnormal remain distinct; noise does not absorb
monotonic trends; stale age uses observation dates per source; missing data is
not zero; uncertainty and unknown regime stay explicit; divergence requires
matching unit, observation key and overlapping dates.

**Verification:** `river-city.domain.research-test`, source-age and chart-gap
cases in `river-city.infra.research-test`.

## RC-A2 — Admit bounded investigation

**Story / why:** As the operator, I want deterministic deduplication, cooldown and
budgets so repetitive signals cannot create an unbounded research bill.

**Acceptance:** stable request identity; sorted admission; per-day/per-report
caps; null/failed/interrupted attempts consume budget; corrupted/orphan
reservations fail closed; invalid policy fails before effects.

**Verification:** domain admission cases; operational reservation, replay and
null/failure cases in the orchestration suite; worker hard-budget tests.

## RC-A3 — Execute and retain research

**Story / why:** As a researcher, I need inspectable source material,
countermodels and discriminating tests so an escalation is more than a queue.

**Acceptance:** execute bounded searches, inspect available text, follow
measurement vocabulary, retain exact raw responses/digests/query/time; keep
candidate evidence, null results and failures distinct; no automatic production
indicator promotion.

**Verification:** Node worker tests plus orchestration invoking the actual Node
module; tampered raw bytes are rejected. Live Crossref execution retained two
responses and four candidate references for each of the two current PortWatch
signals. These are metadata/abstract leads, not verified findings.

**Remaining scope:** full-text appraisal, broader independent evidence providers
and a richer research executor are not implemented by the initial adapter.

## RC-A4 — Lead with the informative visualization

**Story / why:** As a reader, I want the first chart to expose the strongest
supported observation or question so chart order serves the investigation.

**Acceptance:** deterministic ranking; degraded coverage leads when necessary;
visible establishes/does-not-establish/question; immutable dated chart/report
artifacts; prior research remains linked on replay; valid numeric SVG and gaps.

**Verification:** ranking and composition tests, SVG coordinate/gap regression,
plus rendered-image inspection on the live snapshot. Inspection caught a Clojure
ratio emitted as an SVG coordinate; the fix and regression test are retained.

## RC-A5 — Preserve host provenance and review boundaries

**Story / why:** As a Foresight operator, I want existing causal parents and
shared Clio admission so research does not grow a second event-sourcing kernel.

**Acceptance:** portable data-only payload schema; source event IDs survive;
real Clio admission and canonical replay; exact duplicate returns original
event; missing parent or changed admitted dossier fails; host-owned schema
history and event identity; no production-model edits.

**Verification:** `river-city.host.research-test` executes the actual extracted
Clio runtime. Repository output explicitly records pending host admission.

**Remaining scope:** a current Foresight integration PR must restore the declared
River City source and activate its collector. Current Foresight main lacks that
source. Passing an isolated host test is not deployment evidence.

## PR #38 review corrections

**Story / why:** As the operator, I need retained dossiers, causal inputs and
execution limits to remain valid on replay and under degraded conditions, so a
passing first run does not conceal later evidence corruption or lost coverage.

**Acceptance and verification:**

| Review finding | Acceptance / regression |
| --- | --- |
| Codex: counterpart parents | Shaped divergent sources → research request → actual Clio admission names both source events; omitting the counterpart ledger refuses admission |
| Codex: reservation accounting | Missing/noninteger day, missing signal/request identity or missing policy blocks history before spending |
| Codex: replay evidence links | Current suppressed dossier is linked even with more than six historical attempts |
| Codex: byte digests | A failed response containing non-UTF-8 bytes is admitted with its original byte digest |
| Codex: stopped canonical port | A port present only in `:latest` remains stale/insufficient and eligible for research |
| CodeRabbit: checkout credentials | Verification checkout disables credential persistence; publishing checkout retains its push configuration |
| CodeRabbit: retained evidence | Stored result identity/schema/production boundary and raw evidence digests are revalidated before replay/report use |
| CodeRabbit: worker deadline | A real stalled subprocess is terminated by an enforced deadline; ordinary success/nonzero exit handling still works |
| CodeRabbit: lock contention | A second process holding the budget lock yields the intended contention error rather than a nil-close NPE |

Prior verification receipts and live dossiers remain unchanged. The new review
receipt records the replacement source digests and available validation.
