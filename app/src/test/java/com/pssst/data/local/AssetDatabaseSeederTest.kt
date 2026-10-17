package com.pssst.data.local

import android.content.Context
import androidx.room.Room
import com.google.gson.JsonParser
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AssetDatabaseSeederTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var dao: ItemManifestDao

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.itemManifestDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun loadItems_readsAllItemsFromAssets() {
        val items = AssetDatabaseSeeder.loadItems(context)
        assertTrue(items.isNotEmpty())

        val coins = items.find { it.id == 995 }
        assertNotNull(coins)
        assertEquals("Coins", coins?.name)
        assertNotNull(coins?.iconBase64)

        val whip = items.find { it.id == 4151 }
        assertNotNull(whip)
        assertEquals("Abyssal whip", whip?.name)
        assertNotNull(whip?.iconBase64)
    }

    @Test
    fun seed_insertsItemsIntoDatabase() = runTest {
        assertEquals(0, dao.count())

        AssetDatabaseSeeder.seed(context, database)

        assertTrue(dao.count() > 0)
        val item = dao.getItemById(995)
        assertNotNull(item)
        assertEquals("Coins", item.name)
    }

    @Test
    fun seedIfNeeded_seedsOnlyWhenDatabaseIsEmpty() = runTest {
        assertEquals(0, dao.count())

        val firstRunResult = AssetDatabaseSeeder.seedIfNeeded(context, database)
        assertTrue(firstRunResult)
        val initialCount = dao.count()
        assertTrue(initialCount > 0)

        val secondRunResult = AssetDatabaseSeeder.seedIfNeeded(context, database)
        assertFalse(secondRunResult)
        assertEquals(initialCount, dao.count())
    }

    @Test
    fun itemManifestDao_crudOperationsWork() = runTest {
        val testItem = ItemManifestEntity(1234, "Test Item", "data:image/png;base64,abc")
        dao.insertAll(listOf(testItem))

        assertEquals(1, dao.count())
        val retrieved = dao.getItemById(1234)
        assertNotNull(retrieved)
        assertEquals("Test Item", retrieved.name)
        assertEquals("data:image/png;base64,abc", retrieved.iconBase64)

        dao.clearAll()
        assertEquals(0, dao.count())
    }

    // --- versioned manifest and price-preserving reseed ---------------------------------------------------

    private fun fixture(name: String) = AssetDatabaseSeeder.parseManifest(
        JsonParser.parseString(javaClass.classLoader!!.getResourceAsStream(name)!!.reader().readText())
    )

    @Test
    fun parseManifest_readsTheVersionedFormatAndStampsBakedPrices() {
        val m = fixture("manifest-v1.json")
        assertEquals(1, m.version)
        assertEquals(1000L, m.generatedAt)
        val whip = m.items.first { it.id == 4151 }
        assertEquals(500L, whip.gePrice)
        assertEquals(1000L, whip.priceUpdatedAt)
        // No price means no timestamp: it can never overwrite a refreshed price
        assertEquals(0L, m.items.first { it.id == 6000 }.priceUpdatedAt)
    }

    @Test
    fun parseManifest_stillReadsTheOriginalBareArray() {
        val m = AssetDatabaseSeeder.parseManifest(
            JsonParser.parseString("""[{"id":995,"name":"Coins","gePrice":1}]""")
        )
        assertEquals(0, m.version)
        assertEquals("Coins", m.items.single().name)
        assertEquals(0L, m.items.single().priceUpdatedAt)
    }

    @Test
    fun seedIfNeeded_reappliesOnlyWhenTheBundleIsNewer() = runTest {
        assertTrue(AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v1.json")))
        assertFalse("same version, table already populated", AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v1.json")))
        assertEquals("Untradeable thing", dao.getItemById(6000).name)

        assertTrue(AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v2.json")))
        assertEquals("Renamed thing", dao.getItemById(6000).name)
        assertEquals("a brand new id arrives", "New item", dao.getItemById(6001).name)
    }

    @Test
    fun reseed_neverReplacesAFresherRefreshedPriceButTakesNewerBakedOnes() = runTest {
        AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v1.json")) // whip 500 @1000
        dao.applyPrices(mapOf(4151 to 900L), 2000L)                                     // refreshed @2000

        // v2 bundle is generated at 3000 with 700: newer than the refresh, so it wins
        AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v2.json"))
        assertEquals(700L, dao.getItemById(4151).gePrice)
        assertEquals(3000L, dao.getItemById(4151).priceUpdatedAt)

        // A refresh at 5000 and then a reseed of the (older, 3000) bundle must keep the refresh
        dao.applyPrices(mapOf(4151 to 950L), 5000L)
        dao.mergeBundled(fixture("manifest-v2.json").items)
        assertEquals(950L, dao.getItemById(4151).gePrice)
        assertEquals(5000L, dao.getItemById(4151).priceUpdatedAt)
    }

    @Test
    fun reseed_keepsAnIconTheNewBundleOmits() = runTest {
        AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v1.json")) // whip has an icon
        AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v2.json")) // v2 whip has none
        assertEquals("aWNvbg==", dao.getItemById(4151).iconBase64)
    }

    @Test
    fun applyPrices_updatesKnownItemsAndIgnoresUnknownOnes() = runTest {
        AssetDatabaseSeeder.seedIfNeeded(context, database, fixture("manifest-v1.json"))
        dao.applyPrices(mapOf(4151 to 123L, 99999 to 5L), 4000L)
        assertEquals(123L, dao.getItemById(4151).gePrice)
        assertEquals(4000L, dao.latestPriceTimestamp())
        assertEquals(null, dao.getItemById(99999))
    }
}
