package com.pssst.receiver

import com.pssst.data.network.model.StateResponse
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CopyOnWriteArrayList

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PhoneReceiverTest {

    private val token = "0123456789abcdef0123456789abcdef"
    private val received = CopyOnWriteArrayList<StateResponse>()
    private var savedToken: String? = token
    private val deliveredTo = CopyOnWriteArrayList<String>()
    private var needIcons: List<Int> = emptyList()
    private var nowMs: Long = 42_000L
    private var throwInSnapshot: Boolean = false
    private lateinit var receiver: PhoneReceiver
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var serveJob: Job? = null

    @Before
    fun setUp() {
        receiver = PhoneReceiver(
            port = 0,
            token = { savedToken },
            onSnapshot = {
                if (throwInSnapshot) throw IllegalStateException("simulated db error")
                received += it
                needIcons
            },
            onDeliveredTo = { deliveredTo += it },
            now = { nowMs }
        )
        assertTrue(receiver.bind())
        serveJob = scope.launch { receiver.serve() }
    }

    @After
    fun tearDown() {
        receiver.close()
        scope.cancel()
    }

    private fun send(method: String, path: String, body: String?, auth: String?): Int {
        val conn = URL("http://127.0.0.1:${receiver.localPort}$path").openConnection() as HttpURLConnection
        conn.connectTimeout = 2000
        conn.readTimeout = 6000
        conn.requestMethod = method
        auth?.let { conn.setRequestProperty("Authorization", it) }
        if (body != null) {
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { it.write(body.toByteArray()) }
        }
        return conn.responseCode
    }

    @Test
    fun theSnapshotFromRuneLiteIsAcceptedWithTheToken() {
        val code = send("POST", "/sync", """{"observedAt":1750000000000,"inventory":[{"id":995,"quantity":3}]}""", "Bearer $token")

        assertEquals(200, code)
        assertEquals(1, received.size)
        assertEquals(1_750_000_000_000L, received[0].data?.observedAt)
        assertEquals(995, received[0].data?.inventory?.first()?.id)
        assertEquals("stamped with when it arrived (ms)", 42_000L, received[0].updatedAt)
        assertEquals("Include Bank off: no bank, not an empty one", null, received[0].data?.bank)
        assertEquals("the phone address RuneLite reached", listOf("127.0.0.1"), deliveredTo)
    }

    @Test
    fun aWrongOrMissingTokenIsRefused() {
        assertEquals(401, send("POST", "/sync", "{}", "Bearer wrong"))
        assertEquals(401, send("POST", "/sync", "{}", null))
        assertEquals(401, send("POST", "/sync", "{}", "Basic $token"))
        assertTrue(received.isEmpty())
        assertTrue("a refused request doesn't count as reaching the phone", deliveredTo.isEmpty())
    }

    @Test
    fun repeatedWrongTokensAreRateLimitedWith429UntilWindowExpiresOrTokenChanges() {
        repeat(PhoneReceiver.MAX_AUTH_FAILURES - 1) {
            assertEquals(401, send("POST", "/sync", "{}", "Bearer wrong-$it"))
        }
        // 5th consecutive failure triggers 429 lockout
        assertEquals(429, send("POST", "/sync", "{}", "Bearer wrong-final"))
        // Subsequent attempts during the lockout window are rejected with 429 before checking token
        assertEquals(429, send("POST", "/sync", "{}", "Bearer $token"))

        // Advancing past the lockout window allows valid requests again
        nowMs += PhoneReceiver.AUTH_LOCKOUT_SECONDS * 1000L + 1L
        assertEquals(200, send("POST", "/sync", "{}", "Bearer $token"))
        assertEquals(1, received.size)

        // Trigger lockout again, then verify changing savedToken in the app clears the lockout immediately
        repeat(PhoneReceiver.MAX_AUTH_FAILURES) {
            send("POST", "/sync", "{}", "Bearer wrong-$it")
        }
        assertEquals(429, send("POST", "/sync", "{}", "Bearer $token"))
        savedToken = "goblin-whip"
        assertEquals(200, send("POST", "/sync", "{}", "Bearer goblin-whip"))
        assertEquals(2, received.size)
    }

    @Test
    fun anExceptionInSnapshotHandlerReturns500AndKeepsServerAlive() {
        throwInSnapshot = true
        assertEquals(500, send("POST", "/sync", "{}", "Bearer $token"))
        throwInSnapshot = false
        assertEquals(200, send("POST", "/sync", "{}", "Bearer $token"))
        assertEquals(1, received.size)
    }

    @Test
    fun withNoTokenSavedNothingIsAccepted() {
        savedToken = null
        assertEquals(401, send("POST", "/sync", "{}", "Bearer $token"))
        assertTrue(received.isEmpty())
    }

    @Test
    fun onlyPostSyncExists() {
        assertEquals(405, send("GET", "/sync", null, "Bearer $token"))
        assertEquals(404, send("POST", "/sync-other", "{}", "Bearer $token"))
        assertTrue(received.isEmpty())
    }

    @Test
    fun onlyAJsonObjectIsAccepted() {
        assertEquals(400, send("POST", "/sync", "[1,2]", "Bearer $token"))
        assertEquals(400, send("POST", "/sync", "{nope", "Bearer $token"))
        assertTrue(received.isEmpty())
    }

    @Test
    fun anOversizedBodyIsRefusedBeforeReadingIt() {
        val big = "{\"x\":\"" + "a".repeat(PhoneReceiver.MAX_BODY_BYTES) + "\"}"
        val code = runCatching { send("POST", "/sync", big, "Bearer $token") }.getOrDefault(413)
        assertEquals(413, code)
        assertTrue(received.isEmpty())
    }

    @Test
    fun closingStopsListening() {
        receiver.close()
        assertFalse(receiver.isRunning)
    }

    @Test
    fun onlyHomeNetworkAndMeshVpnPeersAreServed() {
        for (ok in listOf("127.0.0.1", "192.168.1.20", "10.1.2.3", "172.16.0.1", "100.64.0.1", "169.254.3.4", "fd7a:115c:a1e0::1")) {
            assertTrue(ok, PhoneReceiver.isPrivatePeer(InetAddress.getByName(ok)))
        }
        for (no in listOf("8.8.8.8", "172.32.0.1", "100.128.0.1", "2001:db8::1")) {
            assertFalse(no, PhoneReceiver.isPrivatePeer(InetAddress.getByName(no)))
        }
        assertFalse(PhoneReceiver.isPrivatePeer(null))
    }
}
