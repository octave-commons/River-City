#!/usr/bin/env node
// SPDX-License-Identifier: GPL-3.0-or-later

import crypto from "node:crypto";
import fs from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { pathToFileURL } from "node:url";

const DEFAULT_CONFIG = "resources/river_city/model-watch.json";
const DEFAULT_SNAPSHOT = "data/normalized/model-watch/latest.json";
const DEFAULT_REPORT = "reports/model-watch/latest.md";
const DEFAULT_RESULT = ".tmp/model-watch-result.json";
const FETCH_TIMEOUT_MS = 25_000;
const USER_AGENT = "River-City-model-watch/1.0 (+https://github.com/octave-commons/River-City)";

const PRICE_FIELDS = ["input", "cached_input", "cache_write", "output"];
const MATERIAL_KEYWORDS = [
  "price", "pricing", "cost", "input", "output", "cache", "batch", "discount",
  "subscription", "plan", "credit", "quota", "minimum", "recharge", "regional",
  "priority", "promotion", "free", "context", "window", "modality", "multimodal",
  "release", "launch", "available", "availability", "deprecated", "deprecation",
  "retired", "retirement", "sunset", "migration", "open weight", "open-weight",
  "open source", "license", "parameter", "tokens", "mtok", "rate limit", "rpm", "tpm"
];
const PACKAGING_KEYWORDS = [
  "subscription", "plan", "credit", "quota", "minimum", "recharge", "regional",
  "priority", "promotion", "discount", "batch", "cache", "free", "rate limit", "rpm", "tpm"
];
const DEPRECATION_KEYWORDS = ["deprecated", "deprecation", "retired", "retirement", "sunset", "migration"];
const LAUNCH_KEYWORDS = ["release", "released", "launch", "launched", "available", "availability", "open weight", "open-weight", "open source"];
const PRIMARY_MODEL_EXCLUSIONS = [
  "gguf", "awq", "gptq", "mlx", "lora", "adapter", "quantized", "quantization",
  "tokenizer", "benchmark", "evaluation", "reward", "critic", "reranker", "embedding",
  "guard", "moderation", "ocr", "tts", "asr", "demo"
];

function sha256(value) {
  return crypto.createHash("sha256").update(value).digest("hex");
}

function round(value, digits = 6) {
  if (!Number.isFinite(value)) return null;
  const scale = 10 ** digits;
  return Math.round(value * scale) / scale;
}

function numberOrNull(value) {
  if (value === null || value === undefined || value === "") return null;
  const number = Number(value);
  return Number.isFinite(number) ? number : null;
}

function perMillion(value) {
  const parsed = numberOrNull(value);
  return parsed === null ? null : round(parsed * 1_000_000, 6);
}

function sortedUnique(values) {
  return [...new Set(values.filter((value) => value !== null && value !== undefined))].sort();
}

