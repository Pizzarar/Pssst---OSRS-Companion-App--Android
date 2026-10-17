const http = require('http');

function request(options, postData) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let body = '';
      res.on('data', (chunk) => { body += chunk; });
      res.on('end', () => {
        let parsed = null;
        try {
          parsed = JSON.parse(body);
        } catch (e) {
          parsed = body;
        }
        resolve({ statusCode: res.statusCode, headers: res.headers, body: parsed });
      });
    });

    req.on('error', (err) => reject(err));

    if (postData) {
      req.write(typeof postData === 'string' ? postData : JSON.stringify(postData));
    }
    req.end();
  });
}

async function runTests() {
  const { server } = require('./server');

  // Allow server to start listening
  await new Promise((resolve) => setTimeout(resolve, 500));

  try {
    const TEST_UUID = '123e4567-e89b-12d3-a456-426614174000';

    console.log('Test 1: Health check');
    const health = await request({
      hostname: '127.0.0.1',
      port: 8787,
      path: '/health',
      method: 'GET'
    });
    console.assert(health.statusCode === 200, `Health failed: ${health.statusCode}`);
    console.assert(health.body.status === 'ok', `Expected status ok: ${JSON.stringify(health.body)}`);
    console.log('Test 1 Passed: Health endpoint returned 200 OK');

    console.log('Test 2: Request without Authorization header returns 401');
    const noAuth = await request({
      hostname: '127.0.0.1',
      port: 8787,
      path: '/state',
      method: 'GET'
    });
    console.assert(noAuth.statusCode === 401, `Expected 401, got ${noAuth.statusCode}`);
    console.log('Test 2 Passed: 401 Unauthorized for missing token');

    console.log('Test 3: Request for non-existent UUID returns 404');
    const notFound = await request({
      hostname: '127.0.0.1',
      port: 8787,
      path: '/state',
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${TEST_UUID}`
      }
    });
    console.assert(notFound.statusCode === 404, `Expected 404, got ${notFound.statusCode}`);
    console.log('Test 3 Passed: 404 Not Found for new UUID');

    console.log('Test 4: POST /sync updates player state');
    const mockState = {
      inventory: [{ id: 4151, quantity: 1 }, { id: 995, quantity: 100000 }],
      bank: [{ id: 1127, quantity: 5 }],
      varbits: { "3948": 95, "3949": 2500000 },
      slayer: { taskName: "Abyssal Demons", amount: 150 }
    };

    const syncRes = await request({
      hostname: '127.0.0.1',
      port: 8787,
      path: '/sync',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${TEST_UUID}`
      }
    }, mockState);

    console.assert(syncRes.statusCode === 200, `Expected 200, got ${syncRes.statusCode}`);
    console.assert(syncRes.body.success === true, `Expected success true: ${JSON.stringify(syncRes.body)}`);
    console.log('Test 4 Passed: POST /sync returned 200 { success: true }');

    console.log('Test 5: GET /state retrieves updated player state');
    const stateRes = await request({
      hostname: '127.0.0.1',
      port: 8787,
      path: '/state',
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${TEST_UUID}`
      }
    });

    console.assert(stateRes.statusCode === 200, `Expected 200, got ${stateRes.statusCode}`);
    console.assert(stateRes.body.updated_at > 0, `Expected updated_at > 0, got ${stateRes.body.updated_at}`);
    console.assert(stateRes.body.data !== undefined, 'Expected data field to be populated');
    console.assert(stateRes.body.data.inventory.length === 2, `Expected 2 inventory items`);
    console.assert(stateRes.body.data.varbits["3948"] === 95, 'Expected approval varbit to be 95');
    console.log('Test 5 Passed: GET /state returned full state with matching data and updated_at');

    console.log('\nAll 5 automated tests passed successfully!');
  } finally {
    server.close();
  }
}

runTests().catch((err) => {
  console.error('Test failed:', err);
  process.exit(1);
});
