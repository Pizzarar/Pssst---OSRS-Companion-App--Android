'use strict';

// Regenerates app/src/main/assets/items-complete.json (the item names, a price snapshot, and icons bundled in the app).
//
//   PSSST_CONTACT=https://github.com/<you>/<repo> node scripts/build-item-manifest.js
//
// Makes exactly three GET requests: RuneLite's static item names, and the OSRS Wiki's /mapping and /latest.
// Icons are carried over from the existing manifest, not downloaded. Run it rarely (before a release).
//
// PSSST_CONTACT is required and goes into the User-Agent: the Wiki asks API users to say who they are and how to
// reach them, and a made-up or browser User-Agent is exactly what gets projects blocked.

const fs = require('fs');
const path = require('path');
const https = require('https');
const { buildManifest } = require('./manifest-lib');

const ASSET_OUTPUT = path.resolve(__dirname, '../../app/src/main/assets/items-complete.json');

const contact = (process.env.PSSST_CONTACT || '').trim();
if (!contact) {
  console.error('Set PSSST_CONTACT to a URL or email where the Wiki can reach you, e.g.\n' +
    '  PSSST_CONTACT=https://github.com/<you>/<repo> node scripts/build-item-manifest.js');
  process.exit(1);
}
const USER_AGENT = `pssst-manifest-build/1.0 (OSRS companion app; +${contact})`;

function fetchJson(url) {
  return new Promise((resolve, reject) => {
    https.get(url, { headers: { 'User-Agent': USER_AGENT, 'Accept-Encoding': 'identity' } }, (res) => {
      if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        res.resume();
        return resolve(fetchJson(res.headers.location));
      }
      if (res.statusCode !== 200) {
        res.resume();
        return reject(new Error(`HTTP ${res.statusCode} from ${url}`));
      }
      let raw = '';
      res.setEncoding('utf8');
      res.on('data', (chunk) => (raw += chunk));
      res.on('end', () => {
        try {
          resolve(JSON.parse(raw));
        } catch (e) {
          reject(e);
        }
      });
    }).on('error', reject);
  });
}

function loadPrevious() {
  if (!fs.existsSync(ASSET_OUTPUT)) {
    return { items: [], version: 0 };
  }
  const existing = JSON.parse(fs.readFileSync(ASSET_OUTPUT, 'utf8'));
  return Array.isArray(existing)
    ? { items: existing, version: 0 }
    : { items: existing.items || [], version: existing.version || 0 };
}

async function run() {
  console.log('Fetching item names (RuneLite static)...');
  const names = await fetchJson('https://static.runelite.net/cache/item/names.json');
  console.log('Fetching tradeable item mapping (OSRS Wiki)...');
  const mapping = await fetchJson('https://prices.runescape.wiki/api/v1/osrs/mapping');
  console.log('Fetching latest prices (OSRS Wiki)...');
  const latest = (await fetchJson('https://prices.runescape.wiki/api/v1/osrs/latest')).data || {};

  const previous = loadPrevious();
  const manifest = buildManifest({
    names,
    mapping,
    latest,
    previousItems: previous.items,
    previousVersion: previous.version,
    generatedAt: Date.now()
  });

  fs.writeFileSync(ASSET_OUTPUT, JSON.stringify(manifest), 'utf8');
  const priced = manifest.items.filter((i) => i.gePrice).length;
  const icons = manifest.items.filter((i) => i.iconBase64).length;
  console.log(`Wrote v${manifest.version}: ${manifest.items.length} items (${priced} priced, ${icons} with icons), ` +
    `${(fs.statSync(ASSET_OUTPUT).size / (1024 * 1024)).toFixed(2)} MB -> ${ASSET_OUTPUT}`);
}

run().catch((err) => {
  console.error('Failed to build item manifest:', err);
  process.exit(1);
});
