package com.pssst.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.gson.Gson
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.network.model.StateDataDto
import com.pssst.data.network.model.StateResponse
import com.pssst.receiver.PhoneReceiver
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IconFlowTest {

    private val uuid = "0123456789abcdef0123456789abcdef"
    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var ingest: SnapshotIngest

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        ingest = SnapshotIngest(context, database, TestPreferences(uuid), now = { 1_800_000_000_000L })
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun snapshot(json: String) =
        StateResponse(data = Gson().fromJson(json, StateDataDto::class.java), updatedAt = 1_800_000_000_000L)

    @Test
    fun anIconFromRuneLiteFillsAGapButNeverReplacesABundledOne() = runBlocking {
        database.itemManifestDao().insertAll(
            listOf(
                ItemManifestEntity(5952, "Antidote++(4)", null, 9782L),
                ItemManifestEntity(4151, "Abyssal whip", "BUNDLED", 1_500_000L)
            )
        )

        ingest.ingest(
            uuid,
            snapshot(
                """{"observedAt":1,"icons":{"5952":{"name":"Antidote++(4)","png":"SENT5952"},
                   "4151":{"name":"Abyssal whip","png":"SENT4151"},"99999":{"name":"Brand new item","png":"SENT99999"}}}"""
            )
        )

        val dao = database.itemManifestDao()
        assertEquals("SENT5952", dao.getItemById(5952).iconBase64)
        assertEquals("a bundled icon wins", "BUNDLED", dao.getItemById(4151).iconBase64)
        assertEquals("an item the manifest never had is added", "Brand new item", dao.getItemById(99999).name)
        assertEquals("SENT99999", dao.getItemById(99999).iconBase64)
        assertEquals("the price of a known item is left alone", 9782L, dao.getItemById(5952).gePrice)
    }

    @Test
    fun iconsAreNotKeptInTheSavedSnapshot() = runBlocking {
        ingest.ingest(uuid, snapshot("""{"observedAt":1,"icons":{"5952":{"name":"x","png":"${"A".repeat(2000)}"}}}"""))
        val raw = database.playerStateDao().getStateByUuid(uuid)!!.rawJson
        assertFalse(raw.contains("AAAAAAAA"))
    }

    @Test
    fun anOversizedOrBlankIconIsIgnored() = runBlocking {
        ingest.ingest(uuid, snapshot("""{"observedAt":1,"icons":{"7":{"name":"big","png":"${"A".repeat(20_000)}"},"8":{"name":"blank","png":""}}}"""))
        assertNull(database.itemManifestDao().getItemById(7))
        assertNull(database.itemManifestDao().getItemById(8))
    }

    @Test
    fun theReplyAsksForIconsOfWhatTheSnapshotShowsAndTheAppUses() = runBlocking {
        database.itemManifestDao().insertAll(
            listOf(
                ItemManifestEntity(5952, "Antidote++(4)", null, 9782L),
                ItemManifestEntity(4151, "Abyssal whip", "HAS", 1L)
            )
        )
        val missing = ingest.missingIcons(
            snapshot("""{"bank":[{"id":4151,"quantity":1},{"id":5952,"quantity":1}],"potion_storage":[{"itemId":2446,"doses":4}],
                "ge_offers":[{"slot":1,"itemId":27277,"state":"BUYING"}],"inventory":[{"id":4153,"quantity":1,"baseId":4152}]}""")
        )

        assertTrue(missing.toString(), missing.containsAll(listOf(5952, 2446, 27277, 4152)))
        assertFalse("already has an icon", missing.contains(4151))
        assertTrue("the app's own icons are asked for too", missing.contains(com.pssst.util.IconWishlist.BIRD_HOUSE))
        assertTrue(missing.size <= SnapshotIngest.MAX_ICON_REQUESTS)
    }

    @Test
    fun thePhoneAnswersWithTheMissingIdsAndStopsAskingOnceTheyArrive() {
        val received = ArrayList<StateResponse>()
        val ids = ArrayList<List<Int>>()
        val receiver = PhoneReceiver(port = 0, token = { uuid }, onSnapshot = {
            received += it
            val missing = if (received.size == 1) listOf(5952, 2446) else emptyList()
            ids += missing
            missing
        })
        assertTrue(receiver.bind())
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch { receiver.serve() }
        try {
            fun post(body: String): String {
                val conn = URL("http://127.0.0.1:${receiver.localPort}/sync").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Authorization", "Bearer $uuid")
                conn.outputStream.use { it.write(body.toByteArray()) }
                assertEquals(200, conn.responseCode)
                return conn.inputStream.bufferedReader().readText()
            }
            assertEquals("""{"ok":true,"needIcons":[5952,2446]}""", post("""{"observedAt":1}"""))
            assertEquals("""{"ok":true,"needIcons":[]}""", post("""{"observedAt":2}"""))
        } finally {
            receiver.close()
            scope.cancel()
        }
    }
}