function decodeHtmlEntities(value) {
  return value
    .replaceAll("&nbsp;", " ")
    .replaceAll("&amp;", "&")
    .replaceAll("&lt;", "<")
    .replaceAll("&gt;", ">")
    .replaceAll("&quot;", '"')
    .replaceAll("&#39;", "'")
    .replace(/&#(\d+);/g, (_, code) => String.fromCodePoint(Number(code)));
}

export function visibleText(raw, contentType = "") {
  if (!raw) return "";
  if (!contentType.includes("html") && !/<(?:html|body|table|div|p|script)\b/i.test(raw)) {
    return raw.replace(/\r\n?/g, "\n");
  }
  return decodeHtmlEntities(raw
    .replace(/<script\b[^>]*>[\s\S]*?<\/script>/gi, "\n")
    .replace(/<style\b[^>]*>[\s\S]*?<\/style>/gi, "\n")
    .replace(/<noscript\b[^>]*>[\s\S]*?<\/noscript>/gi, "\n")
    .replace(/<\/(?:p|div|li|tr|td|th|h[1-6]|section|article|br|table|thead|tbody)>/gi, "\n")
    .replace(/<(?:br|hr)\s*\/?>/gi, "\n")
    .replace(/<[^>]+>/g, " ")
    .replace(/\r\n?/g, "\n"));
}

export function extractCommercialFacts(raw, contentType = "") {
  const lines = visibleText(raw, contentType)
    .split("\n")
    .map((line) => line.replace(/\s+/g, " ").trim())
    .filter((line) => line.length >= 4 && line.length <= 700);

  return sortedUnique(lines.filter((line) => {
    const lower = line.toLowerCase();
    const hasKeyword = MATERIAL_KEYWORDS.some((keyword) => lower.includes(keyword));
    const hasMeasure = /(?:\$|usd|€|eur|¥|rmb|%|\b\d+(?:[.,]\d+)?\s*(?:m|b|k)?\s*(?:tokens?|tok|mtok|credits?|months?|hours?|minutes?|rpm|tpm|tpd|parameters?|context|x)\b)/i.test(line);
    const hasTerminalStatus = DEPRECATION_KEYWORDS.some((keyword) => lower.includes(keyword));
    const hasModelShape = /(?:gpt|claude|kimi|moonshot|glm|mistral|ministral|codestral|mimo|model)/i.test(line);
    return hasKeyword && (hasMeasure || hasTerminalStatus) && (hasModelShape || PACKAGING_KEYWORDS.some((keyword) => lower.includes(keyword)));
  }).map((line) => line.toLowerCase())).slice(0, 500);
}

function normalizeModelId(id) {
  return String(id ?? "").trim();
}

function isVariantModelId(id) {
  return id.includes(":") || /(?:^|[-_.])(free|batch|exacto|extended)(?:$|[-_.])/i.test(id);
}

function maxBenchmark(model) {
  const aa = model?.benchmarks?.artificial_analysis ?? {};
  return Math.max(
    numberOrNull(aa.intelligence_index) ?? -Infinity,
    numberOrNull(aa.coding_index) ?? -Infinity,
    numberOrNull(aa.agentic_index) ?? -Infinity
  );
}

export function normalizeOpenRouterModel(model, providerId) {
  const inputModalities = model?.architecture?.input_modalities ?? [];
  const outputModalities = model?.architecture?.output_modalities ?? [];
  return {
    provider: providerId,
    id: normalizeModelId(model.id),
    canonical_slug: model.canonical_slug ?? null,
    name: model.name ?? model.id ?? "unknown",
    created: numberOrNull(model.created),
    description: String(model.description ?? "").replace(/\s+/g, " ").trim().slice(0, 600),
    context_length: numberOrNull(model.context_length ?? model?.top_provider?.context_length),
    max_output_tokens: numberOrNull(model?.top_provider?.max_completion_tokens),
    input_modalities: sortedUnique(inputModalities),
    output_modalities: sortedUnique(outputModalities),
    supported_parameters: sortedUnique(model.supported_parameters ?? []),
    pricing: {
      input: perMillion(model?.pricing?.prompt),
      cached_input: perMillion(model?.pricing?.input_cache_read),
      cache_write: perMillion(model?.pricing?.input_cache_write),
      output: perMillion(model?.pricing?.completion)
    },
    hugging_face_id: model.hugging_face_id || null,
    expiration_date: model.expiration_date || null,
    benchmark_score: Number.isFinite(maxBenchmark(model)) ? round(maxBenchmark(model), 3) : null,
    variant: isVariantModelId(normalizeModelId(model.id))
  };
}

function normalizeHuggingFaceModel(model, providerId) {
  const tags = sortedUnique(model.tags ?? []);
  const safetensorsTotal = numberOrNull(model?.safetensors?.total);
  return {
    provider: providerId,
    id: String(model.id ?? model.modelId ?? ""),
    created_at: model.createdAt ?? null,
    last_modified: model.lastModified ?? null,
    pipeline_tag: model.pipeline_tag ?? null,
    library_name: model.library_name ?? null,
    tags,
    parameters: safetensorsTotal
  };
}

function primaryOpenWeightCandidate(model) {
  const lower = model.id.toLowerCase();
  if (!model.id || PRIMARY_MODEL_EXCLUSIONS.some((token) => lower.includes(token))) return false;
  const relevantPipeline = ["text-generation", "image-text-to-text", "text2text-generation"].includes(model.pipeline_tag);
  const relevantTag = model.tags.some((tag) => ["text-generation", "image-text-to-text", "transformers"].includes(tag));
  return relevantPipeline || relevantTag;
}

function modelParametersFromName(id) {
  const matches = [...id.matchAll(/(?:^|[-_/])(\d+(?:\.\d+)?)\s*b(?:$|[-_/])/ig)];
  if (matches.length === 0) return null;
  return Math.max(...matches.map((match) => Number(match[1]) * 1_000_000_000));
}

function effectiveParameters(model) {
  return model.parameters ?? modelParametersFromName(model.id);
}

export function estimateWeightMemory(parameters) {
  if (!Number.isFinite(parameters) || parameters <= 0) return null;
  const gib = 1024 ** 3;
  return {
    bf16_gib: round(parameters * 2 / gib, 1),
    int8_gib: round(parameters / gib, 1),
    int4_gib: round(parameters * 0.5 / gib, 1)
  };
}

function workloadCost(model, workload) {
  const pricing = model.pricing ?? {};
  const input = pricing.input;
  const output = pricing.output;
  if (input === null || input === undefined || output === null || output === undefined) return null;
  const cached = pricing.cached_input ?? input;
  return round(workload.input * input + workload.cached_input * cached + workload.output * output, 6);
}

function percentChange(oldValue, newValue) {
  if (oldValue === 0) return newValue === 0 ? 0 : Infinity;
  return (newValue - oldValue) / Math.abs(oldValue);
}

function formatMoney(value) {
  if (!Number.isFinite(value)) return "unknown";
  if (value >= 10) return `$${value.toFixed(2)}`;
  if (value >= 1) return `$${value.toFixed(3).replace(/0+$/, "").replace(/\.$/, "")}`;
  return `$${value.toFixed(4).replace(/0+$/, "").replace(/\.$/, "")}`;
}

function formatPercent(value) {
  if (!Number.isFinite(value)) return value > 0 ? "+∞" : "−∞";
  return `${value >= 0 ? "+" : "−"}${Math.abs(value * 100).toFixed(1)}%`;
}

function mapById(items = []) {
  return new Map(items.map((item) => [item.id, item]));
}

function setDifference(left, right) {
  const rightSet = new Set(right);
  return left.filter((item) => !rightSet.has(item));
}

function sourceFactsChanged(previousProvider, currentProvider) {
  const previousSources = new Map((previousProvider?.official_sources ?? []).map((source) => [source.id, source]));
  return (currentProvider?.official_sources ?? []).some((source) => {
    const previous = previousSources.get(source.id);
    return previous && source.ok !== false && previous.facts_digest !== source.facts_digest;
  });
}

function officialFactChanges(providerId, previousProvider, currentProvider) {
  const changes = [];
  const previousSources = new Map((previousProvider?.official_sources ?? []).map((source) => [source.id, source]));
  for (const source of currentProvider?.official_sources ?? []) {
    const previous = previousSources.get(source.id);
    if (source.ok === false) continue;
    const isNewSource = !previous && previousSources.size > 0;
    if (!isNewSource && (!previous || previous.facts_digest === source.facts_digest)) continue;
    const added = isNewSource ? (source.facts ?? []) : setDifference(source.facts ?? [], previous.facts ?? []);
    const removed = isNewSource ? [] : setDifference(previous.facts ?? [], source.facts ?? []);
    const isHighSignal = (fact) => {
      const lower = fact.toLowerCase();
      const hasPrice = /(?:\$|usd|€|eur|¥|rmb)/i.test(fact) && /\d/.test(fact);
      const hasPackaging = PACKAGING_KEYWORDS.some((keyword) => lower.includes(keyword)) && /\d|free/.test(lower);
      const hasDeprecation = DEPRECATION_KEYWORDS.some((keyword) => lower.includes(keyword));
      const hasLaunch = LAUNCH_KEYWORDS.some((keyword) => lower.includes(keyword)) && /(?:context|token|parameter|multimodal|model|gpt|claude|kimi|glm|mistral|mimo)/i.test(fact);
      return hasPrice || hasPackaging || hasDeprecation || hasLaunch;
    };
    const highSignalAdded = added.filter(isHighSignal);
    const highSignalRemoved = removed.filter(isHighSignal);
    if (highSignalAdded.length === 0 && highSignalRemoved.length === 0) continue;
    changes.push({
      kind: "official-commercial-facts",
      provider: providerId,
      material: true,
      title: `Official pricing or packaging facts changed at ${source.id}`,
      source: {id: source.id, url: source.url},
      added: highSignalAdded.slice(0, 8),
      removed: highSignalRemoved.slice(0, 8),
      impact: "The canonical API-cost row or its packaging assumptions may be stale. The exact unit impact must be verified from the linked official table before replacing River City’s canonical price.",
      update: "Update the provider price/plan record, separating list, cache, batch, promotional, regional, and subscription economics; then regenerate API-vs-self-host ratios."
    });
  }
  return changes;
}

function newModelMateriality(model, previousModels, config, officialChanged) {
  if (model.variant) return [];
  const reasons = [];
  const previousPrimary = previousModels.filter((candidate) => !candidate.variant);
  const previousMaxContext = Math.max(0, ...previousPrimary.map((candidate) => candidate.context_length ?? 0));
  const previousModalities = new Set(previousPrimary.flatMap((candidate) => [...candidate.input_modalities, ...candidate.output_modalities]));
  const newModalities = [...model.input_modalities, ...model.output_modalities].filter((modality) => !previousModalities.has(modality));
  if (newModalities.length > 0) reasons.push(`new modality: ${newModalities.join(", ")}`);
  if ((model.context_length ?? 0) >= config.thresholds.minimum_context_tokens && (previousMaxContext === 0 || model.context_length >= previousMaxContext * config.thresholds.context_multiplier)) {
    reasons.push(`context frontier: ${model.context_length.toLocaleString()} tokens`);
  }
  const previousBestBenchmark = Math.max(-Infinity, ...previousPrimary.map((candidate) => candidate.benchmark_score ?? -Infinity));
  if (model.benchmark_score !== null && model.benchmark_score >= previousBestBenchmark + config.thresholds.benchmark_points) {
    reasons.push(`cross-check benchmark frontier: ${model.benchmark_score}`);
  }
  for (const workload of config.workloads) {
    const newCost = workloadCost(model, workload);
    const previousCosts = previousPrimary.map((candidate) => workloadCost(candidate, workload)).filter(Number.isFinite);
    if (newCost !== null && previousCosts.length > 0) {
      const previousMin = Math.min(...previousCosts);
      if (newCost <= previousMin * (1 - config.thresholds.cost_frontier_fraction)) {
        reasons.push(`${workload.id} API cost frontier: ${formatMoney(newCost)}/M mixed tokens`);
      }
    }
  }
  if (model.hugging_face_id) reasons.push(`open-weight cross-reference: ${model.hugging_face_id}`);
  const highSignalDescription = /(?:flagship|frontier|agent|coding|reasoning|multimodal|open[- ]weight|open source)/i.test(`${model.name} ${model.description}`);
  if (reasons.length === 0 && officialChanged && highSignalDescription) reasons.push("official catalog changed with a high-signal model launch");
  return sortedUnique(reasons);
}

function structuredModelChanges(providerId, previousProvider, currentProvider, config) {
  const changes = [];
  const previousModels = previousProvider?.models ?? [];
  const currentModels = currentProvider?.models ?? [];
  const previousById = mapById(previousModels);
  const currentById = mapById(currentModels);
  const officialChanged = sourceFactsChanged(previousProvider, currentProvider);
  const evidenceUrls = sortedUnique((currentProvider?.official_sources ?? [])
    .filter((source) => source.ok)
    .map((source) => source.url))
    .slice(0, 4);

  for (const model of currentModels) {
    const previous = previousById.get(model.id);
    if (!previous) {
      const reasons = newModelMateriality(model, previousModels, config, officialChanged);
      if (reasons.length > 0) {
        changes.push({
          kind: "model-launch",
          provider: providerId,
          material: true,
          evidence_urls: evidenceUrls,
          title: `${model.name} entered the tracked frontier`,
          model_id: model.id,
          reasons,
          new: model,
          impact: model.hugging_face_id
            ? "This can move both sides of the comparison: add its API row and create a self-host calibration candidate for the linked weights."
            : "This can move the API frontier for at least one River City workload or capability tier.",
          update: model.hugging_face_id
            ? "Add the model alias and API price row; record weight/license provenance and benchmark representative self-host hardware before comparing costs."
            : "Add the model alias, context/modality tier, and verified direct-provider pricing before regenerating comparison charts."
        });
      }
      continue;
    }

    for (const field of PRICE_FIELDS) {
      const oldPrice = previous.pricing?.[field];
      const newPrice = model.pricing?.[field];
      if (!Number.isFinite(oldPrice) || !Number.isFinite(newPrice) || oldPrice === newPrice) continue;
      const fraction = percentChange(oldPrice, newPrice);
      if (Math.abs(fraction) + 1e-12 < config.thresholds.price_fraction) continue;
      const workloadImpacts = config.workloads.map((workload) => {
        const oldCost = workloadCost(previous, workload);
        const newCost = workloadCost(model, workload);
        return Number.isFinite(oldCost) && Number.isFinite(newCost)
          ? {id: workload.id, label: workload.label, old: oldCost, new: newCost, fraction: percentChange(oldCost, newCost)}
          : null;
      }).filter(Boolean);
      changes.push({
        kind: "api-price",
        provider: providerId,
        material: true,
        evidence_urls: evidenceUrls,
        title: `${model.name} ${field.replaceAll("_", " ")} price changed ${formatPercent(fraction)}`,
        model_id: model.id,
        field,
        old: oldPrice,
        new: newPrice,
        fraction,
        workload_impacts: workloadImpacts,
        impact: workloadImpacts.length
          ? workloadImpacts.map((item) => `${item.label}: ${formatMoney(item.old)} → ${formatMoney(item.new)} per 1M mixed tokens (${formatPercent(item.fraction)})`).join("; ")
          : `Cross-check price changed from ${formatMoney(oldPrice)} to ${formatMoney(newPrice)} per 1M tokens.`,
        update: "Verify the direct-provider price and effective date, then update the list/cache/batch row and regenerate API-vs-self-host ratios."
      });
    }

    const oldModalities = new Set([...previous.input_modalities, ...previous.output_modalities]);
    const addedModalities = [...model.input_modalities, ...model.output_modalities].filter((modality) => !oldModalities.has(modality));
    if (addedModalities.length > 0) {
      changes.push({
        kind: "capability",
        provider: providerId,
        material: true,
        evidence_urls: evidenceUrls,
        title: `${model.name} added ${addedModalities.join(", ")} capability`,
        model_id: model.id,
        impact: "Workloads previously requiring a separate specialist API may now consolidate onto one model, changing both token mix and tool-call overhead.",
        update: "Add a multimodal workload lane and compare the consolidated API cost with the current multi-model and self-host paths."
      });
    }

    const oldContext = previous.context_length ?? 0;
    const newContext = model.context_length ?? 0;
    if (oldContext > 0 && newContext >= config.thresholds.minimum_context_tokens && newContext >= oldContext * config.thresholds.context_multiplier) {
      changes.push({
        kind: "capability",
        provider: providerId,
        material: true,
        evidence_urls: evidenceUrls,
        title: `${model.name} context increased ${oldContext.toLocaleString()} → ${newContext.toLocaleString()}`,
        model_id: model.id,
        impact: "Long-context workloads can reduce chunking, retrieval, and orchestration overhead, but may trigger long-context price multipliers.",
        update: "Update the context tier and rerun long-context scenarios with any threshold multiplier represented explicitly."
      });
    }

    if (previous.expiration_date !== model.expiration_date && model.expiration_date) {
      changes.push({
        kind: "deprecation",
        provider: providerId,
        material: true,
        evidence_urls: evidenceUrls,
        title: `${model.name} now carries expiration ${model.expiration_date}`,
        model_id: model.id,
        impact: "The current comparison row has a migration deadline and may cease to be executable.",
        update: "Mark the row deprecated, add the replacement alias, and keep historical prices separate from the current frontier."
      });
    }
  }

  for (const previous of previousModels) {
    if (!currentById.has(previous.id) && previous.expiration_date) {
      changes.push({
        kind: "deprecation",
        provider: providerId,
        material: true,
        evidence_urls: evidenceUrls,
        title: `${previous.name} disappeared after carrying expiration ${previous.expiration_date}`,
        model_id: previous.id,
        impact: "The route is no longer available in the machine-readable cross-check.",
        update: "Verify direct-provider availability, retire the active row if confirmed, and preserve it only as historical evidence."
      });
    }
  }
  return changes;
}

function openWeightChanges(providerId, previousProvider, currentProvider, config) {
  const previousIds = new Set((previousProvider?.open_weight_models ?? []).map((model) => model.id));
  const changes = [];
  for (const model of currentProvider?.open_weight_models ?? []) {
    if (previousIds.has(model.id) || !primaryOpenWeightCandidate(model)) continue;
    const parameters = effectiveParameters(model);
    if (parameters !== null && parameters < config.thresholds.minimum_open_weight_parameters) continue;
    const memory = estimateWeightMemory(parameters);
    changes.push({
      kind: "open-weight-launch",
      provider: providerId,
      material: true,
      evidence_urls: [`https://huggingface.co/${model.id}`],
      title: `${model.id} appeared in the official Hugging Face organization`,
      model_id: model.id,
      parameters,
      memory,
      impact: memory
        ? `Weight-only memory floor is about ${memory.bf16_gib} GiB BF16, ${memory.int8_gib} GiB int8, or ${memory.int4_gib} GiB int4 before KV cache and runtime overhead.`
        : "A new official open-weight artifact may change the self-host candidate frontier; hardware cost cannot be inferred until parameter and runtime metadata are verified.",
      update: "Record the immutable revision and license, then benchmark throughput, power, memory, quantization, and quality on representative hardware before moving the self-host cost floor."
    });
  }
  return changes;
}

export function detectMaterialChanges(previous, current, config) {
  if (!previous?.providers) return [];
  const changes = [];
  for (const provider of config.providers) {
    const previousProvider = previous.providers[provider.id] ?? {};
    const currentProvider = current.providers[provider.id] ?? {};
    changes.push(...structuredModelChanges(provider.id, previousProvider, currentProvider, config));
    changes.push(...openWeightChanges(provider.id, previousProvider, currentProvider, config));
    changes.push(...officialFactChanges(provider.id, previousProvider, currentProvider));
  }
  const seen = new Set();
  return changes.filter((change) => {
    const key = sha256(JSON.stringify([change.kind, change.provider, change.model_id, change.title, change.source?.id]));
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

function modelSummary(model) {
  const prices = [];
  for (const [field, value] of Object.entries(model.pricing ?? {})) {
    if (Number.isFinite(value)) prices.push(`${field.replaceAll("_", " ")} ${formatMoney(value)}`);
  }
  const shape = [
    model.context_length ? `${model.context_length.toLocaleString()} context` : null,
    [...model.input_modalities, ...model.output_modalities].length ? sortedUnique([...model.input_modalities, ...model.output_modalities]).join("/") : null,
    prices.length ? `${prices.join(", ")} /M` : null
  ].filter(Boolean).join("; ");
  return shape || "catalog entry";
}

function providerLabel(config, providerId) {
  return config.providers.find((provider) => provider.id === providerId)?.label ?? providerId;
}

function markdownFact(value) {
  return String(value)
    .replaceAll("@", "@\u200b")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("`", "\\`")
    .slice(0, 500);
}

export function renderAlert(changes, current, config) {
  const digest = sha256(JSON.stringify(changes)).slice(0, 12);
  const observed = current.observed_at;
  const grouped = new Map();
  for (const change of changes) {
    const bucket = grouped.get(change.provider) ?? [];
    bucket.push(change);
    grouped.set(change.provider, bucket);
  }
  const lines = [
    config.mention,
    "",
    `## River City model-economics alert — ${observed.slice(0, 10)}`,
    "",
    `**Alert digest:** \`${digest}\`  `,
    `**Material changes:** ${changes.length}  `,
    "",
    "Only threshold-crossing pricing, packaging, lifecycle, capability-frontier, or official open-weight changes are included. Raw page churn is suppressed.",
    ""
  ];

  for (const [providerId, providerChanges] of grouped) {
    lines.push(`### ${providerLabel(config, providerId)}`, "");
    providerChanges.forEach((change, index) => {
      lines.push(`#### ${index + 1}. ${markdownFact(change.title)}`, "");
      if (change.kind === "model-launch" && change.new) {
        lines.push(`**What changed.** \`${change.model_id}\` entered the tracked frontier (${change.reasons.join("; ")}). ${modelSummary(change.new)}.`);
      } else if (change.kind === "api-price") {
        lines.push(`**What changed.** OpenRouter’s machine-readable cross-check moved ${change.field.replaceAll("_", " ")} from ${formatMoney(change.old)} to ${formatMoney(change.new)} per 1M tokens (${formatPercent(change.fraction)}). Verify against the official provider page before treating it as canonical.`);
      } else if (change.kind === "official-commercial-facts") {
        lines.push("**What changed.** The official source’s normalized commercial facts changed.");
        if (change.added.length) lines.push("", "Added:", ...change.added.map((fact) => `- ${markdownFact(fact)}`));
        if (change.removed.length) lines.push("", "Removed:", ...change.removed.map((fact) => `- ${markdownFact(fact)}`));
      } else {
        lines.push(`**What changed.** ${change.title}.`);
      }
      lines.push("", `**Likely cost impact.** ${change.impact}`, "", `**Update.** ${change.update}`);
      if (change.source?.url) lines.push("", `**Official evidence.** ${change.source.url}`);
      if (change.evidence_urls?.length) {
        lines.push("", "**Evidence to verify.**", ...change.evidence_urls.map((url) => `- ${url}`));
      }
      lines.push("");
    });
  }

  lines.push("### Coverage", "");
  for (const provider of config.providers) {
    const snapshot = current.providers[provider.id];
    const sourceCount = snapshot?.official_sources?.filter((source) => source.ok).length ?? 0;
    const sourceTotal = snapshot?.official_sources?.length ?? 0;
    lines.push(`- **${provider.label}:** ${snapshot?.models?.length ?? 0} API catalog rows; ${snapshot?.open_weight_models?.length ?? 0} official Hugging Face rows; ${sourceCount}/${sourceTotal} official pages fresh.`);
  }
  lines.push("", "OpenRouter is a cross-check, not canonical price authority. Closed-provider internal cost remains unknown; River City should continue reporting self-host and inferred-provider costs as explicit ranges.", "");
  return {digest, markdown: lines.join("\n")};
}

function renderLatestReport(changes, current, config, seeded) {
  const lines = [
    "# River City model-economics watch",
    "",
    `Observed: ${current.observed_at}`,
    "",
    seeded
      ? "Baseline seeded. No launch or price alert was emitted on the first observation."
      : changes.length > 0
        ? `${changes.length} material change(s) crossed the alert gate.`
        : "No material pricing, packaging, lifecycle, capability-frontier, or open-weight change crossed the alert gate.",
    "",
    "## Coverage",
    ""
  ];
  for (const provider of config.providers) {
    const snapshot = current.providers[provider.id];
    const sourceCount = snapshot?.official_sources?.filter((source) => source.ok).length ?? 0;
    const sourceTotal = snapshot?.official_sources?.length ?? 0;
    lines.push(`- ${provider.label}: ${snapshot?.models?.length ?? 0} OpenRouter rows; ${snapshot?.open_weight_models?.length ?? 0} official Hugging Face rows; ${sourceCount}/${sourceTotal} official pages fresh.`);
  }
  lines.push("", "## Gate", "", `- API price delta: at least ${(config.thresholds.price_fraction * 100).toFixed(0)}%.`, `- Workload cost frontier: at least ${(config.thresholds.cost_frontier_fraction * 100).toFixed(0)}% cheaper.`, `- Context frontier: at least ${config.thresholds.context_multiplier.toFixed(2)}× the prior provider maximum and at least ${config.thresholds.minimum_context_tokens.toLocaleString()} tokens.`, `- Capability: new modality, benchmark frontier, deprecation, or official open-weight artifact.`, `- Packaging: high-signal official facts only; page hash or layout churn alone is never alertable.`, "");
  return lines.join("\n");
}

function ednKey(key) {
  return /^[A-Za-z0-9_.-]+$/.test(key)
    ? `:${key.replaceAll("_", "-")}`
    : JSON.stringify(key);
}

export function toEdn(value) {
  if (value === null || value === undefined) return "nil";
  if (typeof value === "boolean" || typeof value === "number") return String(value);
  if (typeof value === "string") return JSON.stringify(value);
  if (Array.isArray(value)) return `[${value.map(toEdn).join(" ")}]`;
  if (typeof value === "object") {
    return `{${Object.entries(value).sort(([a], [b]) => a.localeCompare(b)).map(([key, item]) => `${ednKey(key)} ${toEdn(item)}`).join(" ")}}`;
  }
  throw new TypeError(`Unsupported EDN value: ${typeof value}`);
}

async function fetchText(url, options = {}) {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), options.timeoutMs ?? FETCH_TIMEOUT_MS);
  try {
    const response = await fetch(url, {
      headers: {"user-agent": USER_AGENT, "accept": options.accept ?? "*/*"},
      signal: controller.signal,
      redirect: "follow"
    });
    if (!response.ok) throw new Error(`HTTP ${response.status} ${response.statusText}`);
    return {
      url: response.url,
      contentType: response.headers.get("content-type") ?? "",
      text: await response.text()
    };
  } finally {
    clearTimeout(timeout);
  }
}

async function fetchJson(url) {
  const response = await fetchText(url, {accept: "application/json"});
  return {url: response.url, data: JSON.parse(response.text)};
}

function discoveredKimiSources(text) {
  const urls = [...text.matchAll(/https:\/\/platform\.kimi\.ai\/docs\/(?:pricing\/[^)\s]+|models\.md|platform-changelog\.md)/g)].map((match) => match[0]);
  return sortedUnique(urls).slice(0, 24).map((url) => ({
    id: `kimi-discovered-${sha256(url).slice(0, 10)}`,
    url
  }));
}

async function collectOfficialSources(provider, previousProvider) {
  const previousSources = new Map((previousProvider?.official_sources ?? []).map((source) => [source.id, source]));
  const configured = [...provider.official_sources];
  const index = configured.find((source) => source.discover_kimi_docs);
  if (index) {
    try {
      const response = await fetchText(index.url);
      configured.push(...discoveredKimiSources(response.text));
    } catch {
      // The configured index itself will carry the failure below.
    }
  }

  const unique = [...new Map(configured.map((source) => [source.id, source])).values()];
  return Promise.all(unique.map(async (source) => {
    try {
      const response = await fetchText(source.url);
      const text = visibleText(response.text, response.contentType).replace(/\s+/g, " ").trim();
      const facts = extractCommercialFacts(response.text, response.contentType);
      return {
        id: source.id,
        url: response.url,
        ok: true,
        observed_at: new Date().toISOString(),
        content_digest: sha256(text),
        facts_digest: sha256(JSON.stringify(facts)),
        facts
      };
    } catch (error) {
      const previous = previousSources.get(source.id);
      return previous
        ? {...previous, ok: false, stale: true, error: String(error.message ?? error)}
        : {id: source.id, url: source.url, ok: false, stale: false, error: String(error.message ?? error), facts: [], facts_digest: sha256("[]")};
    }
  }));
}

async function collectHuggingFaceModels(provider, previousProvider) {
  const all = [];
  for (const org of provider.hugging_face_orgs ?? []) {
    try {
      const url = `https://huggingface.co/api/models?author=${encodeURIComponent(org)}&sort=createdAt&direction=-1&limit=60&full=true`;
      const response = await fetchJson(url);
      all.push(...response.data.map((model) => normalizeHuggingFaceModel(model, provider.id)));
    } catch (error) {
      const previous = previousProvider?.open_weight_models ?? [];
      if (previous.length > 0) return previous;
      console.warn(`Hugging Face collection failed for ${provider.id}: ${error.message}`);
    }
  }
  return [...mapById(all).values()].sort((a, b) => a.id.localeCompare(b.id));
}

async function collectSnapshot(config, previous) {
  let openRouterModels = [];
  let openRouterError = null;
  try {
    const response = await fetchJson(config.openrouter_url);
    openRouterModels = response.data.data ?? [];
  } catch (error) {
    openRouterError = String(error.message ?? error);
    console.warn(`OpenRouter collection failed: ${openRouterError}`);
  }

  const providerEntries = await Promise.all(config.providers.map(async (provider) => {
    const previousProvider = previous?.providers?.[provider.id] ?? {};
    const providerModels = openRouterError
      ? (previousProvider.models ?? [])
      : openRouterModels
          .filter((model) => provider.openrouter_prefixes.some((prefix) => String(model.id ?? "").startsWith(prefix)))
          .map((model) => normalizeOpenRouterModel(model, provider.id))
          .sort((a, b) => a.id.localeCompare(b.id));
    const [officialSources, openWeightModels] = await Promise.all([
      collectOfficialSources(provider, previousProvider),
      collectHuggingFaceModels(provider, previousProvider)
    ]);
    return [provider.id, {
      label: provider.label,
      models: providerModels,
      open_weight_models: openWeightModels,
      official_sources: officialSources
    }];
  }));
  const providers = Object.fromEntries(providerEntries);

  return {
    schema_version: config.schema_version,
    observed_at: new Date().toISOString(),
    openrouter: {url: config.openrouter_url, ok: openRouterError === null, error: openRouterError},
    providers
  };
}

async function readJsonIfExists(filePath) {
  try {
    return JSON.parse(await fs.readFile(filePath, "utf8"));
  } catch (error) {
    if (error.code === "ENOENT") return null;
    throw error;
  }
}

async function writeFileEnsured(filePath, content) {
  await fs.mkdir(path.dirname(filePath), {recursive: true});
  await fs.writeFile(filePath, content);
}

function parseArgs(argv) {
  const options = {
    config: DEFAULT_CONFIG,
    snapshot: DEFAULT_SNAPSHOT,
    report: DEFAULT_REPORT,
    result: DEFAULT_RESULT,
    fixtureCurrent: null,
    now: null
  };
  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index];
    if (arg === "--config") options.config = argv[++index];
    else if (arg === "--snapshot") options.snapshot = argv[++index];
    else if (arg === "--report") options.report = argv[++index];
    else if (arg === "--result") options.result = argv[++index];
    else if (arg === "--fixture-current") options.fixtureCurrent = argv[++index];
    else if (arg === "--now") options.now = argv[++index];
    else throw new Error(`Unknown argument: ${arg}`);
  }
  return options;
}

