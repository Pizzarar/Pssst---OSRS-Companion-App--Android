package com.pssst.worker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.WikiPricesClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PriceRefreshWorkerTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var server: MockWebServer
    private val prefs = PricePrefs()

    private val latest = """{"data": {"4151": {"high": 2000000, "highTime": 1, "low": 1000000, "lowTime": 1}}}"""

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        database.itemManifestDao().insertAll(listOf(ItemManifestEntity(4151, "Abyssal whip", null, 500L, 1000L)))
        server = MockWebServer().also { it.start() }

        PriceRefreshWorker.defaultDatabase = database
        PriceRefreshWorker.defaultPreferences = prefs
        PriceRefreshWorker.defaultClient = WikiPricesClient(OkHttpClient(), "Pssst/test", server.url("/latest").toString())
    }

    @After
    fun tearDown() {
        database.close()
        server.shutdown()
        PriceRefreshWorker.defaultDatabase = null
        PriceRefreshWorker.defaultPreferences = null
        PriceRefreshWorker.defaultClient = null
    }

    private fun run(): ListenableWorker.Result = runBlocking {
        TestListenableWorkerBuilder<PriceRefreshWorker>(context).build().doWork()
    }

    @Test
    fun doesNothingUntilTheUserHasOptedIn() {
        prefs.choice = null
        assertEquals(ListenableWorker.Result.success(), run())
        prefs.choice = false
        assertEquals(ListenableWorker.Result.success(), run())

        assertEquals("not a single request without consent", 0, server.requestCount)
        assertEquals(500L, database.itemManifestDao().getItemById(4151).gePrice)
    }

    @Test
    fun optedInStoresTheFetchedPriceWithItsTimestamp() {
        prefs.choice = true
        server.enqueue(MockResponse().setBody(latest))

        assertEquals(ListenableWorker.Result.success(), run())

        val whip = database.itemManifestDao().getItemById(4151)
        assertEquals(1_500_000L, whip.gePrice)
        assertTrue("stamped with the fetch time", whip.priceUpdatedAt > 1000L)
    }

    @Test
    fun skipsTheRequestWhenPricesWereJustRefreshed() {
        prefs.choice = true
        database.itemManifestDao().applyPrices(mapOf(4151 to 700L), System.currentTimeMillis())

        assertEquals(ListenableWorker.Result.success(), run())

        assertEquals(0, server.requestCount)
        assertEquals(700L, database.itemManifestDao().getItemById(4151).gePrice)
    }

    @Test
    fun retriesOnRateLimitAndServerErrorsButNotOnOtherClientErrors() {
        prefs.choice = true

        server.enqueue(MockResponse().setResponseCode(429))
        assertEquals(ListenableWorker.Result.retry(), run())
        server.enqueue(MockResponse().setResponseCode(503))
        assertEquals(ListenableWorker.Result.retry(), run())
        server.enqueue(MockResponse().setResponseCode(403))
        assertEquals(ListenableWorker.Result.failure(), run())
    }

    @Test
    fun anEmptyAnswerIsNotTreatedAsSuccess() {
        prefs.choice = true
        server.enqueue(MockResponse().setBody("""{"data": {}}"""))
        assertEquals(ListenableWorker.Result.retry(), run())
        assertEquals(500L, database.itemManifestDao().getItemById(4151).gePrice)
    }
}

private class PricePrefs : UserPreferencesRepository {
    var choice: Boolean? = null
    override suspend fun getPriceRefreshEnabled(): Boolean? = choice

    override val uuidFlow: Flow<String?> = MutableStateFlow(null)
    override val rememberedUuidFlow: Flow<String?> = MutableStateFlow(null)
    override val serverUrlFlow: Flow<String?> = MutableStateFlow(null)
    override val lastSyncStatusFlow: Flow<String?> = MutableStateFlow(null)
    override val notificationsEnabledFlow: Flow<Boolean> = MutableStateFlow(true)
    override suspend fun getUuid(): String? = null
    override suspend fun saveUuid(uuid: String) {}
    override suspend fun clearUuid() {}
    override suspend fun getRememberedUuid(): String? = null
    override suspend fun saveRememberedUuid(uuid: String) {}
    override suspend fun clearRememberedUuid() {}
    override suspend fun getServerUrl(): String? = null
    override suspend fun saveServerUrl(serverUrl: String) {}
    override suspend fun clearServerUrl() {}
    override suspend fun saveSetup(serverUrl: String, uuid: String) {}
    override suspend fun saveLastSyncStatus(status: String?) {}
    override suspend fun getNotificationsEnabled(): Boolean = true
    override suspend fun saveNotificationsEnabled(enabled: Boolean) {}
}
