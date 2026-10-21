// SPDX-License-Identifier: GPL-3.0-or-later
import test from 'node:test';
import assert from 'node:assert/strict';
import { mkdtemp, readFile } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { createHash } from 'node:crypto';
import { boundedGet, executeResearch } from '../scripts/research_worker.mjs';
const policy = { 'queries-per-run': 2, 'max-response-bytes': 4096, 'timeout-ms': 100, 'max-candidates': 4 };
const request = { policy, 'request-id': 'research-a', label: 'Hormuz visible passages' };
const response = items => new Response(JSON.stringify({ message: { items } }));
test('executes bounded research and retains exact source bytes and candidate-only claims', async () => {
  const dir = await mkdtemp(join(tmpdir(), 'river-research-')); let calls = 0;
  const result = await executeResearch(request, dir, { fetcher: async url => {
    assert.equal(new URL(url).hostname, 'api.crossref.org'); calls++;
    return response([{ title: ['AIS coverage uncertainty'], DOI: '10.1234/test', type: 'journal-article' }]);
  }});
  assert.equal(calls, 2); assert.equal(result.status, 'completed');
  assert.equal(result.candidates.length, 1); assert.equal(result.candidates[0].peer_review, 'not-verified');
  assert.equal(result['production-change?'], false);
  for (const e of result.evidence) {
    const bytes = await readFile(join(dir, e['raw-path']));
    assert.equal(createHash('sha256').update(bytes).digest('hex'), e.sha256);
  }
});
test('null is a retained successful search outcome, not absence proof', async () => {
  const dir = await mkdtemp(join(tmpdir(), 'river-null-'));
  const result = await executeResearch(request, dir, { fetcher: async () => response([]) });
  assert.equal(result.status, 'null'); assert.equal(result.evidence.length, 2);
  assert.match(result.limitation, /do not establish absence/);
});
test('malformed, HTTP-failed and rejected fetches remain failed, never null', async () => {
  for (const fetcher of [async () => new Response('bad-json'), async () => new Response('unavailable', { status: 503 }),
    async () => { throw new Error('timeout'); }]) {
    const dir = await mkdtemp(join(tmpdir(), 'river-failed-'));
    const result = await executeResearch(request, dir, { fetcher });
    assert.equal(result.status, 'failed'); assert.equal(result.errors.length, 2);
  }
});
test('byte and hard call budgets fail closed', async () => {
  await assert.rejects(boundedGet('https://api.crossref.org/works', { timeoutMs: 100, maxBytes: 3,
    fetcher: async () => new Response('longer') }), /byte-budget/);
  const dir = await mkdtemp(join(tmpdir(), 'river-budget-'));
  await assert.rejects(executeResearch({ ...request, policy: { ...policy, 'queries-per-run': 5 } }, dir), /hard-research-budget/);
});
test('research follows discovered measurement vocabulary and retains proposed tests', async () => {
  const queries = [];
  const dir = await mkdtemp(join(tmpdir(), 'river-follow-'));
  const result = await executeResearch(request, dir, { fetcher: async url => {
    queries.push(new URL(url).searchParams.get('query.bibliographic'));
    return response([{ title: ['AIS coverage'], DOI: '10.1234/ais', abstract: '<p>Satellite port activity measures sampling error.</p>' }]);
  }});
  assert.match(queries[1], /AIS satellite observations independent validation/);
  assert.equal(result.candidates[0].inspected_text, 'title-and-abstract');
  assert.ok(result.proposals.every(p => p['source-sha256'] && p['production-change?'] === false));
  assert.ok(result.tests[0].falsifies);
});
test('an actual abort deadline ends a pending provider request', async () => {
  await assert.rejects(boundedGet('https://api.crossref.org/works', {
    timeoutMs: 10, maxBytes: 100,
    fetcher: async (_url, { signal }) => new Promise((_, reject) => {
      signal.addEventListener('abort', () => reject(signal.reason), { once: true });
      // Keep the test process alive while AbortSignal's unref timer expires.
      setTimeout(() => reject(new Error('deadline failed')), 40);
    }),
  }), error => error.name === 'TimeoutError');
});