export async function run(options) {
  const config = JSON.parse(await fs.readFile(options.config, "utf8"));
  const previous = await readJsonIfExists(options.snapshot);
  const current = options.fixtureCurrent
    ? JSON.parse(await fs.readFile(options.fixtureCurrent, "utf8"))
    : await collectSnapshot(config, previous);
  if (options.now) current.observed_at = new Date(options.now).toISOString();
  const seeded = previous === null;
  const changes = detectMaterialChanges(previous, current, config);
  const alert = changes.length > 0 ? renderAlert(changes, current, config) : null;

  await writeFileEnsured(options.snapshot, `${JSON.stringify(current, null, 2)}\n`);
  await writeFileEnsured(options.snapshot.replace(/\.json$/, ".edn"), `${toEdn(current)}\n`);
  await writeFileEnsured(options.report, `${renderLatestReport(changes, current, config, seeded)}\n`);

  let alertPath = null;
  if (alert) {
    alertPath = `reports/model-watch/alerts/${current.observed_at.slice(0, 10)}-${alert.digest}.md`;
    await writeFileEnsured(alertPath, `${alert.markdown}\n`);
  }

  const result = {
    schema_version: 1,
    observed_at: current.observed_at,
    seeded,
    material: changes.length > 0,
    material_change_count: changes.length,
    alert_digest: alert?.digest ?? null,
    alert_path: alertPath,
    snapshot_path: options.snapshot,
    report_path: options.report
  };
  await writeFileEnsured(options.result, `${JSON.stringify(result, null, 2)}\n`);
  return {result, current, changes};
}

const isMain = process.argv[1] && import.meta.url === pathToFileURL(path.resolve(process.argv[1])).href;
if (isMain) {
  run(parseArgs(process.argv.slice(2))).then(({result}) => {
    console.log(JSON.stringify(result));
  }).catch((error) => {
    console.error(error.stack ?? error);
    process.exitCode = 1;
  });
}
