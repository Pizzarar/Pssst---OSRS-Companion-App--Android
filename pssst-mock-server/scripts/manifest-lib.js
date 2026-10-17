'use strict';

// Pure logic for building the bundled item manifest (no network, no files), so it can be unit tested.

// If the last high and last low trade are further apart than this, one of them is too old to average with.
// Keep in sync with WikiPrices.kt in the app, which applies the same rule to live refreshes.
const MAX_SIDE_SKEW_SECONDS = 7 * 24 * 60 * 60;

const COINS_ID = 995;

function priceOf(high, low, highTime, lowTime) {
  const h = high > 0 ? high : null;
  const l = low > 0 ? low : null;
  if (h !== null && l !== null) {
    if (highTime != null && lowTime != null && Math.abs(highTime - lowTime) > MAX_SIDE_SKEW_SECONDS) {
      return highTime >= lowTime ? h : l;
    }
    return Math.floor((h + l) / 2);
  }
  return h !== null ? h : l;
}

function usableName(name) {
  return typeof name === 'string' && name.trim() !== '' && name !== 'null';
}

/**
 * @param names           RuneLite's static item names: { "<id>": "name" } for every item id (noted and untradeable included)
 * @param mapping         Wiki /mapping: [{ id, name, ... }] for tradeable items; names here are the freshest
 * @param latest          Wiki /latest `data`: { "<id>": { high, highTime, low, lowTime } }
 * @param previousItems   the previous manifest's items; icons are carried over rather than re-downloaded
 * @returns the manifest object written to app/src/main/assets/items-complete.json
 */
function buildManifest({ names, mapping, latest, previousItems, previousVersion, generatedAt }) {
  const wikiNames = new Map(mapping.map((m) => [m.id, m.name]));
  const previous = new Map(previousItems.map((i) => [i.id, i]));

  const ids = new Set([...Object.keys(names).map(Number), ...wikiNames.keys(), ...previous.keys()]);
  const items = [];
  for (const id of [...ids].sort((a, b) => a - b)) {
    const name = [wikiNames.get(id), names[id], previous.get(id) && previous.get(id).name].find(usableName);
    if (!name) {
      continue; // an id nobody can name is not worth a row; the app says "Unknown item (id)"
    }

    const item = { id, name };
    const icon = previous.get(id) && previous.get(id).iconBase64;
    if (icon) {
      item.iconBase64 = icon;
    }

    // Prices only for items the Wiki lists as tradeable. No fallback to alch value: that is not a GE price.
    let price = null;
    if (id === COINS_ID) {
      price = 1;
    } else if (wikiNames.has(id) && latest[id]) {
      const p = latest[id];
      price = priceOf(p.high, p.low, p.highTime, p.lowTime);
    }
    if (price) {
      item.gePrice = price;
    }
    items.push(item);
  }
  return { version: previousVersion + 1, generatedAt, items };
}

module.exports = { priceOf, buildManifest, MAX_SIDE_SKEW_SECONDS };
