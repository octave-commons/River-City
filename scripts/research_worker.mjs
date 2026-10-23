// SPDX-License-Identifier: GPL-3.0-or-later
// Bounded literature discovery adapter. Metadata is a research lead, never a
// confirmed real-world observation or a reviewed production indicator.
import { createHash } from 'node:crypto';
import { readFile, writeFile } from 'node:fs/promises';
import { resolve } from 'node:path';
import { pathToFileURL } from 'node:url';

export async function boundedGet(url, { timeoutMs, maxBytes, fetcher = fetch }) {
  const response = await fetcher(url, {
    redirect: 'error', signal: AbortSignal.timeout(timeoutMs),
    headers: { accept: 'application/json', 'user-agent': 'River-City-Research/1.0 (https://github.com/octave-commons/River-City)' },
  });
  if (!response.body) throw new Error('missing-response-body');
  const chunks = []; let size = 0;
  try {
    for await (const chunk of response.body) {
      size += chunk.length;
      if (size > maxBytes) throw new Error('response-byte-budget-exceeded');
      chunks.push(Buffer.from(chunk));
    }
  } catch (error) {
    throw error;
  }
  return { status: response.status, bytes: Buffer.concat(chunks) };
}

const decodeEntities = value => value
  .replace(/&lt;/gu, '<')
  .replace(/&gt;/gu, '>')
  .replace(/&quot;/gu, '"')
  .replace(/&#39;|&apos;/gu, "'")
  .replace(/&amp;/gu, '&');

const stripMarkup = value =>
  value.replace(/<\/?[A-Za-z][\w:.-]*(?:\s[^<>]*)?\/?>/gu, ' ');

export function candidatesFromCrossref(payload, limit) {
  if (!payload || !Array.isArray(payload.message?.items)) throw new Error('invalid-crossref-response');
  return payload.message.items.slice(0, limit).flatMap(item => {
    const title = item.title?.[0];
    if (typeof title !== 'string' || typeof item.DOI !== 'string' || !/^10\.\d{4,9}\/\S+$/u.test(item.DOI)) return [];
    const abstract = typeof item.abstract === 'string'
      ? stripMarkup(decodeEntities(item.abstract)).replace(/\s+/gu, ' ').trim().slice(0, 6000) : null;
    const text = `${title} ${abstract ?? ''}`;
    const indicators = [
      ['AIS', /\bAIS\b|automatic identification system/iu],
      ['satellite observations', /satellite|remote sensing/iu],
      ['freight rates', /freight|shipping cost/iu],
      ['insurance restrictions', /insurance|war.risk/iu],
      ['recipient delivery', /delivery volume|cargo delivery|recipient.allocat|import volume|export volume|trade.flow/iu],
      ['port activity', /port activity|port call|throughput/iu],
    ].filter(([, pattern]) => pattern.test(text)).map(([indicator]) => indicator);
    return [{ title: title.slice(0, 500), abstract, indicators,
      inspected_text: abstract ? 'title-and-abstract' : 'title-only',
      proposed_test: 'Compare this candidate measurement with date-, geography-, unit-, coverage- and revision-matched canonical observations; disagreement that vanishes after matching falsifies the source-divergence explanation.', uri: `https://doi.org/${encodeURI(item.DOI)}`,
      publication_type: typeof item.type === 'string' ? item.type : 'unknown',
      peer_review: 'not-verified', evidence_level: 'bibliographic-metadata',
      // An abstract is retained in raw evidence if supplied. No metadata field
      // is treated as a paper's experimental finding without a paper review.
      status: 'candidate', production_change: false }];
  });
}

export async function executeResearch(request, directory, {
  fetcher = fetch, now = () => new Date().toISOString(),
} = {}) {
  const policy = request.policy;
  for (const key of ['queries-per-run', 'max-response-bytes', 'timeout-ms', 'max-candidates']) {
    if (!Number.isSafeInteger(policy?.[key]) || policy[key] <= 0) throw new Error(`invalid-policy:${key}`);
  }
  if (policy['queries-per-run'] > 4 || policy['max-response-bytes'] > 1048576 ||
      policy['timeout-ms'] > 30000 || policy['max-candidates'] > 10) throw new Error('hard-research-budget-exceeded');
  if (typeof request['request-id'] !== 'string' || typeof request.label !== 'string') throw new Error('invalid-request');
  const evidence = [], candidates = [], errors = [];
  const queries = [
    `${request.label} measurement reliability coverage uncertainty`,
    `${request.label} alternative indicators usable capacity delivery disruption`,
    `${request.label} source revisions sampling bias`,
    `${request.label} independent validation empirical measurement`,
  ].slice(0, policy['queries-per-run']);
  for (const [index, plannedQuery] of queries.entries()) {
    // A second stage follows the first source's measurement vocabulary. The
    // exact executed query and complete source response are retained.
    const discovered = [...new Set(candidates.flatMap(c => c.indicators))].slice(0, 2);
    const query = index === 1 && discovered.length
      ? `${request.label} ${discovered.join(' ')} independent validation uncertainty` : plannedQuery;
    const url = new URL('https://api.crossref.org/works');
    url.searchParams.set('query.bibliographic', query);
    url.searchParams.set('rows', String(policy['max-candidates']));
    url.searchParams.set('select', 'DOI,title,type,published,abstract,link');
    try {
      const { status, bytes } = await boundedGet(url.href, {
        timeoutMs: policy['timeout-ms'], maxBytes: policy['max-response-bytes'], fetcher,
      });
      const filename = `query-${index}.json`;
      await writeFile(resolve(directory, filename), bytes, { flag: 'wx' });
      const source = { uri: url.href, 'raw-path': filename,
        sha256: createHash('sha256').update(bytes).digest('hex'),
        'retrieved-at': now(), 'evidence-level': 'bibliographic-metadata',
        'http-status': status, query };
      evidence.push(source);
      if (status !== 200) throw new Error(`http-${status}`);
      const items = candidatesFromCrossref(JSON.parse(bytes.toString('utf8')), policy['max-candidates']);
      candidates.push(...items.map(item => ({ ...item, 'source-sha256': source.sha256 })));
    } catch (error) {
      errors.push({ query, error: String(error.message).slice(0, 500) });
    }
  }
  const unique = [...new Map(candidates.map(c => [c.uri, c])).values()].slice(0, policy['max-candidates']);
  return { 'request-id': request['request-id'],
    status: errors.length ? 'failed' : unique.length ? 'completed' : 'null',
    evidence, candidates: unique, errors,
    proposals: unique.flatMap(c => c.indicators.map(indicator => ({
      status: 'proposed', indicator, 'source-uri': c.uri, 'source-sha256': c['source-sha256'],
      rationale: `Named in retrieved ${c.inspected_text}; relevance and measurement comparability require review.`,
      'production-change?': false,
    }))),
    tests: unique.map(c => ({ status: 'proposed', test: c.proposed_test,
      falsifies: 'Reject common-basis disagreement if source revisions or matched coverage remove it.',
      'source-uri': c.uri, 'source-sha256': c['source-sha256'] })),
    limitation: 'Bounded literature search and available-abstract inspection. Full-text experimental claims and indicator adoption require review. Empty results do not establish absence.',
    runtime: { node: process.version },
    'requests-attempted': queries.length, 'max-cost-usd': 0,
    'production-change?': false };
}

if (process.argv[1] && import.meta.url === pathToFileURL(resolve(process.argv[1])).href) {
  const directory = resolve(process.argv[2]);
  const request = JSON.parse(await readFile(resolve(directory, 'request.json'), 'utf8'));
  const result = await executeResearch(request, directory);
  await writeFile(resolve(directory, 'worker-result.json'), `${JSON.stringify(result, null, 2)}\n`, { flag: 'wx' });
}
