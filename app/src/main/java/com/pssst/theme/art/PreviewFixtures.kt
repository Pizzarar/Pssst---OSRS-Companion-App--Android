package com.pssst.theme.art

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.data.local.GoalConfig
import com.pssst.theme.OsrsButton
import com.pssst.theme.OsrsButtonVariant
import com.pssst.theme.OsrsDarkStone
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsSwitch
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot
import com.pssst.ui.main.BirdHouseUiModel
import com.pssst.ui.main.ChestVectorIcon
import com.pssst.ui.main.CoinsVectorIcon
import com.pssst.ui.main.DailyTaskUiModel
import com.pssst.ui.main.DashboardTab
import com.pssst.ui.main.FarmingPatchUiModel
import com.pssst.ui.main.FarmingPaymentUiModel
import com.pssst.ui.main.GeOfferSlotUiModel
import com.pssst.ui.main.GeOfferState
import com.pssst.ui.main.HamburgerVectorIcon
import com.pssst.ui.main.HourglassVectorIcon
import com.pssst.ui.main.InventorySlotUiModel
import com.pssst.ui.main.ItemUiModel
import com.pssst.ui.main.MainDashboardUiState
import com.pssst.ui.main.MiscellaniaUiModel
import com.pssst.ui.main.PencilVectorIcon
import com.pssst.ui.main.PotionStorageItemUiModel
import com.pssst.ui.main.QuestState
import com.pssst.ui.main.QuestUiModel
import com.pssst.ui.main.ResetCategory
import com.pssst.ui.main.ShieldVectorIcon
import com.pssst.ui.main.SkillUiModel
import com.pssst.ui.main.SlayerUiModel
import com.pssst.ui.main.StarVectorIcon
import com.pssst.ui.main.TopBarReadyRow
import com.pssst.util.GoalPriceMode
import com.pssst.util.PhoneAddresses

/**
 * Shared fixture data and phone chrome wrapper for `@Preview` composables and Robolectric screen captures.
 */
object PreviewFixtures {

    val FIXED_NOW_MS: Long = System.currentTimeMillis()

