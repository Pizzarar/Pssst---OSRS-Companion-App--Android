package com.pssst.worker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.google.gson.Gson
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.StateApiService
import com.pssst.data.network.model.ItemEntryDto
import com.pssst.data.network.model.StateDataDto
import com.pssst.data.network.model.StateResponse
import com.pssst.util.StateDiffAnalyzer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SyncWorkerTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var mockWebServer: MockWebServer
    private val gson = Gson()
    private val testUuid = "12345678-1234-1234-1234-123456789abc"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        database.close()
        mockWebServer.shutdown()
        SyncWorker.defaultApiService = null
        SyncWorker.defaultDatabase = null
        SyncWorker.defaultPreferences = null
    }

    @Test
    fun syncWorker_successfullyFetchesParsesAndWritesToRoom() = runBlocking {
        // Mock API response payload
        val mockJson = """
            {
              "data": {
                "inventory": [
                  {"id": 4151, "quantity": 1},
                  {"id": 995, "quantity": 50000}
                ],
                "bank": [
                  {"id": 560, "quantity": 2500}
                ],
                "varbits": {
                  "4771": 8,
                  "4772": 3
                },
                "slayer": {
                  "taskName": "Abyssal demons",
                  "amount": 0
                }
              },
              "updated_at": 1729100000
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJson)
                .addHeader("Content-Type", "application/json")
        )

        // Seed previous state in Room with patch 4771 growing (stage 5)
        val previousEntity = PlayerStateEntity(
            testUuid,
            1729000000L,
            "{}",
            """{"4771":5,"4772":2}""",
            """{"taskName":"Abyssal demons","amount":45}""",
            "[]",
            "[]",
            System.currentTimeMillis() - 60000
        )
        database.playerStateDao().upsert(previousEntity)

        // Create API service pointing to MockWebServer
        val apiService = StateApiService.create(
            preferencesRepository = TestSyncUserPreferencesRepository(testUuid, mockWebServer.url("/").toString()),
            okHttpClient = OkHttpClient.Builder().build()
        )

        // Configure test worker dependencies
        SyncWorker.defaultApiService = apiService
        SyncWorker.defaultDatabase = database
        SyncWorker.defaultPreferences = TestSyncUserPreferencesRepository(testUuid, mockWebServer.url("/").toString())

        // Build worker via WorkManager test harness
        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setInputData(workDataOf(SyncWorker.KEY_UUID to testUuid))
            .build()

        // Execute worker
        val result = worker.doWork()

        // Verify WorkManager Result
        assertEquals(ListenableWorker.Result.success(), result)

        // Verify MockWebServer received correct GET request and Authorization header
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/state", recordedRequest.path)
        assertEquals("Bearer $testUuid", recordedRequest.getHeader("Authorization"))

        // Verify Room Database persistence
        val savedEntity = database.playerStateDao().getStateByUuid(testUuid)
        assertNotNull("Saved entity in Room should not be null", savedEntity)
        assertEquals(testUuid, savedEntity!!.uuid)
        assertEquals(1729100000000L, savedEntity.updatedAt) // worker sends seconds; SyncWorker normalizes to ms
        assertTrue(savedEntity.rawJson.contains("4151"))
        assertTrue(savedEntity.varbitsJson.contains(""""4771":8"""))
        assertTrue(savedEntity.slayerJson.contains("Abyssal demons"))

        // Verify StateDiffAnalyzer detected the completed patch and slayer task
        val parsedResponse = gson.fromJson(mockJson, StateResponse::class.java)
        val alerts = StateDiffAnalyzer.findCompletedRunsOrPatches(previousEntity, parsedResponse)
        // Farming alerts are timed reminders now, not state diffs
        assertEquals(1, alerts.size)
        assertTrue(alerts.any { it.title.contains("Slayer Task Complete") })
    }

    @Test
    fun syncWorker_failsGracefullyWhenUuidMissing() = runBlocking {
        SyncWorker.defaultDatabase = database
        SyncWorker.defaultPreferences = TestSyncUserPreferencesRepository(null)

        val worker = TestListenableWorkerBuilder<SyncWorker>(context).build()
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun syncWorker_dynamicallyReadsServerUrlFromRepository() = runBlocking {
        val dynamicServerUrl = mockWebServer.url("/custom-host/").toString()
        val emptyPayloadJson = """
            {
              "data": {
                "inventory": [],
                "bank": [],
                "varbits": {},
                "slayer": {}
              },
              "updated_at": 1729200000
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(emptyPayloadJson)
                .addHeader("Content-Type", "application/json")
        )

        SyncWorker.defaultApiService = null
        SyncWorker.defaultDatabase = database
        SyncWorker.defaultPreferences = TestSyncUserPreferencesRepository(testUuid, dynamicServerUrl)

        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setInputData(workDataOf(SyncWorker.KEY_UUID to testUuid))
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/custom-host/state", recordedRequest.path)
        assertEquals("Bearer $testUuid", recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun syncWorker_returnsSuccessOn404() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"error":"State not found for UUID"}""")
        )

        val apiService = StateApiService.create(
            preferencesRepository = TestSyncUserPreferencesRepository(testUuid, mockWebServer.url("/").toString()),
            okHttpClient = OkHttpClient.Builder().build()
        )

        SyncWorker.defaultApiService = apiService
        SyncWorker.defaultDatabase = database
        SyncWorker.defaultPreferences = TestSyncUserPreferencesRepository(testUuid, mockWebServer.url("/").toString())

        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setInputData(workDataOf(SyncWorker.KEY_UUID to testUuid))
            .build()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun syncWorker_withNoServerConfiguredMakesNoRequestAndSaysSo() = runBlocking {
        val prefs = TestSyncUserPreferencesRepository(testUuid, serverUrl = null)
        SyncWorker.defaultApiService = StateApiService.create(prefs, OkHttpClient.Builder().build())
        SyncWorker.defaultDatabase = database
        SyncWorker.defaultPreferences = prefs

        val result = TestListenableWorkerBuilder<SyncWorker>(context)
            .setInputData(workDataOf(SyncWorker.KEY_UUID to testUuid))
            .build()
            .doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        assertEquals("no central server to fall back to", 0, mockWebServer.requestCount)
        assertEquals("No server configured", prefs.lastStatus)
    }

    @Test
    fun syncWorker_neverLogsAnyPartOfTheToken() = runBlocking {
        val secret = "SECRETTOKEN-9f8e7d6c5b4a39281706"
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"data": {"inventory": []}, "updated_at": 1}"""))
        val prefs = TestSyncUserPreferencesRepository(secret, mockWebServer.url("/").toString())
        SyncWorker.defaultApiService = StateApiService.create(prefs, OkHttpClient.Builder().build())
        SyncWorker.defaultDatabase = database
        SyncWorker.defaultPreferences = prefs
        ShadowLog.clear()

        TestListenableWorkerBuilder<SyncWorker>(context)
            .setInputData(workDataOf(SyncWorker.KEY_UUID to secret))
            .build()
            .doWork()

        val logged = ShadowLog.getLogs().joinToString("\n") { it.msg ?: "" }
        assertTrue("worker should have logged something", logged.isNotEmpty())
        for (length in listOf(4, 8, secret.length)) {
            assertFalse("log contains the first $length chars of the token", logged.contains(secret.take(length)))
        }
    }


    // --- riding out drops, and stopping when told to -----------------------------------------------------------

    private fun syncOnce(prefs: TestSyncUserPreferencesRepository): ListenableWorker.Result = runBlocking {
        SyncWorker.defaultApiService = StateApiService.create(prefs, OkHttpClient.Builder().build())
        SyncWorker.defaultDatabase = database
        SyncWorker.defaultPreferences = prefs
        TestListenableWorkerBuilder<SyncWorker>(context).build().doWork()
    }

    private fun serverPrefs() = TestSyncUserPreferencesRepository(testUuid, mockWebServer.url("/").toString())

    @Test
    fun syncWorker_oneOrTwoFailuresAreRetriedAndLookLikeNothingHappened() {
        val prefs = serverPrefs()
        prefs.lastStatus = "OK 10:00:00 inv=3 bank=100 slayer=none"

        mockWebServer.enqueue(MockResponse().setResponseCode(503))
        assertEquals(ListenableWorker.Result.retry(), syncOnce(prefs))
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST))
        assertEquals(ListenableWorker.Result.retry(), syncOnce(prefs))

        assertEquals(2, prefs.failures)
        assertEquals("the last good status is still what the user sees", "OK 10:00:00 inv=3 bank=100 slayer=none", prefs.lastStatus)
    }

    @Test
    fun syncWorker_aRunOfFailuresShowsUnreachableAndTheNextSuccessClearsIt() {
        val prefs = serverPrefs()
        repeat(SyncWorker.FAILURES_BEFORE_UNREACHABLE) {
            mockWebServer.enqueue(MockResponse().setResponseCode(502))
            syncOnce(prefs)
        }
        assertTrue(prefs.lastStatus!!, prefs.lastStatus!!.startsWith("Server unreachable"))

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"data": {"inventory": []}, "updated_at": 1}"""))
        assertEquals(ListenableWorker.Result.success(), syncOnce(prefs))
        assertEquals(0, prefs.failures)
        assertTrue(prefs.lastStatus!!, prefs.lastStatus!!.startsWith("OK"))
    }

    @Test
    fun syncWorker_tooManyRequestsStopsWithoutARetryLoop() {
        val prefs = serverPrefs()
        mockWebServer.enqueue(MockResponse().setResponseCode(429).setHeader("Retry-After", "1"))

        assertEquals("a 429 ends this attempt; only the next scheduled run tries again", ListenableWorker.Result.failure(), syncOnce(prefs))
        assertEquals(1, mockWebServer.requestCount)
        assertEquals(1, prefs.failures)

        mockWebServer.enqueue(MockResponse().setResponseCode(403))
        assertEquals(ListenableWorker.Result.failure(), syncOnce(prefs))
        assertEquals(2, mockWebServer.requestCount)
    }

    @Test
    fun syncWorker_aRejectedTokenIsShownImmediately() {
        val prefs = serverPrefs()
        mockWebServer.enqueue(MockResponse().setResponseCode(401))
        assertEquals(ListenableWorker.Result.failure(), syncOnce(prefs))
        assertEquals("Unauthorized (token rejected)", prefs.lastStatus)
    }


    @Test
    fun syncWorker_appliesThePricesTheRuneLitePluginSent() {
        val updatedAtSeconds = 1_800_000_000L
        database.itemManifestDao().insertAll(
            listOf(
                ItemManifestEntity(4151, "Abyssal whip", null, 500L, 1000L),
                ItemManifestEntity(995, "Coins", null, 1L, 1000L)
            )
        )
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"data": {"inventory": [], "prices": {"4151": 1500000, "99999": 7, "not-an-id": 3}}, "updated_at": $updatedAtSeconds}"""
            )
        )

        assertEquals(ListenableWorker.Result.success(), syncOnce(serverPrefs()))

        val whip = database.itemManifestDao().getItemById(4151)
        assertEquals(1_500_000L, whip.gePrice)
        assertEquals("stamped with the snapshot time (ms)", updatedAtSeconds * 1000, whip.priceUpdatedAt)
        assertEquals("items the plugin did not price keep their price", 1L, database.itemManifestDao().getItemById(995).gePrice)
        assertEquals("ids not in the manifest are ignored", null, database.itemManifestDao().getItemById(99999))
    }

}

