// SPDX-License-Identifier: GPL-3.0-or-later

import assert from "node:assert/strict";
import test from "node:test";
import {
  detectMaterialChanges,
  estimateWeightMemory,
  extractCommercialFacts,
  normalizeOpenRouterModel,
  renderAlert,
  toEdn,
  visibleText
} from "../scripts/model_watch.mjs";

const config = {
  schema_version: 1,
  mention: "@operator",
  thresholds: {
    price_fraction: 0.10,
    cost_frontier_fraction: 0.20,
    context_multiplier: 1.50,
    benchmark_points: 5,
    minimum_context_tokens: 262144,
    minimum_open_weight_parameters: 1_000_000_000
  },
  workloads: [
    {id: "balanced", label: "balanced", input: 0.8, cached_input: 0, output: 0.2},
    {id: "agentic-cache", label: "agentic", input: 0.5, cached_input: 0.3, output: 0.2}
  ],
  providers: [
    {id: "openai", label: "GPT / OpenAI"}
  ]
};

function model(overrides = {}) {
  return {
    provider: "openai",
    id: "openai/gpt-a",
    canonical_slug: "openai/gpt-a",
    name: "GPT A",
    created: 1,
    description: "agentic coding model",
    context_length: 128000,
    max_output_tokens: 32000,
    input_modalities: ["text"],
    output_modalities: ["text"],
    supported_parameters: ["tools"],
    pricing: {input: 1, cached_input: 0.1, cache_write: null, output: 5},
    hugging_face_id: null,
    expiration_date: null,
    benchmark_score: 50,
    variant: false,
    ...overrides
  };
}

function providerSnapshot({models = [model()], facts = ["gpt a input $1 output $5 per 1m tokens"], weights = []} = {}) {
  return {
    label: "GPT / OpenAI",
    models,
    open_weight_models: weights,
    official_sources: [{
      id: "official",
      url: "https://example.test/pricing",
      ok: true,
      facts,
      facts_digest: JSON.stringify(facts)
    }]
  };
}

function snapshot(provider) {
  return {
    schema_version: 1,
    observed_at: "2026-09-01T12:00:00.000Z",
    providers: {openai: provider}
  };
}

test("normalizes OpenRouter per-token prices to per-million prices", () => {
  const normalized = normalizeOpenRouterModel({
    id: "openai/gpt-test",
    name: "GPT Test",
    context_length: 1000000,
    architecture: {input_modalities: ["text", "image"], output_modalities: ["text"]},
    pricing: {prompt: "0.000002", completion: "0.000012", input_cache_read: "0.0000002"},
    top_provider: {max_completion_tokens: 128000}
  }, "openai");
  assert.deepEqual(normalized.pricing, {input: 2, cached_input: 0.2, cache_write: null, output: 12});
  assert.equal(normalized.context_length, 1000000);
});

test("ignores sub-threshold price changes and alerts at the threshold", () => {
  const previous = snapshot(providerSnapshot());
  const below = snapshot(providerSnapshot({models: [model({pricing: {input: 1.09, cached_input: 0.1, cache_write: null, output: 5}})]}));
  assert.equal(detectMaterialChanges(previous, below, config).length, 0);

  const atThreshold = snapshot(providerSnapshot({models: [model({pricing: {input: 1.10, cached_input: 0.1, cache_write: null, output: 5}})]}));
  const changes = detectMaterialChanges(previous, atThreshold, config);
  assert.equal(changes.length, 1);
  assert.equal(changes[0].kind, "api-price");
  assert.equal(changes[0].field, "input");

  const decrease = snapshot(providerSnapshot({models: [model({pricing: {input: 0.90, cached_input: 0.1, cache_write: null, output: 5}})]}));
  assert.equal(detectMaterialChanges(previous, decrease, config).length, 1);
});

test("ignores OpenRouter batch/free variants as model launches", () => {
  const previous = snapshot(providerSnapshot());
  const current = snapshot(providerSnapshot({models: [model(), model({id: "openai/gpt-a:batch", name: "GPT A batch", variant: true})]}));
  assert.equal(detectMaterialChanges(previous, current, config).length, 0);
});

test("alerts on a material context frontier launch", () => {
  const previous = snapshot(providerSnapshot());
  const frontier = model({
    id: "openai/gpt-b",
    name: "GPT B",
    context_length: 262144,
    pricing: {input: 2, cached_input: 0.2, cache_write: null, output: 8},
    benchmark_score: 52
  });
  const current = snapshot(providerSnapshot({models: [model(), frontier]}));
  const changes = detectMaterialChanges(previous, current, config);
  assert.equal(changes.length, 1);
  assert.equal(changes[0].kind, "model-launch");
  assert.match(changes[0].reasons.join(" "), /context frontier/);
});