    private val bundledIcons: Map<Int, String> by lazy {
        val file = sequenceOf(
            java.io.File("src/main/assets/items-complete.json"),
            java.io.File("app/src/main/assets/items-complete.json")
        ).firstOrNull { it.exists() } ?: return@lazy emptyMap()
        val result = HashMap<Int, String>(1200)
        try {
            com.google.gson.stream.JsonReader(file.bufferedReader(Charsets.UTF_8)).use { reader ->
                reader.beginObject()
                while (reader.hasNext()) {
                    if (reader.nextName() == "items") {
                        reader.beginArray()
                        while (reader.hasNext()) {
                            reader.beginObject()
                            var id = -1
                            var icon: String? = null
                            while (reader.hasNext()) {
                                when (reader.nextName()) {
                                    "id" -> id = reader.nextInt()
                                    "iconBase64" -> icon = reader.nextString()
                                    else -> reader.skipValue()
                                }
                            }
                            reader.endObject()
                            if (id != -1 && !icon.isNullOrBlank()) {
                                result[id] = icon
                            }
                        }
                        reader.endArray()
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endObject()
            }
        } catch (_: Exception) {
        }
        result
    }

    fun iconFor(itemId: Int): String? = bundledIcons[itemId]

    val sampleAddresses = listOf(
        PhoneAddresses.Address("192.168.1.42", PhoneAddresses.LABEL_HOME),
        PhoneAddresses.Address("100.94.18.7", PhoneAddresses.LABEL_TAILSCALE)
    )

    val sampleBankItems = listOf(
        ItemUiModel(id = 995, quantity = 24_500_000, name = "Coins", iconBase64 = iconFor(995), gePrice = 1L, haPrice = 1L, tab = 0),
        ItemUiModel(id = 11832, quantity = 1, name = "Bandos chestplate", iconBase64 = iconFor(11832), gePrice = 28_400_000L, haPrice = 174_006L, tab = 0),
        ItemUiModel(id = 4151, quantity = 1, name = "Abyssal whip", iconBase64 = iconFor(4151), gePrice = 1_450_000L, haPrice = 72_000L, tab = 1),
        ItemUiModel(id = 11802, quantity = 1, name = "Armadyl godsword", iconBase64 = iconFor(11802), gePrice = 11_800_000L, haPrice = 750_000L, tab = 1),
        ItemUiModel(id = 11283, quantity = 1, name = "Dragonfire shield", iconBase64 = iconFor(11283), gePrice = 3_950_000L, haPrice = 1_200_000L, tab = 1),
        ItemUiModel(id = 560, quantity = 145_200, name = "Death rune", iconBase64 = iconFor(560), gePrice = 178L, haPrice = 108L, tab = 2),
        ItemUiModel(id = 565, quantity = 84_500, name = "Blood rune", iconBase64 = iconFor(565), gePrice = 215L, haPrice = 240L, tab = 2),
        ItemUiModel(id = 556, quantity = 12_400_000, name = "Air rune", iconBase64 = iconFor(556), gePrice = 5L, haPrice = 2L, tab = 2),
        ItemUiModel(id = 257, quantity = 1_840, name = "Ranarr weed", iconBase64 = iconFor(257), gePrice = 6_400L, haPrice = 19L, tab = 3),
        ItemUiModel(id = 3000, quantity = 920, name = "Snapdragon", iconBase64 = iconFor(3000), gePrice = 8_200L, haPrice = 39L, tab = 3),
        ItemUiModel(id = 536, quantity = 640, name = "Dragon bones", iconBase64 = iconFor(536), gePrice = 2_850L, haPrice = 72L, tab = 4),
        ItemUiModel(id = 385, quantity = 4_200, name = "Shark", iconBase64 = iconFor(385), gePrice = 690L, haPrice = 102L, tab = 5),
        ItemUiModel(id = 12934, quantity = 285_000, name = "Zulrah's scales", iconBase64 = iconFor(12934), gePrice = 148L, haPrice = 12L, tab = 6),
        ItemUiModel(id = 19553, quantity = 1, name = "Amulet of torture", iconBase64 = iconFor(19553), gePrice = 16_900_000L, haPrice = 120_600L, tab = 1),
        ItemUiModel(id = 6585, quantity = 1, name = "Amulet of fury", iconBase64 = iconFor(6585), gePrice = 2_180_000L, haPrice = 121_200L, tab = 1),
        ItemUiModel(id = 21521, quantity = 24, name = "Teak birdhouse", iconBase64 = iconFor(21521), gePrice = 1_120L, haPrice = 120L, tab = 7)
    )

    val sampleInventorySlots: List<InventorySlotUiModel> = (0 until 28).map { slot ->
        val item = when (slot) {
            0 -> ItemUiModel(id = 995, quantity = 14_250_000, name = "Coins", iconBase64 = iconFor(995), gePrice = 1L)
            1 -> ItemUiModel(id = 561, quantity = 185_000, name = "Nature rune", iconBase64 = iconFor(561), gePrice = 94L)
            2 -> ItemUiModel(id = 554, quantity = 42_000, name = "Fire rune", iconBase64 = iconFor(554), gePrice = 5L)
            3 -> ItemUiModel(id = 4151, quantity = 1, name = "Abyssal whip", iconBase64 = iconFor(4151), gePrice = 1_450_000L)
            4 -> ItemUiModel(id = 12954, quantity = 1, name = "Dragon defender", iconBase64 = iconFor(12954), gePrice = 0L)
            5 -> ItemUiModel(id = 2434, quantity = 1, name = "Prayer potion(4)", iconBase64 = iconFor(2434), gePrice = 8_900L)
            6 -> ItemUiModel(id = 2434, quantity = 1, name = "Prayer potion(4)", iconBase64 = iconFor(2434), gePrice = 8_900L)
            7 -> ItemUiModel(id = 3024, quantity = 1, name = "Super restore(4)", iconBase64 = iconFor(3024), gePrice = 11_400L)
            8 -> ItemUiModel(id = 5329, quantity = 1, name = "Magic secateurs", iconBase64 = iconFor(5329), gePrice = 40_000L)
            9 -> ItemUiModel(id = 5343, quantity = 1, name = "Seed dibber", iconBase64 = iconFor(5343), gePrice = 15L)
            10 -> ItemUiModel(id = 952, quantity = 1, name = "Spade", iconBase64 = iconFor(952), gePrice = 60L)
            11 -> ItemUiModel(id = 5295, quantity = 18, name = "Ranarr seed", iconBase64 = iconFor(5295), gePrice = 27_500L)
            12 -> ItemUiModel(id = 21488, quantity = 6, name = "Ultracompost", iconBase64 = iconFor(21488), gePrice = 1_150L)
            13 -> ItemUiModel(id = 257, quantity = 8, name = "Ranarr weed", iconBase64 = iconFor(257), gePrice = 6_400L)
            14 -> ItemUiModel(id = 385, quantity = 1, name = "Shark", iconBase64 = iconFor(385), gePrice = 690L)
            15 -> ItemUiModel(id = 385, quantity = 1, name = "Shark", iconBase64 = iconFor(385), gePrice = 690L)
            16 -> ItemUiModel(id = 8013, quantity = 120, name = "Teleport to house", iconBase64 = iconFor(8013), gePrice = 520L)
            17 -> ItemUiModel(id = 2347, quantity = 1, name = "Hammer", iconBase64 = iconFor(2347), gePrice = 45L)
            18 -> ItemUiModel(id = 1755, quantity = 1, name = "Chisel", iconBase64 = iconFor(1755), gePrice = 15L)
            19 -> ItemUiModel(id = 22198, quantity = 4, name = "Yew birdhouse", iconBase64 = iconFor(22198), gePrice = 2_450L)
            else -> null
        }
        InventorySlotUiModel(slotIndex = slot, item = item)
    }

    val samplePotions = listOf(
        PotionStorageItemUiModel(itemId = 2434, name = "Prayer potion", iconBase64 = iconFor(2434), totalDoses = 486, gePrice = 8_900L),
        PotionStorageItemUiModel(itemId = 3024, name = "Super restore", iconBase64 = iconFor(3024), totalDoses = 312, gePrice = 11_400L),
        PotionStorageItemUiModel(itemId = 6685, name = "Saradomin brew", iconBase64 = iconFor(6685), totalDoses = 640, gePrice = 9_600L),
        PotionStorageItemUiModel(itemId = 12695, name = "Super combat potion", iconBase64 = iconFor(12695), totalDoses = 128, gePrice = 14_200L),
        PotionStorageItemUiModel(itemId = 12625, name = "Stamina potion", iconBase64 = iconFor(12625), totalDoses = 204, gePrice = 1_850L)
    )

    val sampleGeOffers = listOf(
        GeOfferSlotUiModel(
            slot = 1,
            itemId = 257,
            itemName = "Ranarr weed",
            iconBase64 = iconFor(257),
            quantitySold = 500,
            totalQuantity = 500,
            price = 6_400,
            spent = 3_200_000L,
            state = GeOfferState.BOUGHT
        ),
        GeOfferSlotUiModel(
            slot = 2,
            itemId = 536,
            itemName = "Dragon bones",
            iconBase64 = iconFor(536),
            quantitySold = 650,
            totalQuantity = 1_000,
            price = 2_840,
            spent = 1_846_000L,
            state = GeOfferState.BUYING
        ),
        GeOfferSlotUiModel(
            slot = 3,
            itemId = 12934,
            itemName = "Zulrah's scales",
            iconBase64 = iconFor(12934),
            quantitySold = 20_000,
            totalQuantity = 50_000,
            price = 150,
            spent = 3_000_000L,
            state = GeOfferState.SELLING
        ),
        GeOfferSlotUiModel(
            slot = 4,
            itemId = 4151,
            itemName = "Abyssal whip",
            iconBase64 = iconFor(4151),
            quantitySold = 1,
            totalQuantity = 1,
            price = 1_460_000,
            spent = 1_460_000L,
            state = GeOfferState.SOLD
        ),
        GeOfferSlotUiModel(slot = 5),
        GeOfferSlotUiModel(slot = 6),
        GeOfferSlotUiModel(slot = 7),
        GeOfferSlotUiModel(slot = 8)
    )

    private fun skill(name: String, level: Int, boostedLevel: Int = level, xp: Long): SkillUiModel {
        val itemId = SkillUiModel.SKILL_ITEM_IDS[name] ?: 0
        return SkillUiModel(
            name = name,
            level = level,
            boostedLevel = boostedLevel,
            xp = xp,
            itemId = itemId,
            iconBase64 = iconFor(itemId)
        )
    }

    // Canonical OSRS 3-column order matching OsrsXpTable.SKILL_NAMES
    val sampleSkills = listOf(
        skill("Attack", level = 99, xp = 13_450_210L),
        skill("Hitpoints", level = 99, xp = 21_400_000L),
        skill("Mining", level = 85, xp = 3_350_000L),
        skill("Strength", level = 99, xp = 15_820_400L),
        skill("Agility", level = 82, xp = 2_480_000L),
        skill("Smithing", level = 86, xp = 3_690_000L),
        skill("Defence", level = 94, xp = 8_210_000L),
        skill("Herblore", level = 90, boostedLevel = 94, xp = 5_520_000L),
        skill("Fishing", level = 91, xp = 6_010_000L),
        skill("Ranged", level = 99, xp = 18_100_500L),
        skill("Thieving", level = 86, xp = 3_710_000L),
        skill("Cooking", level = 99, xp = 13_110_000L),
        skill("Prayer", level = 85, boostedLevel = 72, xp = 3_420_000L),
        skill("Crafting", level = 88, xp = 4_490_000L),
        skill("Firemaking", level = 90, xp = 5_410_000L),
        skill("Magic", level = 99, xp = 14_900_120L),
        skill("Fletching", level = 92, xp = 6_720_000L),
        skill("Woodcutting", level = 89, xp = 4_920_000L),
        skill("Runecraft", level = 78, xp = 1_680_000L),
        skill("Slayer", level = 95, xp = 9_150_000L),
        skill("Farming", level = 99, xp = 16_250_000L),
        skill("Construction", level = 84, xp = 3_010_000L),
        skill("Hunter", level = 87, xp = 4_080_000L)
    )

    val sampleQuests = listOf(
        QuestUiModel(name = "Dragon Slayer II", state = QuestState.COMPLETED),
        QuestUiModel(name = "Song of the Elves", state = QuestState.COMPLETED),
        QuestUiModel(name = "Desert Treasure II", state = QuestState.IN_PROGRESS),
        QuestUiModel(name = "Monkey Madness II", state = QuestState.COMPLETED),
        QuestUiModel(name = "While Guthix Sleeps", state = QuestState.IN_PROGRESS),
        QuestUiModel(name = "Sins of the Father", state = QuestState.COMPLETED),
        QuestUiModel(name = "A Night at the Theatre", state = QuestState.NOT_STARTED),
        QuestUiModel(name = "Recipe for Disaster", state = QuestState.COMPLETED)
    )

    val sampleDailyTasks = listOf(
        DailyTaskUiModel(
            id = "herb_boxes",
            title = "NMZ Herb Boxes",
            subtitle = "Dominic Onion (Nightmare Zone)",
            statusText = "15 available",
            isAvailable = true,
            category = ResetCategory.DAILY,
            itemId = 11738,
            iconBase64 = iconFor(11738)
        ),
        DailyTaskUiModel(
            id = "battlestaves",
            title = "Zaff's Battlestaves",
            subtitle = "Varrock Staff Shop",
            statusText = "Ready to claim",
            isAvailable = true,
            category = ResetCategory.DAILY,
            itemId = 1391,
            iconBase64 = iconFor(1391)
        ),
        DailyTaskUiModel(
            id = "pure_essence",
            title = "Wizard Cromperty Essence",
            subtitle = "East Ardougne",
            statusText = "Claimed today",
            isAvailable = false,
            category = ResetCategory.DAILY,
            itemId = 7936,
            iconBase64 = iconFor(7936)
        ),
        DailyTaskUiModel(
            id = "sand",
            title = "Bert's Daily Sand",
            subtitle = "Yanille (84 buckets)",
            statusText = "Ready",
            isAvailable = true,
            category = ResetCategory.DAILY,
            itemId = 1783,
            iconBase64 = iconFor(1783)
        ),
        DailyTaskUiModel(
            id = "tears",
            title = "Tears of Guthix",
            subtitle = "Lumbridge Swamp Cavern",
            statusText = "Eligible now",
            isAvailable = true,
            category = ResetCategory.WEEKLY,
            itemId = 2888,
            iconBase64 = iconFor(2888)
        )
    )

    val sampleBirdHouses = listOf(
        BirdHouseUiModel(
            varpId = "1626",
            spaceName = "Mushroom Meadow (North)",
            houseType = "Yew birdhouse",
            itemId = 22198,
            iconBase64 = iconFor(22198),
            state = "SEEDED",
            observedAtMs = FIXED_NOW_MS - 3_600_000L,
            readyAtMs = FIXED_NOW_MS - 600_000L
        ),
        BirdHouseUiModel(
            varpId = "1627",
            spaceName = "Verdant Valley (Northeast)",
            houseType = "Yew birdhouse",
            itemId = 22198,
            iconBase64 = iconFor(22198),
            state = "SEEDED",
            observedAtMs = FIXED_NOW_MS - 3_600_000L,
            readyAtMs = FIXED_NOW_MS - 600_000L
        ),
        BirdHouseUiModel(
            varpId = "1628",
            spaceName = "Verdant Valley (Southwest)",
            houseType = "Yew birdhouse",
            itemId = 22198,
            iconBase64 = iconFor(22198),
            state = "SEEDED",
            observedAtMs = FIXED_NOW_MS - 900_000L,
            readyAtMs = FIXED_NOW_MS + 2_100_000L
        ),
        BirdHouseUiModel(
            varpId = "1629",
            spaceName = "Mushroom Meadow (South)",
            houseType = null,
            itemId = 21512,
            iconBase64 = iconFor(21512),
            state = "EMPTY",
            observedAtMs = FIXED_NOW_MS - 3_600_000L,
            readyAtMs = 0L
        )
    )

    val sampleFarmingPatches = listOf(
        FarmingPatchUiModel(
            patchId = "hosidius_herb",
            regionName = "Hosidius",
            patchLabel = "Hosidius Herb",
            cropName = "Ranarr",
            itemId = 257,
            iconBase64 = iconFor(257),
            cropState = "HARVESTABLE",
            stage = 4,
            stages = 5,
            tickMinutes = 20,
            observedAtMs = FIXED_NOW_MS - 5_400_000L,
            doneAtMs = FIXED_NOW_MS - 600_000L
        ),
        FarmingPatchUiModel(
            patchId = "catherby_herb",
            regionName = "Catherby",
            patchLabel = "Catherby Herb",
            cropName = "Snapdragon",
            itemId = 3000,
            iconBase64 = iconFor(3000),
            cropState = "GROWING",
            stage = 2,
            stages = 5,
            tickMinutes = 20,
            observedAtMs = FIXED_NOW_MS - 600_000L,
            doneAtMs = FIXED_NOW_MS + 2_400_000L
        ),
        FarmingPatchUiModel(
            patchId = "ardougne_herb",
            regionName = "Ardougne",
            patchLabel = "Ardougne Herb",
            cropName = "Ranarr",
            itemId = 257,
            iconBase64 = iconFor(257),
            cropState = "HARVESTABLE",
            stage = 4,
            stages = 5,
            tickMinutes = 20,
            observedAtMs = FIXED_NOW_MS - 5_400_000L,
            doneAtMs = FIXED_NOW_MS - 300_000L
        ),
        FarmingPatchUiModel(
            patchId = "varrock_tree",
            regionName = "Varrock",
            patchLabel = "Varrock Tree",
            cropName = "Yew tree",
            itemId = 1515,
            iconBase64 = iconFor(1515),
            cropState = "DISEASED",
            stage = 3,
            stages = 7,
            tickMinutes = 40,
            observedAtMs = FIXED_NOW_MS - 1_800_000L,
            doneAtMs = null
        ),
        FarmingPatchUiModel(
            patchId = "falador_herb",
            regionName = "Falador",
            patchLabel = "Falador Herb",
            cropName = null,
            itemId = 6055,
            iconBase64 = iconFor(6055),
            cropState = "EMPTY",
            stage = 0,
            stages = 5,
            tickMinutes = 20,
            observedAtMs = FIXED_NOW_MS - 3_600_000L,
            doneAtMs = null
        )
    )

    val sampleFarmingPayments = listOf(
        FarmingPaymentUiModel(
            cropName = "Yew tree",
            patchCategory = "Trees",
            farmingLevel = 60,
            paymentItemId = 6016,
            paymentItemName = "Cactus spine",
            paymentIconBase64 = iconFor(6016),
            requiredQuantity = 10,
            ownedInBankAndInv = 84,
            hasEnough = true
        ),
        FarmingPaymentUiModel(
            cropName = "Magic tree",
            patchCategory = "Trees",
            farmingLevel = 75,
            paymentItemId = 5974,
            paymentItemName = "Coconut",
            paymentIconBase64 = iconFor(5974),
            requiredQuantity = 25,
            ownedInBankAndInv = 112,
            hasEnough = true
        ),
        FarmingPaymentUiModel(
            cropName = "Dragonfruit tree",
            patchCategory = "Fruit Trees",
            farmingLevel = 81,
            paymentItemId = 5974,
            paymentItemName = "Coconut",
            paymentIconBase64 = iconFor(5974),
            requiredQuantity = 15,
            ownedInBankAndInv = 112,
            hasEnough = true
        ),
        FarmingPaymentUiModel(
            cropName = "Redwood tree",
            patchCategory = "Special",
            farmingLevel = 90,
            paymentItemId = 22869,
            paymentItemName = "Dragonfruit",
            paymentIconBase64 = iconFor(22869),
            requiredQuantity = 6,
            ownedInBankAndInv = 2,
            hasEnough = false
        ),
        FarmingPaymentUiModel(
            cropName = "Celastrus tree",
            patchCategory = "Special",
            farmingLevel = 85,
            paymentItemId = 3138,
            paymentItemName = "Potato cactus",
            paymentIconBase64 = iconFor(3138),
            requiredQuantity = 8,
            ownedInBankAndInv = 0,
            hasEnough = false
        )
    )

    val sampleGoalSearchResults = listOf(
        ItemUiModel(id = 20997, quantity = 1, name = "Twisted bow", iconBase64 = iconFor(20997), gePrice = 1_620_000_000L, haPrice = 2_400_000L),
        ItemUiModel(id = 11802, quantity = 1, name = "Armadyl godsword", iconBase64 = iconFor(11802), gePrice = 11_800_000L, haPrice = 750_000L),
        ItemUiModel(id = 11832, quantity = 1, name = "Bandos chestplate", iconBase64 = iconFor(11832), gePrice = 28_400_000L, haPrice = 174_006L),
        ItemUiModel(id = 19553, quantity = 1, name = "Amulet of torture", iconBase64 = iconFor(19553), gePrice = 16_900_000L, haPrice = 120_600L),
        ItemUiModel(id = 11283, quantity = 1, name = "Dragonfire shield", iconBase64 = iconFor(11283), gePrice = 3_950_000L, haPrice = 1_200_000L)
    )

    val populatedState = MainDashboardUiState(
        uuid = "goblin-whip",
        rememberedUuid = "goblin-whip",
        hasConfiguredUuid = true,
        hasSyncedData = true,
        lastUpdated = FIXED_NOW_MS - 120_000L,
        lastSyncedAt = FIXED_NOW_MS - 120_000L,
        gameObservedAt = FIXED_NOW_MS - 120_000L,
        bankShared = true,
        lastSyncStatus = "OK • received from 192.168.1.42",
        bankItems = sampleBankItems,
        inventorySlots = sampleInventorySlots,
        miscellania = MiscellaniaUiModel(
            approvalPercent = 96,
            cofferAmount = 3_450_000L,
            blastFurnaceCoffer = 540_000L,
            nmzCoffer = 1_250_000L,
            nmzRewardPoints = 2_450_000,
            pohServantUses = 24,
            lmsCoffer = 750_000L
        ),
        slayer = SlayerUiModel(
            taskName = "Abyssal demons",
            amount = 142,
            taskLocation = "Catacombs of Kourend",
            initialAmount = 200,
            points = 840,
            streak = 175,
            itemId = 4149,
            iconBase64 = iconFor(4149) ?: iconFor(4155),
            storageUnlocked = true,
            storedTaskName = "Hydras",
            storedAmount = 170,
            storedItemId = 23042,
            storedIconBase64 = iconFor(23042) ?: iconFor(4155)
        ),
        farmingPatches = sampleFarmingPatches,
        birdHouses = sampleBirdHouses,
        dailyTasks = sampleDailyTasks,
        geOffers = sampleGeOffers,
        skills = sampleSkills,
        potionStorageItems = samplePotions,
        farmingPayments = sampleFarmingPayments,
        quests = sampleQuests,
        topBarIcons = listOf(257, 21512, 11738, 995).associateWith { iconFor(it) },
        notificationsEnabled = true,
        pricesAsOfMs = FIXED_NOW_MS - 300_000L,
        goalConfig = GoalConfig(
            source = -1,
            priceMode = GoalPriceMode.GE,
            targetItemId = 20997,
            targetGp = 1_620_000_000L
        ),
        goalTargetItem = ItemUiModel(
            id = 20997,
            quantity = 1,
            name = "Twisted bow",
            iconBase64 = iconFor(20997),
            gePrice = 1_620_000_000L,
            haPrice = 2_400_000L
        ),
        goalItemSearchResults = sampleGoalSearchResults
    )

    val emptyState = MainDashboardUiState(
        uuid = "goblin-whip",
        rememberedUuid = "goblin-whip",
        hasConfiguredUuid = true,
        hasSyncedData = true,
        bankShared = false,
        bankItems = emptyList(),
        potionStorageItems = emptyList(),
        quests = emptyList(),
        dailyTasks = emptyList(),
        birdHouses = emptyList(),
        farmingPatches = emptyList(),
        goalConfig = GoalConfig()
    )
}

/**
 * Renders a screen inside the exact phone top oak bar, stone ground, and bottom stone sill bar
 * used by `DashboardScreen` for `@Preview`s and Robolectric full-screen captures.
 */
@Composable
fun PhoneScreenShell(
    selectedTab: DashboardTab,
    state: MainDashboardUiState = PreviewFixtures.populatedState,
    showEditIcon: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val tabs = DashboardTab.entries
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OsrsDarkStone,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .oakBeamFrame()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                        HamburgerVectorIcon(tint = OsrsGold, modifier = Modifier.size(25.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        GoldLeafText(
                            text = selectedTab.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            BulletGlyph(size = 5.dp, color = OsrsGold)
                            Text(
                                text = "Synced 2m ago",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.5.sp,
                                color = OsrsTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TopBarReadyRow(
                        state = state,
                        nowMs = PreviewFixtures.FIXED_NOW_MS,
                        onNavigate = { _, _ -> }
                    )
                    if (showEditIcon) {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = {}, modifier = Modifier.size(38.dp)) {
                            PencilVectorIcon(tint = OsrsGold, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .stoneSillFrame()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEach { tab ->
                        val isSelected = tab == selectedTab
                        val tint = if (isSelected) OsrsGold else OsrsTextMuted
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (isSelected) {
                                        Modifier.ledgerWellSlot(empty = true, highlighted = true)
                                    } else {
                                        Modifier
                                    }
                                )
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when (tab) {
                                DashboardTab.OVERVIEW -> ShieldVectorIcon(selected = isSelected, tint = tint)
                                DashboardTab.STORAGE -> ChestVectorIcon(selected = isSelected, tint = tint)
                                DashboardTab.GE -> CoinsVectorIcon(selected = isSelected, tint = tint)
                                DashboardTab.PROGRESSION -> StarVectorIcon(selected = isSelected, tint = tint)
                                DashboardTab.TIMERS -> HourglassVectorIcon(selected = isSelected, tint = tint)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tint,
                                fontSize = 11.5.sp,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall.copy(shadow = OsrsHardShadow)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .stoneGround()
        ) {
            content()
        }
    }
}

/**
 * Renders the leather-bound navigation drawer sheet for `@Preview` and Robolectric screen captures.
 */
@Composable
fun DrawerSheetPreviewContent(
    state: MainDashboardUiState = PreviewFixtures.populatedState,
    modifier: Modifier = Modifier
) {
    val tabs = DashboardTab.entries
    Row(
        modifier = modifier
            .fillMaxSize()
            .stoneGround()
    ) {
        Column(
            modifier = Modifier
                .width(310.dp)
                .fillMaxHeight()
                .leatherBoundPanel()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TitlePlaque(
                    title = "Pssst Companion",
                    subtitle = "OSRS Offline-First Tracker"
                )

                Spacer(modifier = Modifier.height(14.dp))
                OsrsHeaderDivider(withCenterFlourish = true)
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .osrsPanel(weight = FrameWeight.MEDIUM)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        GoldLeafText(
                            text = "Connection Status",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sync Token: ${state.uuid}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = "Phone address for RuneLite: 192.168.1.42 or 100.94.18.7",
                            style = MaterialTheme.typography.labelSmall,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = state.lastSyncStatus ?: "OK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = OsrsHerbGreen
                        )
                        Text(
                            text = "Last update received 2m ago",
                            style = MaterialTheme.typography.labelSmall,
                            color = OsrsTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .osrsPanel(weight = FrameWeight.LIGHT)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Item prices",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = OsrsTextPrimary
                        )
                        Text(
                            text = "Refreshed from your RuneLite client on each sync",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .osrsPanel(weight = FrameWeight.LIGHT)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = OsrsTextPrimary
                        )
                        Text(
                            text = "Active alerts",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )
                    }
                    OsrsSwitch(checked = true, onCheckedChange = {})
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionHeaderPlaque(title = "Jump to Section")
                Spacer(modifier = Modifier.height(8.dp))
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == 0
                    val navMod = if (isSelected) {
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .osrsSlot(hasItem = false, cornerRadius = 2.dp, highlightBorderColor = OsrsGold)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    }
                    Row(
                        modifier = navMod,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BulletGlyph(
                            size = 6.dp,
                            color = if (isSelected) OsrsGold else OsrsTextMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) OsrsGold else OsrsTextPrimary
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OsrsButton(
                    text = "Listen Now",
                    onClick = {},
                    variant = OsrsButtonVariant.STONE,
                    textColor = OsrsGold,
                    modifier = Modifier.fillMaxWidth()
                )
                OsrsButton(
                    text = "Disconnect / Change Token",
                    onClick = {},
                    variant = OsrsButtonVariant.DANGER,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
