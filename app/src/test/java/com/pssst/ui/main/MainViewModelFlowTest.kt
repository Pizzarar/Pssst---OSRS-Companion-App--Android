package com.pssst.ui.main

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.local.PlayerStateDao
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.local.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainViewModelFlowTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var dao: PlayerStateDao
    private lateinit var fakePrefs: FakeUserPreferencesRepository
    private lateinit var viewModel: MainViewModel
    private val testUuid = "test-uuid-abcdef-1234"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.playerStateDao()
        fakePrefs = FakeUserPreferencesRepository()
        viewModel = MainViewModel(dao, fakePrefs, database.itemManifestDao(), sharingStarted = SharingStarted.Eagerly)
    }

    @After
    fun tearDown() {
        // Stop the view model's Room flows first. Closing the database while a flow is still opening it deadlocks
        // (Room's close lock vs. its invalidation tracker), which hung the suite now and then.
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initiallyUnconfiguredWhenUuidEmpty() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse("Should not have configured UUID initially", state.hasConfiguredUuid)
        assertEquals("", state.uuid)
        assertTrue(state.bankItems.isEmpty())
        assertEquals(28, state.inventorySlots.size)
        assertTrue(state.inventorySlots.all { it.item == null })
    }

    // Two regions share the same varbit slot (4774); Catherby's ranarr is mid-growth, Falador's guam is fully grown
    private val farmingObservedAt = 1_000_020_000L // 7 minutes into a 20-minute tick
    private val farmingRawJson = """
        {"data": {"farming": [
          {"regionId": 11062, "regionName": "Catherby", "patchName": "", "patchType": "HERB", "varbitId": 4774,
           "value": 33, "observedAt": $farmingObservedAt, "cropName": "Ranarr", "itemId": 207,
           "cropState": "GROWING", "stage": 1, "stages": 5, "tickMinutes": 20},
          {"regionId": 12083, "regionName": "Falador", "patchName": "", "patchType": "HERB", "varbitId": 4774,
           "value": 10, "observedAt": $farmingObservedAt, "cropName": "Guam", "itemId": 199,
           "cropState": "HARVESTABLE", "stage": 0, "stages": 3, "tickMinutes": 0},
          {"regionId": 14651, "regionName": "Fossil Island", "patchName": "East", "patchType": "HARDWOOD_TREE", "varbitId": 4771,
           "value": 200, "observedAt": $farmingObservedAt}
        ]}, "updated_at": 1729100000000}
    """.trimIndent()

    @Test
    fun uiState_updatesAutomaticallyWhenMockDataInsertedIntoRoom() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        // 1. Save UUID
        fakePrefs.emitUuid(testUuid)
        val stateAfterUuid = viewModel.uiState.first { it.hasConfiguredUuid }
        assertEquals(testUuid, stateAfterUuid.uuid)
        assertTrue(stateAfterUuid.hasConfiguredUuid)

        // 2. Insert mock data directly into Room database
        val bankJson = """
            [
              {"id": 4151, "quantity": 2},
              {"id": 995, "quantity": 15000000},
              {"id": 560, "quantity": 25000}
            ]
        """.trimIndent()

        val inventoryJson = """
            [
              {"id": 1127, "quantity": 1},
              {"id": 1079, "quantity": 1},
              {"id": 995, "quantity": 50000}
            ]
        """.trimIndent()

        val varbitsJson = """
            {
              "72": 121,
              "74": 2500000
            }
        """.trimIndent()

        val slayerJson = """
            {
              "taskName": "Bloodvelds",
              "amount": 80
            }
        """.trimIndent()

        val mockEntity = PlayerStateEntity(
            testUuid,
            1729100000L,
            farmingRawJson,
            varbitsJson,
            slayerJson,
            inventoryJson,
            bankJson,
            System.currentTimeMillis()
        )

        dao.upsert(mockEntity)

        // 3. Observe the reactive UI state emitted from the Room Flow
        val state = viewModel.uiState.first { it.bankItems.isNotEmpty() }

        // Bank checks
        assertEquals(3, state.bankItems.size)
        val whip = state.bankItems.first { it.id == 4151 }
        assertEquals(2, whip.quantity)
        assertEquals("2", whip.formattedQuantity)

        val coins = state.bankItems.first { it.id == 995 }
        assertEquals(15000000, coins.quantity)
        assertEquals("15M", coins.formattedQuantity)

        val deathRunes = state.bankItems.first { it.id == 560 }
        assertEquals(25000, deathRunes.quantity)
        assertEquals("25K", deathRunes.formattedQuantity)

        // Inventory checks (28 slots)
        assertEquals(28, state.inventorySlots.size)
        val slot0 = state.inventorySlots[0]
        assertNotNull(slot0.item)
        assertEquals(1127, slot0.item?.id)
        assertEquals(1, slot0.item?.quantity)

        val slot1 = state.inventorySlots[1]
        assertNotNull(slot1.item)
        assertEquals(1079, slot1.item?.id)

        val slot2 = state.inventorySlots[2]
        assertNotNull(slot2.item)
        assertEquals(995, slot2.item?.id)
        assertEquals("50K", slot2.item?.formattedQuantity)

        // Remaining slots 3..27 should be empty
        for (i in 3 until 28) {
            assertNull("Slot $i should be empty", state.inventorySlots[i].item)
        }

        // Miscellania checks
        assertEquals(95, state.miscellania.approvalPercent)
        assertEquals(2500000L, state.miscellania.cofferAmount)
        assertEquals("2.5M GP", state.miscellania.formattedCoffer)

        // Slayer checks
        assertEquals("Bloodvelds", state.slayer.taskName)
        assertEquals(80, state.slayer.amount)

        // Farming: the same varbit slot (4774) is a different patch in each region
        assertEquals(3, state.farmingPatches.size)
        val catherbyHerb = state.farmingPatches.first { it.patchId == "11062.4774" }
        assertEquals("Catherby Herb", catherbyHerb.patchLabel)
        assertEquals("Ranarr", catherbyHerb.cropName)
        // Done 3 ticks after the start of the tick it was seen in (stage 1 of 5, 20 min ticks)
        val tickMs = 20 * 60_000L
        assertEquals((farmingObservedAt / tickMs) * tickMs + 3 * tickMs, catherbyHerb.doneAtMs)

        val tenMinutesLater = farmingObservedAt + 10 * 60_000L
        assertEquals(FarmingStatus.GROWING, catherbyHerb.status(tenMinutesLater))
        assertFalse(catherbyHerb.isGrown(tenMinutesLater))
        assertEquals("Ranarr • Stage 2/4", catherbyHerb.statusText(tenMinutesLater))
        assertEquals(0.25f, catherbyHerb.progress(tenMinutesLater))
        assertTrue(catherbyHerb.isGrown(catherbyHerb.doneAtMs!!))

        val faladorHerb = state.farmingPatches.first { it.patchId == "12083.4774" }
        assertEquals("Falador Herb", faladorHerb.patchLabel)
        assertTrue(faladorHerb.isGrown(tenMinutesLater))
        assertEquals("Guam • Fully grown", faladorHerb.statusText(tenMinutesLater))

        // A value the plugin could not decode stays visible but is not guessed at
        val fossil = state.farmingPatches.first { it.patchId == "14651.4771" }
        assertEquals("Fossil Island Hardwood tree (East)", fossil.patchLabel)
        assertEquals(FarmingStatus.UNKNOWN, fossil.status(tenMinutesLater))

        // Search Query checks
        viewModel.onSearchQueryChanged("4151")
        val searchedState = viewModel.uiState.first { it.searchQuery == "4151" }
        assertEquals("4151", searchedState.searchQuery)
        assertEquals(1, searchedState.filteredBankItems.size)
        assertEquals(4151, searchedState.filteredBankItems[0].id)
    }

    @Test
    fun uiState_clearingUuidResetsConfiguration() = runTest(testDispatcher) {
        fakePrefs.emitUuid(testUuid)
        val configuredState = viewModel.uiState.first { it.hasConfiguredUuid }
        assertTrue(configuredState.hasConfiguredUuid)

        fakePrefs.emitUuid(null)
        val resetState = viewModel.uiState.first { !it.hasConfiguredUuid }
        assertFalse(resetState.hasConfiguredUuid)
        assertEquals("", resetState.uuid)
    }

    @Test
    fun uiState_bankValuationAndSortingWorkOffline() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        fakePrefs.emitUuid(testUuid)

        // Seed offline item manifest with prices
        val manifestDao = database.itemManifestDao()
        manifestDao.insertAll(listOf(
            ItemManifestEntity(4151, "Abyssal whip", null, 1_500_000L),
            ItemManifestEntity(995, "Coins", null, 1L),
            ItemManifestEntity(560, "Death rune", null, 200L)
        ))

        val bankJson = """
            [
              {"id": 4151, "quantity": 2},
              {"id": 995, "quantity": 15000000},
              {"id": 560, "quantity": 25000}
            ]
        """.trimIndent()

        val mockEntity = PlayerStateEntity(
            testUuid,
            1729100000L,
            "{}",
            "{}",
            "{}",
            "[]",
            bankJson,
            System.currentTimeMillis()
        )
        dao.upsert(mockEntity)

        val state = viewModel.uiState.first { it.bankItems.isNotEmpty() && it.bankItems[0].gePrice > 0 }

        // Total bank value: (2 * 1.5M) + (15M * 1) + (25k * 200) = 3M + 15M + 5M = 23,000,000
        assertEquals(23_000_000L, state.totalBankValue)
        assertEquals("23.0M GP", state.formattedBankValue)

        // Default sort (original order: 4151, 995, 560)
        assertEquals(listOf(4151, 995, 560), state.filteredBankItems.map { it.id })

        // Sort by Value (High to Low): Coins (15M), Death rune (5M), Whip (3M)
        viewModel.onBankSortModeChanged(BankSortMode.VALUE)
        val valueSortedState = viewModel.uiState.first { it.bankSortMode == BankSortMode.VALUE }
        assertEquals(listOf(995, 560, 4151), valueSortedState.filteredBankItems.map { it.id })

        // Sort by Quantity: Coins (15,000,000), Death rune (25,000), Whip (2)
        viewModel.onBankSortModeChanged(BankSortMode.QUANTITY)
        val qtySortedState = viewModel.uiState.first { it.bankSortMode == BankSortMode.QUANTITY }
        assertEquals(listOf(995, 560, 4151), qtySortedState.filteredBankItems.map { it.id })
    }

    @Test
    fun uiState_birdHousesComeFromPluginObservationsAndReadinessFollowsTheClock() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        fakePrefs.emitUuid(testUuid)

        // 27 = seeded redwood (a multiple of 3 is seeded), 26 = redwood built but not seeded, 4 = oak built
        val seededAt = 2_000_000_000_000L
        val readyAt = seededAt + 50 * 60_000L
        val rawJson = """
            {"data": {"birdhouses": [
              {"space": "MEADOW_NORTH", "spaceName": "Mushroom Meadow (North)", "varpId": 1626, "value": 27,
               "observedAt": $seededAt, "state": "SEEDED", "houseType": "REDWOOD", "houseItemId": 22204, "readyAt": $readyAt},
              {"space": "MEADOW_SOUTH", "spaceName": "Mushroom Meadow (South)", "varpId": 1627, "value": 26,
               "observedAt": $seededAt, "state": "BUILT", "houseType": "REDWOOD", "houseItemId": 22204, "readyAt": 0},
              {"space": "VALLEY_NORTH", "spaceName": "Verdant Valley (Northeast)", "varpId": 1628, "value": 0,
               "observedAt": $seededAt, "state": "EMPTY", "readyAt": 0},
              {"space": "VALLEY_SOUTH", "spaceName": "Verdant Valley (Southwest)", "varpId": 1629, "value": 4,
               "observedAt": $seededAt, "state": "BUILT", "houseType": "OAK", "houseItemId": 21515, "readyAt": 0}
            ]}}
        """.trimIndent()

        dao.upsert(
            PlayerStateEntity(testUuid, 1729100000L, rawJson, "{}", "{}", "[]", "[]", System.currentTimeMillis())
        )

        val state = viewModel.uiState.first { it.birdHouses.isNotEmpty() }
        assertEquals(4, state.birdHouses.size)

        val tenMinutesIn = seededAt + 10 * 60_000L
        val meadowNorth = state.birdHouses.first { it.varpId == "1626" }
        assertEquals("Mushroom Meadow (North)", meadowNorth.spaceName)
        assertEquals("Redwood birdhouse", meadowNorth.houseType)
        assertEquals(BirdHouseStatus.SEEDED, meadowNorth.status(tenMinutesIn))
        assertFalse(meadowNorth.isReady(tenMinutesIn))
        assertEquals("Seeded \u2022 ready in ~40m", meadowNorth.statusText(tenMinutesIn))
        // Ready is time based: 50 minutes after the seeded value was first observed, not a varp value
        assertTrue(meadowNorth.isReady(readyAt))
        assertEquals("Seeded \u2022 ready", meadowNorth.statusText(readyAt))

        val meadowSouth = state.birdHouses.first { it.varpId == "1627" }
        assertEquals(BirdHouseStatus.BUILT, meadowSouth.status(readyAt + 24 * 60 * 60_000L))
        assertFalse(meadowSouth.isReady(readyAt + 24 * 60 * 60_000L))
        assertEquals("Built, not seeded", meadowSouth.statusText(tenMinutesIn))

        val valleyNE = state.birdHouses.first { it.varpId == "1628" }
        assertNull(valleyNE.houseType)
        assertEquals(BirdHouseStatus.EMPTY, valleyNE.status(tenMinutesIn))

        assertEquals("Oak birdhouse", state.birdHouses.first { it.varpId == "1629" }.houseType)

        // The varps no longer feed birdhouses, and legacy fixed-patch varbits are not interpreted either
        assertTrue(state.farmingPatches.isEmpty())
    }

    @Test
    fun uiState_realClientVarbitsAndDailiesDecodedCorrectly() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        fakePrefs.emitUuid(testUuid)

        // Seed Battlestaff (id 1391) price for profit calculation
        database.itemManifestDao().insertAll(listOf(
            ItemManifestEntity(1391, "Battlestaff", null, 8_200L)
        ))

        // Real payload as sent from live RuneLite state.json:
        // Miscellania: 72 = 32 (32/127 = 25%), 74 = 2554 (2.6K GP)
        // Slayer: taskLocation = "Karuulm Slayer Dungeon"
        // Dailies: 4479 = 1 (Varrock easy: the gate RuneLite checks), 4539 = 0 (Zaff not claimed yet)
        // NMZ: 3961 = 5 (5/15 claimed), 1060 = 50000 points
        // Tears of Guthix: 5099 = 0, 456 = 0 (Ready)
        val varbitsJson = """
            {
              "72": 32,
              "74": 2554,
              "4539": 0,
              "4479": 1,
              "4481": 1,
              "3961": 5,
              "5099": 0,
              "456": 0
            }
        """.trimIndent()

        val slayerJson = """
            {
              "taskName": "Hydras",
              "amount": 88,
              "taskLocation": "Karuulm Slayer Dungeon",
              "initialAmount": 130,
              "points": 1234,
              "streak": 45
            }
        """.trimIndent()

        // NMZ reward points are a varp (1060), so they travel in varps{}, not varbits{}
        val rawJson = """{"data": {"varps": {"1060": 50000}}}"""

        val mockEntity = PlayerStateEntity(
            testUuid,
            1729100000L,
            rawJson,
            varbitsJson,
            slayerJson,
            "[]",
            "[]",
            System.currentTimeMillis()
        )
        dao.upsert(mockEntity)

        val state = viewModel.uiState.first { it.dailyTasks.isNotEmpty() }

        // Miscellania real varbits
        assertEquals(25, state.miscellania.approvalPercent)
        assertEquals(2554L, state.miscellania.cofferAmount)
        assertEquals("2.6K GP", state.miscellania.formattedCoffer)

        // Slayer location
        assertEquals("Hydras", state.slayer.taskName)
        assertEquals(88, state.slayer.amount)
        assertEquals("Karuulm Slayer Dungeon", state.slayer.taskLocation)
        assertEquals(130, state.slayer.initialAmount)
        assertEquals(1234, state.slayer.points)
        assertEquals(45, state.slayer.streak)
        assertTrue(state.slayer.hasTask)

        // Birdhouses are not read from varps any more
        assertTrue(state.birdHouses.isEmpty())

        assertTrue(state.farmingPatches.isEmpty())

        // Dailies: Zaff staves
        val zaff = state.dailyTasks.first { it.id == "zaff_staves" }
        assertTrue(zaff.isAvailable)
        // Availability only: no profit estimate, nothing that reads as advice
        assertEquals("Available", zaff.statusText)
        assertEquals("the requirement is shown, not a reward amount", "Varrock Diary (Easy)", zaff.subtitle)

        // Dailies: NMZ herb boxes
        val nmz = state.dailyTasks.first { it.id == "nmz_herb_boxes" }
        assertTrue(nmz.isAvailable)
        // Status only: what is taken and what the player holds, never what to buy
        assertEquals("5/15 claimed today \u2022 50K points", nmz.statusText)

        // Weeklies: Tears of Guthix
        val tog = state.dailyTasks.first { it.id == "tears_of_guthix" }
        assertEquals(ResetCategory.WEEKLY, tog.category)
        assertEquals("Countdown: 0 \u2022 Quest points check: 0", tog.statusText)
        assertFalse("no rule for these values, so never presented as available", tog.isAvailable)
    }

    @Test
    fun testNotificationsEnabledToggle() = runTest {
        val testUuid = "test-uuid-alerts"
        fakePrefs.saveSetup("http://10.0.2.2:8787", testUuid)

        val initialState = viewModel.uiState.first { it.notificationsEnabled }
        assertTrue(initialState.notificationsEnabled)

        viewModel.setNotificationsEnabled(false)

        val disabledState = viewModel.uiState.first { !it.notificationsEnabled }
        assertFalse(disabledState.notificationsEnabled)

        viewModel.setNotificationsEnabled(true)

        val reenabledState = viewModel.uiState.first { it.notificationsEnabled }
        assertTrue(reenabledState.notificationsEnabled)
    }

    @Test
    fun uiState_parsesNewFeaturesCorrectly() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        fakePrefs.emitUuid(testUuid)

        database.itemManifestDao().insertAll(listOf(
            ItemManifestEntity(4151, "Abyssal whip", null, 1_500_000L),
            ItemManifestEntity(1391, "Battlestaff", null, 8_200L),
            ItemManifestEntity(2434, "Prayer potion(4)", null, 10_000L),
            ItemManifestEntity(5968, "Basket of tomatoes", null, 500L),
            ItemManifestEntity(5386, "Basket of apples", null, 600L)
        ))

        val bankJson = """
            [
              {"id": 5968, "quantity": 10},
              {"id": 4151, "quantity": 1}
            ]
        """.trimIndent()

        val geOffersJson = """
            [
              {
                "slot": 1,
                "itemId": 4151,
                "quantitySold": 1,
                "totalQuantity": 1,
                "price": 1500000,
                "spent": 1500000,
                "state": "BOUGHT"
              },
              {
                "slot": 2,
                "itemId": 1391,
                "quantitySold": 50,
                "totalQuantity": 100,
                "price": 8000,
                "spent": 400000,
                "state": "BUYING"
              }
            ]
        """.trimIndent()

        val skillsJson = """
            [
              {"name": "Attack", "level": 99, "boostedLevel": 99, "xp": 13034431},
              {"name": "Farming", "level": 70, "boostedLevel": 70, "xp": 737627}
            ]
        """.trimIndent()

        val potionJson = """
            [
              {"itemId": 2434, "doses": 120}
            ]
        """.trimIndent()

        val questsJson = """
            {
              "Cook's Assistant": "COMPLETED",
              "Dragon Slayer I": "IN_PROGRESS",
              "Desert Treasure II": "NOT_STARTED"
            }
        """.trimIndent()

        val mockEntity = PlayerStateEntity(
            testUuid,
            1729100000L,
            "{}",
            "{}",
            "{}",
            "[]",
            bankJson,
            System.currentTimeMillis(),
            geOffersJson,
            skillsJson,
            potionJson,
            questsJson
        )
        dao.upsert(mockEntity)

        val state = viewModel.uiState.first { it.skills.isNotEmpty() && it.skills.first().xp > 0 }

        // 1. GE Offers
        assertEquals(8, state.geOffers.size)
        val slot1 = state.geOffers[0]
        assertEquals(1, slot1.slot)
        assertEquals(4151, slot1.itemId)
        assertEquals("Abyssal whip", slot1.itemName)
        assertEquals(GeOfferState.BOUGHT, slot1.state)
        assertTrue(slot1.isComplete)
        assertEquals(1.0f, slot1.progress)

        val slot2 = state.geOffers[1]
        assertEquals(2, slot2.slot)
        assertEquals("Battlestaff", slot2.itemName)
        assertEquals(GeOfferState.BUYING, slot2.state)
        assertEquals(0.5f, slot2.progress)

        // Empty slots
        for (i in 2..7) {
            assertTrue(state.geOffers[i].isEmpty)
        }

        // 2. Skills
        val attack = state.skills.first { it.name == "Attack" }
        assertEquals(99, attack.level)
        assertEquals(13034431L, attack.xp)

        val farming = state.skills.first { it.name == "Farming" }
        assertEquals(70, farming.level)
        assertEquals(737627L, farming.xp)
        assertTrue(farming.progress >= 0f)

        // 3. Potion Storage
        assertEquals(1, state.potionStorageItems.size)
        val pPot = state.potionStorageItems[0]
        assertEquals(2434, pPot.itemId)
        assertEquals("Prayer potion(4)", pPot.name)
        assertEquals(120, pPot.totalDoses)
        assertEquals(30, pPot.fourDoseEquivalent)
        assertEquals(0, pPot.remainingDoses)
        assertEquals(300_000L, pPot.estimatedValue)

        // 4. Farming Payments
        val oakPayment = state.farmingPayments.first { it.cropName == "Oak Tree" }
        assertEquals(5968, oakPayment.paymentItemId)
        assertEquals("Basket of tomatoes", oakPayment.paymentItemName)
        assertEquals(1, oakPayment.requiredQuantity)
        assertEquals(10, oakPayment.ownedInBankAndInv)
        assertTrue(oakPayment.hasEnough)

        val willowPayment = state.farmingPayments.first { it.cropName == "Willow Tree" }
        assertEquals(5386, willowPayment.paymentItemId)
        assertEquals(0, willowPayment.ownedInBankAndInv)
        assertFalse(willowPayment.hasEnough)

        // 5. Quests
        assertEquals(3, state.quests.size)
        val cooks = state.quests.first { it.name == "Cook's Assistant" }
        assertEquals(QuestState.COMPLETED, cooks.state)
        val ds1 = state.quests.first { it.name == "Dragon Slayer I" }
        assertEquals(QuestState.IN_PROGRESS, ds1.state)
        val dt2 = state.quests.first { it.name == "Desert Treasure II" }
        assertEquals(QuestState.NOT_STARTED, dt2.state)

        // Quest filtering
        viewModel.onQuestFilterChanged(QuestFilter.COMPLETED)
        val completedQuests = viewModel.uiState.first { it.questFilter == QuestFilter.COMPLETED }
        assertEquals(1, completedQuests.filteredQuests.size)
        assertEquals("Cook's Assistant", completedQuests.filteredQuests[0].name)
    }

    @Test
    fun uiState_notedInventoryItemsTakeTheNameAndPriceOfTheirUnnotedItem() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        fakePrefs.emitUuid(testUuid)
        database.itemManifestDao().insertAll(listOf(ItemManifestEntity(4151, "Abyssal whip", null, 1_500_000L)))

        // 4152 is the noted whip: it has no manifest row of its own, the plugin says it stands for 4151
        val inventoryJson = """[{"id": 4152, "quantity": 3, "baseId": 4151}, {"id": 4151, "quantity": 1}, {"id": 31337, "quantity": 2}]"""
        dao.upsert(
            PlayerStateEntity(testUuid, 1729100000L, "{}", "{}", "{}", inventoryJson, "[]", System.currentTimeMillis())
        )

        val state = viewModel.uiState.first { it.inventorySlots.any { slot -> slot.item != null } }
        val noted = state.inventorySlots[0].item!!
        assertEquals(4152, noted.id)
        assertEquals("Abyssal whip (noted)", noted.name)
        assertEquals(4_500_000L, noted.stackValue)

        assertEquals("Abyssal whip", state.inventorySlots[1].item!!.name)

        // Unknown ids stay recognisably unknown instead of pretending to be a name
        val unknown = state.inventorySlots[2].item!!
        assertEquals("", unknown.name)
        assertEquals(0L, unknown.stackValue)
    }
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val uuidStateFlow = MutableStateFlow<String?>(null)
    private val rememberedUuidStateFlow = MutableStateFlow<String?>(null)
    private val serverUrlStateFlow = MutableStateFlow<String?>(null)
    private val lastSyncStatusStateFlow = MutableStateFlow<String?>(null)

    override val uuidFlow: Flow<String?> = uuidStateFlow
    override val rememberedUuidFlow: Flow<String?> = rememberedUuidStateFlow
    override val serverUrlFlow: Flow<String?> = serverUrlStateFlow
    override val lastSyncStatusFlow: Flow<String?> = lastSyncStatusStateFlow

    fun emitUuid(uuid: String?) {
        uuidStateFlow.value = uuid
    }

    fun emitRememberedUuid(uuid: String?) {
        rememberedUuidStateFlow.value = uuid
    }

    override suspend fun getUuid(): String? = uuidStateFlow.value

    override suspend fun saveUuid(uuid: String) {
        uuidStateFlow.value = uuid
    }

    override suspend fun clearUuid() {
        uuidStateFlow.value = null
    }

    override suspend fun getRememberedUuid(): String? = rememberedUuidStateFlow.value

    override suspend fun saveRememberedUuid(uuid: String) {
        rememberedUuidStateFlow.value = uuid
    }

    override suspend fun clearRememberedUuid() {
        rememberedUuidStateFlow.value = null
    }

    override suspend fun getServerUrl(): String? = serverUrlStateFlow.value

    override suspend fun saveServerUrl(serverUrl: String) {
        serverUrlStateFlow.value = serverUrl
    }

    override suspend fun clearServerUrl() {
        serverUrlStateFlow.value = null
    }

    override suspend fun saveSetup(serverUrl: String, uuid: String) {
        serverUrlStateFlow.value = serverUrl
        uuidStateFlow.value = uuid
    }

    override suspend fun saveLastSyncStatus(status: String?) {
        lastSyncStatusStateFlow.value = status
    }

    private val notificationsEnabledStateFlow = MutableStateFlow(true)
    override val notificationsEnabledFlow: Flow<Boolean> = notificationsEnabledStateFlow
    override suspend fun getNotificationsEnabled(): Boolean = notificationsEnabledStateFlow.value
    override suspend fun saveNotificationsEnabled(enabled: Boolean) {
        notificationsEnabledStateFlow.value = enabled
    }
}
