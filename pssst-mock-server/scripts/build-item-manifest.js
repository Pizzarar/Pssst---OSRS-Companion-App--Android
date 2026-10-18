'use strict';

// Regenerates app/src/main/assets/items-complete.json (the item names, a price snapshot, and icons bundled in the app).
//
//   PSSST_CONTACT=https://github.com/<you>/<repo> node scripts/build-item-manifest.js
//   PSSST_CONTACT=https://github.com/<you>/<repo> node scripts/build-item-manifest.js --icons-only
//
// Fetches RuneLite's static item names, the OSRS Wiki's /mapping and /latest, and backfills any missing
// priority item icons from RuneLite's official static cache (https://static.runelite.net/cache/item/icon/{id}.png).
//
// PSSST_CONTACT is required and goes into the User-Agent: the Wiki asks API users to say who they are and how to
// reach them, and a made-up or browser User-Agent is exactly what gets projects blocked.

const fs = require('fs');
const path = require('path');
const https = require('https');
const { buildManifest } = require('./manifest-lib');

const ASSET_OUTPUT = path.resolve(__dirname, '../../app/src/main/assets/items-complete.json');

// Priority item IDs used across FarmingData.Crop, BirdhouseTracker.House, FarmingPaymentCatalog,
// DailyTasks, PotionStorage, and UI Kit mocks. Missing icons are pulled from static.runelite.net.
const PRIORITY_ICON_IDS = [
  2, 139, 207, 225, 231, 239, 247, 249, 251, 253, 255, 257, 259, 261, 263, 265, 267, 269,
  314, 385, 536, 556, 560, 561, 563, 565, 753, 892, 995, 1391, 1513, 1515, 1517, 1519, 1521,
  1779, 1783, 1793, 1942, 1951, 1955, 1957, 1963, 1965, 1982, 1987, 2108, 2114, 2126, 2398,
  2434, 2481, 2518, 2552, 2866, 2888, 2998, 3000, 3024, 3138, 3261, 3271, 4151, 4251, 4255,
  5295, 5300, 5386, 5396, 5406, 5416, 5438, 5458, 5464, 5478, 5504, 5931, 5968, 5970, 5972,
  5974, 5978, 5980, 5982, 5984, 5986, 5994, 5996, 5998, 6000, 6002, 6004, 6006, 6010, 6012,
  6014, 6016, 6018, 6032, 6034, 6055, 6059, 6063, 6332, 6333, 6685, 7936, 8013, 11738, 11978,
  12625, 12695, 12934, 12954, 13573, 19669, 21483, 21504, 21512, 21515, 21518, 21521, 22192,
  22195, 22198, 22201, 22204, 22929, 22932, 22938, 22939, 22940, 23044, 23685, 23866, 30097,
  31457, 31460, 31481, 31484, 31487, 32904, 32907, 32910
];

const contactArg = (process.argv.find((a) => a.startsWith('--contact=')) || '').slice('--contact='.length);
const contact = (process.env.PSSST_CONTACT || contactArg || '').trim();
if (!contact) {
  console.error('Set PSSST_CONTACT (or pass --contact=<url>) to a URL or email where the Wiki can reach you, e.g.\n' +
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

function fetchIconBase64(id) {
  const url = `https://static.runelite.net/cache/item/icon/${id}.png`;
  return new Promise((resolve, reject) => {
    https.get(url, { headers: { 'User-Agent': USER_AGENT, 'Accept-Encoding': 'identity' } }, (res) => {
      if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        res.resume();
        return resolve(fetchIconBase64(id));
      }
      if (res.statusCode === 404) {
        res.resume();
        return resolve(null);
      }
      if (res.statusCode !== 200) {
        res.resume();
        return reject(new Error(`HTTP ${res.statusCode} from ${url}`));
      }
      const chunks = [];
      res.on('data', (chunk) => chunks.push(chunk));
      res.on('end', () => {
        const buf = Buffer.concat(chunks);
        resolve(buf.length > 0 ? buf.toString('base64') : null);
      });
    }).on('error', reject);
  });
}

const FARMING_CROP_FALLBACK_NAMES = {
  22938: 'Kronos',
  22939: 'Iasor',
  22940: 'Attas',
  23044: 'Hespori'
};

async function backfillPriorityIcons(items) {
  const byId = new Map(items.map((i) => [i.id, i]));
  let addedFallback = false;
  for (const [idStr, fallbackName] of Object.entries(FARMING_CROP_FALLBACK_NAMES)) {
    const id = Number(idStr);
    if (!byId.has(id)) {
      const entry = { id, name: fallbackName };
      items.push(entry);
      byId.set(id, entry);
      addedFallback = true;
    }
  }
  if (addedFallback) {
    items.sort((a, b) => a.id - b.id);
  }
  const missingIds = PRIORITY_ICON_IDS.filter((id) => {
    const entry = byId.get(id);
    return entry && !entry.iconBase64;
  });
  if (missingIds.length === 0) {
    return 0;
  }
  console.log(`Fetching ${missingIds.length} missing priority item icons from static.runelite.net...`);
  let fetched = 0;
  const concurrency = 6;
  for (let i = 0; i < missingIds.length; i += concurrency) {
    const batch = missingIds.slice(i, i + concurrency);
    await Promise.all(batch.map(async (id) => {
      const b64 = await fetchIconBase64(id);
      if (b64) {
        byId.get(id).iconBase64 = b64;
        fetched++;
      }
    }));
  }
  console.log(`Fetched ${fetched}/${missingIds.length} official item icons from RuneLite cache.`);
  return fetched;
}

function loadPrevious() {
  if (!fs.existsSync(ASSET_OUTPUT)) {
    return { items: [], version: 0, generatedAt: 0 };
  }
  const existing = JSON.parse(fs.readFileSync(ASSET_OUTPUT, 'utf8'));
  return Array.isArray(existing)
    ? { items: existing, version: 0, generatedAt: 0 }
    : { items: existing.items || [], version: existing.version || 0, generatedAt: existing.generatedAt || Date.now() };
}

async function run() {
  const iconsOnly = process.argv.includes('--icons-only');
  const previous = loadPrevious();

  let manifest;
  if (iconsOnly && previous.items.length > 0) {
    manifest = {
      version: previous.version + 1,
      generatedAt: previous.generatedAt || Date.now(),
      items: previous.items
    };
  } else {
    console.log('Fetching item names (RuneLite static)...');
    const names = await fetchJson('https://static.runelite.net/cache/item/names.json');
    console.log('Fetching tradeable item mapping (OSRS Wiki)...');
    const mapping = await fetchJson('https://prices.runescape.wiki/api/v1/osrs/mapping');
    console.log('Fetching latest prices (OSRS Wiki)...');
    const latest = (await fetchJson('https://prices.runescape.wiki/api/v1/osrs/latest')).data || {};

    manifest = buildManifest({
      names,
      mapping,
      latest,
      previousItems: previous.items,
      previousVersion: previous.version,
      generatedAt: Date.now()
    });
  }

  await backfillPriorityIcons(manifest.items);

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
