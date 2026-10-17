package com.pssst.data.local

import android.content.Context
import androidx.room.Room
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
    fun seedIfEmpty_seedsOnlyWhenDatabaseIsEmpty() = runTest {
        assertEquals(0, dao.count())

        val firstRunResult = AssetDatabaseSeeder.seedIfEmpty(context, database)
        assertTrue(firstRunResult)
        val initialCount = dao.count()
        assertTrue(initialCount > 0)

        val secondRunResult = AssetDatabaseSeeder.seedIfEmpty(context, database)
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
}
