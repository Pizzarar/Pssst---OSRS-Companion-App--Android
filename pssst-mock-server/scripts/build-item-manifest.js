const fs = require('fs');
const path = require('path');
const https = require('https');

const ASSET_OUTPUT = path.resolve(__dirname, '../../app/src/main/assets/items-complete.json');
const STATE_FILE = path.resolve(__dirname, '../state.json');

const KNOWN_NAMES = {
  995: 'Coins',
  4151: 'Abyssal whip',
  11802: 'Armadyl godsword',
  20997: 'Twisted bow',
  946: 'Knife',
  11865: 'Slayer helmet (i)'
};

function fetchJson(url) {
  return new Promise((resolve, reject) => {
    https.get(url, { headers: { 'User-Agent': 'pssst-companion-build/1.0 (contact: github.com/pssst-project)' } }, (res) => {
      if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        return resolve(fetchJson(res.headers.location));
      }
      if (res.statusCode !== 200) {
        return reject(new Error(`HTTP ${res.statusCode} from ${url}`));
      }
      let raw = '';
      res.on('data', chunk => raw += chunk);
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

function fetchBase64Png(url) {
  return new Promise((resolve, reject) => {
    https.get(url, { headers: { 'User-Agent': 'pssst-companion-build/1.0 (contact: github.com/pssst-project)' } }, (res) => {
      if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        return resolve(fetchBase64Png(res.headers.location));
      }
      if (res.statusCode !== 200) {
        return resolve(null); // icon might not exist in cache
      }
      const chunks = [];
      res.on('data', chunk => chunks.push(chunk));
      res.on('end', () => {
        const buffer = Buffer.concat(chunks);
        resolve(buffer.toString('base64'));
      });
    }).on('error', () => resolve(null));
  });
}

async function run() {
  console.log('Fetching OSRS Wiki item mappings...');
  const mapping = await fetchJson('https://prices.runescape.wiki/api/v1/osrs/mapping');
  console.log(`Loaded ${mapping.length} items from OSRS Wiki.`);

  // Collect item IDs from active state.json if available
  const priorityIds = new Set([995, 4151, 11802, 20997, 946, 11865]);
  if (fs.existsSync(STATE_FILE)) {
    try {
      const stateData = JSON.parse(fs.readFileSync(STATE_FILE, 'utf8'));
      for (const token of Object.keys(stateData)) {
        const entry = stateData[token];
        const data = entry.data || entry;
        for (const item of (data.inventory || [])) {
          if (item && item.id) priorityIds.add(item.id);
        }
        for (const item of (data.bank || [])) {
          if (item && item.id) priorityIds.add(item.id);
        }
      }
      console.log(`Found ${priorityIds.size} unique items in local player state & defaults.`);
    } catch (e) {
      console.warn('Could not read state.json for priority IDs:', e.message);
    }
  }

  // Also include first 300 common items from mapping if not already included
  for (let i = 0; i < Math.min(mapping.length, 300); i++) {
    priorityIds.add(mapping[i].id);
  }

  console.log(`Downloading icons for ${priorityIds.size} priority items...`);
  const iconMap = new Map();
  const idArray = Array.from(priorityIds);
  const BATCH_SIZE = 25;

  for (let i = 0; i < idArray.length; i += BATCH_SIZE) {
    const batch = idArray.slice(i, i + BATCH_SIZE);
    await Promise.all(batch.map(async (id) => {
      const iconUrl = `https://static.runelite.net/cache/item/icon/${id}.png`;
      const base64 = await fetchBase64Png(iconUrl);
      if (base64) {
        iconMap.set(id, base64);
      }
    }));
    process.stdout.write(`Downloaded icons: ${Math.min(i + BATCH_SIZE, idArray.length)}/${idArray.length}\r`);
  }
  console.log(`\nSuccessfully downloaded ${iconMap.size} item icons.`);

  console.log('Fetching bulk GE price snapshot from OSRS Wiki (/latest)...');
  let prices = {};
  try {
    const priceRes = await fetchJson('https://prices.runescape.wiki/api/v1/osrs/latest');
    prices = priceRes.data || {};
    console.log(`Loaded ${Object.keys(prices).length} item prices.`);
  } catch (e) {
    console.warn('Could not fetch latest prices:', e.message);
  }

  // Build manifest array for Room database
  const manifest = mapping.map(item => {
    const priceObj = prices[item.id];
    const gePrice = (item.id === 995) ? 1 : (priceObj && (priceObj.high || priceObj.low) ? (priceObj.high || priceObj.low) : (item.highalch || 0));
    return {
      id: item.id,
      name: KNOWN_NAMES[item.id] || item.name || `Item #${item.id}`,
      iconBase64: iconMap.get(item.id) || null,
      gePrice: Number(gePrice) || 0
    };
  });

  // Ensure priority items (e.g. Coins 995) are present even if not in GE mapping
  for (const id of priorityIds) {
    const existing = manifest.find(m => m.id === id);
    if (!existing) {
      const priceObj = prices[id];
      const gePrice = (id === 995) ? 1 : (priceObj && (priceObj.high || priceObj.low) ? (priceObj.high || priceObj.low) : 0);
      manifest.push({
        id,
        name: KNOWN_NAMES[id] || `Item #${id}`,
        iconBase64: iconMap.get(id) || null,
        gePrice: Number(gePrice) || 0
      });
    } else if (KNOWN_NAMES[id]) {
      existing.name = KNOWN_NAMES[id];
    }
  }

  fs.writeFileSync(ASSET_OUTPUT, JSON.stringify(manifest), 'utf8');
  console.log(`Wrote ${manifest.length} items to ${ASSET_OUTPUT} (${(fs.statSync(ASSET_OUTPUT).size / (1024 * 1024)).toFixed(2)} MB).`);
}

run().catch(err => {
  console.error('Failed to build item manifest:', err);
  process.exit(1);
});
