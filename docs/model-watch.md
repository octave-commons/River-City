# Model economics watch

River City observes API pricing, packaging, model lifecycle, capability-frontier changes, and official open-weight releases for:

- GPT / OpenAI
- Claude / Anthropic
- Kimi / Moonshot AI
- GLM / Z.ai
- Mistral
- MiMo / Xiaomi

The scheduled workflow runs daily at `13:47 UTC`. Its first successful run establishes a baseline and emits no alert. Later runs append one grouped comment to issue #13 only when at least one materiality rule is satisfied. Alert delivery happens before the new snapshot is committed, and a stable alert digest suppresses duplicates while allowing failed deliveries to retry.

## Source authority

1. Official provider pricing, model, release-note, and lifecycle pages are the canonical evidence surface.
2. OpenRouter is a machine-readable cross-check for catalog, price, context, modality, and expiration deltas. Its prices are never promoted directly into River City's canonical provider-price dataset without official verification.
3. Official Hugging Face organizations are the evidence surface for new open-weight artifacts. A repository appearance is not a self-host cost measurement.

Raw HTML is not retained. The snapshot stores normalized high-signal facts, hashes, model catalog rows, source coverage, and official open-weight metadata.

## Materiality gate

A run is alertable when one or more of these laws hold:

- a tracked input, cached-input, cache-write, or output price moves by at least 10%;
- a new model lowers a configured mixed-workload API frontier by at least 20%;
- a new model expands the provider context frontier by at least 1.5× and reaches at least 262,144 tokens;
- a new modality, cross-check benchmark frontier, explicit expiration, or deprecation appears;
- an official source changes a high-signal price, batch/cache rule, regional/priority multiplier, minimum spend, rate limit, credit quota, subscription plan, promotion, or migration fact;
- a primary text or multimodal model appears in an official Hugging Face organization.

A page hash, formatting edit, reordered table, OpenRouter `:free`/`:batch` variant, or sub-threshold price move is not alertable by itself.

## Alert contract

Every alert states:

1. **What changed** — model, price field, package/lifecycle fact, or open-weight artifact.
2. **Likely cost impact** — change under River City's balanced and cached-agent workload mixes, or a weight-only memory floor where parameter count is available.
3. **What to update** — the exact canonical price, workload, alias, lifecycle, provenance, or self-host calibration data that is now stale.
4. **Evidence** — official provider URL when the change came from official facts; OpenRouter is labeled as a cross-check.

Closed-provider internal inference cost remains unknown. Alerts must not turn public API prices into claims about provider cost, and open-weight memory estimates must include KV-cache, runtime, utilization, power, amortization, and quality calibration before they move the self-host cost floor.

## Generated state

- `data/normalized/model-watch/latest.json`
- `data/normalized/model-watch/latest.edn`
- `reports/model-watch/latest.md`
- `reports/model-watch/alerts/<date>-<digest>.md` for material runs

Run locally:

```bash
node --test test/model_watch.test.mjs
node scripts/model_watch.mjs
```