private class TestSyncUserPreferencesRepository(
    private val uuid: String? = null,
    private val serverUrl: String? = null
) : UserPreferencesRepository {
    override val uuidFlow: Flow<String?> = flowOf(uuid)
    override val rememberedUuidFlow: Flow<String?> = flowOf(uuid)
    override val serverUrlFlow: Flow<String?> = flowOf(serverUrl)
    override val lastSyncStatusFlow: Flow<String?> = flowOf(null)
    override val notificationsEnabledFlow: Flow<Boolean> = flowOf(true)
    override suspend fun getUuid(): String? = uuid
    override suspend fun saveUuid(uuid: String) {}
    override suspend fun clearUuid() {}
    override suspend fun getRememberedUuid(): String? = uuid
    override suspend fun saveRememberedUuid(uuid: String) {}
    override suspend fun clearRememberedUuid() {}
    override suspend fun getServerUrl(): String? = serverUrl
    override suspend fun saveServerUrl(serverUrl: String) {}
    override suspend fun clearServerUrl() {}
    override suspend fun saveSetup(serverUrl: String, uuid: String) {}
    var lastStatus: String? = null
    override suspend fun saveLastSyncStatus(status: String?) { lastStatus = status }
    var failures: Int = 0
    override suspend fun getSyncFailures(): Int = failures
    override suspend fun saveSyncFailures(count: Int) { failures = count }
    override suspend fun getNotificationsEnabled(): Boolean = true
    override suspend fun saveNotificationsEnabled(enabled: Boolean) {}
}
