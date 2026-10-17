const fs = require('fs');
const os = require('os');
const path = require('path');
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const PORT = process.env.PORT || 8787;
const HOST = '0.0.0.0';
const defaultDataFile = require.main === module
  ? './state.json'
  : path.join(os.tmpdir(), `pssst-test-state-${process.pid}.json`);
const DATA_FILE = process.env.DATA_FILE || (process.env.DATA_DIR ? path.join(process.env.DATA_DIR, 'state.json') : defaultDataFile);

// In-memory datastore holding player states keyed by user UUID, optionally initialized from file
let datastore = {};

if (DATA_FILE && fs.existsSync(DATA_FILE)) {
  try {
    const raw = fs.readFileSync(DATA_FILE, 'utf8');
    datastore = JSON.parse(raw);
    console.log(`Loaded persisted state for ${Object.keys(datastore).length} user(s) from ${DATA_FILE}`);
  } catch (err) {
    console.error(`Failed to load persisted state from ${DATA_FILE}:`, err.message);
  }
}

function persistState() {
  if (!DATA_FILE) return;
  try {
    const dir = path.dirname(DATA_FILE);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(DATA_FILE, JSON.stringify(datastore, null, 2), 'utf8');
  } catch (err) {
    console.error(`Failed to persist state to ${DATA_FILE}:`, err.message);
  }
}


function mask(token) {
  if (!token || typeof token !== 'string') return 'none';
  return token.length <= 4 ? token : token.slice(0, 4) + '…';
}

// Middlewares
app.use(cors());
app.use(bodyParser.json({ limit: '10mb' }));
app.use(bodyParser.urlencoded({ extended: true }));

app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    console.log(`${new Date().toISOString()} ${req.method} ${req.originalUrl} -> ${res.statusCode} (${Date.now() - start}ms)`);
  });
  next();
});

/**
 * Authentication middleware: extracts and verifies Bearer token UUID from Authorization header
 */
function requireAuth(req, res, next) {
  const authHeader = req.headers['authorization'] || req.headers['Authorization'];
  if (!authHeader) {
    const reason = 'Missing Authorization header';
    console.warn(`Auth failed (401): ${reason}`);
    return res.status(401).json({ error: reason });
  }

  const parts = authHeader.trim().split(/\s+/);
  if (parts.length !== 2 || parts[0].toLowerCase() !== 'bearer' || !parts[1].trim()) {
    const reason = 'Invalid Authorization header format. Expected Bearer <UUID>';
    console.warn(`Auth failed (401): ${reason}`);
    return res.status(401).json({ error: reason });
  }

  req.userUuid = parts[1].trim();
  console.log(`Authenticated request for token: ${mask(req.userUuid)}`);
  next();
}

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'ok', activeUsers: Object.keys(datastore).length });
});

/**
 * POST /sync: Updates the datastore for the authenticated UUID with the incoming payload and timestamp
 */
app.post('/sync', requireAuth, (req, res) => {
  const uuid = req.userUuid;
  const payload = req.body || {};
  const timestamp = Date.now();

  let stateEntry;
  if (payload && typeof payload === 'object' && !Array.isArray(payload)) {
    if ('data' in payload) {
      stateEntry = {
        ...payload,
        updated_at: payload.updated_at || timestamp
      };
    } else {
      stateEntry = {
        data: payload,
        ...payload,
        updated_at: timestamp
      };
    }
  } else {
    stateEntry = {
      data: payload,
      updated_at: timestamp
    };
  }

  datastore[uuid] = stateEntry;
  persistState();

  const payloadKeys = payload && typeof payload === 'object' ? Object.keys(payload) : [];
  const invLen = payload?.inventory?.length ?? payload?.data?.inventory?.length ?? 0;
  const bankLen = payload?.bank?.length ?? payload?.data?.bank?.length ?? 0;
  const varbitsObj = payload?.varbits ?? payload?.data?.varbits ?? {};
  const varbitsCount = typeof varbitsObj === 'object' && varbitsObj !== null ? Object.keys(varbitsObj).length : 0;
  const byteSize = Buffer.byteLength(JSON.stringify(payload));

  console.log(`Sync stored for token: ${mask(uuid)}, keys: [${payloadKeys.join(', ')}], inventory: ${invLen}, bank: ${bankLen}, varbits: ${varbitsCount}, payloadSize: ${byteSize} bytes`);

  return res.status(200).json({ success: true });
});

/**
 * GET /state: Retrieves the stored state for the authenticated UUID, or returns 404 if not found
 */
app.get('/state', requireAuth, (req, res) => {
  const uuid = req.userUuid;
  const state = datastore[uuid];

  if (!state) {
    console.warn(`No state for token ${mask(uuid)}; known tokens: ${Object.keys(datastore).map(mask).join(', ')}`);
    return res.status(404).json({ error: 'State not found for UUID' });
  }

  return res.status(200).json(state);
});

// Start listening
const server = app.listen(PORT, HOST, () => {
  console.log(`Pssst mock server running on http://${HOST}:${PORT}`);
  console.log(`  Host / RuneLite:   http://127.0.0.1:${PORT}`);
  console.log(`  Android emulator:  http://10.0.2.2:${PORT}`);
  const nets = os.networkInterfaces();
  for (const [name, addrs] of Object.entries(nets)) {
    for (const addr of addrs || []) {
      if ((addr.family === 'IPv4' || addr.family === 4) && !addr.internal) {
        console.log(`  Physical device (${name}): http://${addr.address}:${PORT}`);
      }
    }
  }
});

module.exports = { app, datastore, server };
