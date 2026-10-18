package com.pssst.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.gson.Gson
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.network.model.StateResponse
import com.pssst.worker.SyncWorker
import kotlinx.coroutines.runBlocking
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
class SnapshotIngestTest {

    private val uuid = "0123456789abcdef0123456789abcdef"
    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var prefs: TestPreferences
    private lateinit var ingest: SnapshotIngest

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        prefs = TestPreferences(uuid)
        ingest = SnapshotIngest(context, database, prefs, now = { 1_800_000_000_000L })
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun snapshot(json: String, updatedAt: Long = 1_800_000_000_000L) =
        StateResponse(data = Gson().fromJson(json, com.pssst.data.network.model.StateDataDto::class.java), updatedAt = updatedAt)

    @Test
    fun aSnapshotIsSavedWithItsSections() = runBlocking {
        val outcome = ingest.ingest(uuid, snapshot("""{"observedAt":1000,"inventory":[{"id":4151,"quantity":1}],"bank":[{"id":560,"quantity":2500}],"varbits":{"4771":8}}"""))

        assertEquals(SnapshotIngest.Outcome.SAVED, outcome)
        val saved = database.playerStateDao().getStateByUuid(uuid)!!
        assertTrue(saved.rawJson.contains("4151"))
        assertTrue(saved.bankJson.contains("560"))
        assertTrue(saved.varbitsJson.contains(""""4771":8"""))
        assertEquals(1_800_000_000_000L, saved.lastSyncedAt)
        assertTrue(prefs.lastStatus!!, prefs.lastStatus!!.startsWith("OK"))
    }

    @Test
    fun secondsFromAServerAreTurnedIntoMilliseconds() = runBlocking {
        ingest.ingest(uuid, snapshot("""{"inventory":[]}""", updatedAt = 1_729_100_000L))
        assertEquals(1_729_100_000_000L, database.playerStateDao().getStateByUuid(uuid)!!.updatedAt)
    }

    @Test
    fun neverReplacesSavedDataWithAnOlderObservation() = runBlocking {
        database.playerStateDao().upsert(
            PlayerStateEntity(uuid, 5_000L, """{"data":{"observedAt":2000},"updated_at":5}""", "{}", "{}", "[]", "[]", 0L)
        )

        val outcome = ingest.ingest(uuid, snapshot("""{"observedAt":1000,"inventory":[]}"""))

        assertEquals(SnapshotIngest.Outcome.OLDER, outcome)
        assertEquals(SyncWorker.STATUS_OLDER, prefs.lastStatus)
        assertTrue(database.playerStateDao().getStateByUuid(uuid)!!.rawJson.contains(""""observedAt":2000"""))
    }

    @Test
    fun newerOrUnstampedDataReplacesStampedData() = runBlocking {
        database.playerStateDao().upsert(
            PlayerStateEntity(uuid, 5_000L, """{"data":{"observedAt":2000},"updated_at":5}""", "{}", "{}", "[]", "[]", 0L)
        )
        assertEquals(SnapshotIngest.Outcome.SAVED, ingest.ingest(uuid, snapshot("""{"observedAt":3000}""")))
        assertEquals(SnapshotIngest.Outcome.SAVED, ingest.ingest(uuid, snapshot("""{"inventory":[]}""")))
    }

    @Test
    fun pricesAreStampedWithWhenTheGameSawThem() = runBlocking {
        database.itemManifestDao().insertAll(listOf(ItemManifestEntity(4151, "Abyssal whip", null, 500L, 1000L)))

        ingest.ingest(uuid, snapshot("""{"observedAt":1750000000000,"prices":{"4151":1500000,"not-an-id":3}}"""))

        val whip = database.itemManifestDao().getItemById(4151)
        assertEquals(1_500_000L, whip.gePrice)
        assertEquals(1_750_000_000_000L, whip.priceUpdatedAt)
    }

    @Test
    fun bankSharingOffStoresNoBank() = runBlocking {
        ingest.ingest(uuid, snapshot("""{"observedAt":1000,"inventory":[]}"""))
        assertEquals("[]", database.playerStateDao().getStateByUuid(uuid)!!.bankJson)
    }
}
