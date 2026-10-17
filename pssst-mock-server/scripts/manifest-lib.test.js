'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const { priceOf, buildManifest } = require('./manifest-lib');

const DAY = 24 * 60 * 60;
const NOW = 1_800_000_000;

test('price is the midpoint of recent high and low trades', () => {
  assert.equal(priceOf(200, 100, NOW, NOW - DAY), 150);
});

test('the newer side alone is used when the other is over a week older', () => {
  assert.equal(priceOf(200, 100, NOW, NOW - 30 * DAY), 200);
  assert.equal(priceOf(200, 100, NOW - 30 * DAY, NOW), 100);
});

test('a single known side is used, and no side means no price', () => {
  assert.equal(priceOf(200, null, NOW, null), 200);
  assert.equal(priceOf(null, 100, null, NOW), 100);
  assert.equal(priceOf(null, null, null, null), null);
  assert.equal(priceOf(0, 0, NOW, NOW), null);
});

const base = {
  names: { 4151: 'Abyssal whip', 4152: 'Abyssal whip', 995: 'Coins', 7: 'null', 8: 'Cannon base', 9000: 'Quest thing' },
  mapping: [{ id: 4151, name: 'Abyssal whip (current)' }, { id: 995, name: 'Coins' }, { id: 34631, name: 'Brand new item' }],
  latest: { 4151: { high: 2000, highTime: NOW, low: 1000, lowTime: NOW }, 34631: { high: 5, highTime: NOW, low: null, lowTime: null } },
  previousItems: [{ id: 4151, name: 'Old whip name', iconBase64: 'aWNvbg==', gePrice: 1 }],
  previousVersion: 3,
  generatedAt: 123
};

test('manifest covers every named id, with Wiki names winning and noted ids kept', () => {
  const m = buildManifest(base);
  const byId = new Map(m.items.map((i) => [i.id, i]));

  assert.equal(m.version, 4);
  assert.equal(m.generatedAt, 123);
  assert.equal(byId.get(4151).name, 'Abyssal whip (current)'); // Wiki is fresher than the cache names
  assert.equal(byId.get(4152).name, 'Abyssal whip');            // noted id: named, no price of its own
  assert.equal(byId.get(9000).name, 'Quest thing');             // untradeable: named
  assert.equal(byId.get(34631).name, 'Brand new item');         // newer than the names file: Wiki covers it
  assert.equal(byId.has(7), false);                              // "null" is not a name
});

test('prices exist only for tradeable items, never from alch values, and coins are 1', () => {
  const byId = new Map(buildManifest(base).items.map((i) => [i.id, i]));
  assert.equal(byId.get(4151).gePrice, 1500);
  assert.equal(byId.get(34631).gePrice, 5);
  assert.equal(byId.get(995).gePrice, 1);
  assert.equal(byId.get(4152).gePrice, undefined);
  assert.equal(byId.get(9000).gePrice, undefined);
  assert.equal(byId.get(8).gePrice, undefined);
});

test('icons are carried over from the previous manifest', () => {
  const byId = new Map(buildManifest(base).items.map((i) => [i.id, i]));
  assert.equal(byId.get(4151).iconBase64, 'aWNvbg==');
  assert.equal(byId.get(4152).iconBase64, undefined);
});

test('items are sorted by id so regenerating produces small diffs', () => {
  const ids = buildManifest(base).items.map((i) => i.id);
  assert.deepEqual(ids, [...ids].sort((a, b) => a - b));
});