test("does not treat a small-context increase as the configured context frontier", () => {
  const previous = snapshot(providerSnapshot({models: [model({context_length: 4096})]}));
  const current = snapshot(providerSnapshot({models: [model({context_length: 8192})]}));
  assert.equal(detectMaterialChanges(previous, current, config).length, 0);
});

test("page layout churn is not alertable without commercial fact changes", () => {
  const htmlA = "<html><style>x</style><body><h1>Pricing</h1><p>GPT A input $1 / 1M tokens</p></body></html>";
  const htmlB = "<html><body><div><h1>Pricing</h1></div><section>GPT A input $1 / 1M tokens</section></body></html>";
  assert.deepEqual(extractCommercialFacts(htmlA, "text/html"), extractCommercialFacts(htmlB, "text/html"));
  assert.match(visibleText(htmlA, "text/html"), /GPT A input/);
});

test("alerts when an official package quota changes", () => {
  const previousFacts = ["mimo token plan $50 month 100m credits model"];
  const currentFacts = ["mimo token plan $50 month 500m credits model"];
  const previous = snapshot(providerSnapshot({facts: previousFacts}));
  const current = snapshot(providerSnapshot({facts: currentFacts}));
  const changes = detectMaterialChanges(previous, current, config);
  assert.equal(changes.length, 1);
  assert.equal(changes[0].kind, "official-commercial-facts");
  assert.deepEqual(changes[0].added, currentFacts);
  assert.deepEqual(changes[0].removed, previousFacts);
});


test("official fact alerts expose only the high-signal delta", () => {
  const previous = snapshot(providerSnapshot({facts: ["navigation item model", "mimo plan $50 month 100m credits model"]}));
  const current = snapshot(providerSnapshot({facts: ["navigation item changed model", "mimo plan $50 month 500m credits model"]}));
  const [change] = detectMaterialChanges(previous, current, config);
  assert.equal(change.kind, "official-commercial-facts");
  assert.deepEqual(change.added, ["mimo plan $50 month 500m credits model"]);
  assert.deepEqual(change.removed, ["mimo plan $50 month 100m credits model"]);
});

test("alerts on a new official open-weight model and computes memory floors", () => {
  const previous = snapshot(providerSnapshot({weights: []}));
  const weights = [{
    provider: "openai",
    id: "openai/gpt-oss-20b",
    created_at: "2026-09-01T00:00:00Z",
    last_modified: "2026-09-01T00:00:00Z",
    pipeline_tag: "text-generation",
    library_name: "transformers",
    tags: ["transformers", "text-generation"],
    parameters: 20_000_000_000
  }];
  const current = snapshot(providerSnapshot({weights}));
  const changes = detectMaterialChanges(previous, current, config);
  assert.equal(changes.length, 1);
  assert.equal(changes[0].kind, "open-weight-launch");
  assert.deepEqual(changes[0].memory, estimateWeightMemory(20_000_000_000));
  assert.equal(changes[0].memory.int4_gib, 9.3);
});


test("EDN snapshots use stable keyword keys", () => {
  const edn = toEdn({schema_version: 1, provider: {model_id: "x"}});
  assert.equal(edn, '{:provider {:model-id "x"} :schema-version 1}');
});

test("first observation seeds a baseline without an alert", () => {
  assert.deepEqual(detectMaterialChanges(null, snapshot(providerSnapshot()), config), []);
});


test("alert digest is stable across observation timestamps for retry deduplication", () => {
  const previous = snapshot(providerSnapshot());
  const current = snapshot(providerSnapshot({models: [model({pricing: {input: 1.2, cached_input: 0.1, cache_write: null, output: 5}})]}));
  const changes = detectMaterialChanges(previous, current, config);
  const first = renderAlert(changes, current, config);
  const second = renderAlert(changes, {...current, observed_at: "2026-09-02T12:00:00.000Z"}, config);
  assert.equal(first.digest, second.digest);
});

test("alert rendering contains the required cost/update/evidence structure", () => {
  const previous = snapshot(providerSnapshot());
  const current = snapshot(providerSnapshot({models: [model({pricing: {input: 1.2, cached_input: 0.1, cache_write: null, output: 5}})]}));
  const changes = detectMaterialChanges(previous, current, config);
  const alert = renderAlert(changes, current, config);
  assert.match(alert.markdown, /What changed/);
  assert.match(alert.markdown, /Likely cost impact/);
  assert.match(alert.markdown, /Update/);
  assert.match(alert.markdown, /OpenRouter is a cross-check, not canonical price authority/);
  assert.match(alert.markdown, /Evidence to verify/);
});
