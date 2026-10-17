package com.pssst.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.DataStoreUserPreferencesRepository
import com.pssst.data.local.ItemManifestDao
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.local.PlayerStateDao
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.model.StateResponse
import com.pssst.data.network.model.GeOfferDto
import com.pssst.data.network.model.ItemEntryDto
import com.pssst.data.network.model.PotionEntryDto
import com.pssst.data.network.model.SkillEntryDto
import com.pssst.util.FarmingPaymentCatalog
import com.pssst.util.OsrsXpTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val playerStateDao: PlayerStateDao,
    private val preferencesRepository: UserPreferencesRepository,
    private val itemManifestDao: ItemManifestDao? = null,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5000)
) : ViewModel() {

    private val gson = Gson()
    private val searchQueryFlow = MutableStateFlow("")
    private val potionSearchQueryFlow = MutableStateFlow("")
    private val questSearchQueryFlow = MutableStateFlow("")
    private val questFilterFlow = MutableStateFlow(QuestFilter.ALL)
    private val bankSortModeFlow = MutableStateFlow(BankSortMode.DEFAULT)

    private data class FilterState(
        val searchQuery: String,
        val potionSearchQuery: String,
        val questSearchQuery: String,
        val questFilter: QuestFilter,
        val bankSortMode: BankSortMode
    )

    private val filtersFlow = combine(
        searchQueryFlow,
        potionSearchQueryFlow,
        questSearchQueryFlow,
        questFilterFlow,
        bankSortModeFlow
    ) { bankQ, potQ, questQ, qFilter, sort ->
        FilterState(bankQ, potQ, questQ, qFilter, sort)
    }

    private data class PrefsState(
        val uuid: String?,
        val rememberedUuid: String?,
        val serverUrl: String?,
        val lastSyncStatus: String?,
        val notificationsEnabled: Boolean
    )

    private val prefsFlow = combine(
        preferencesRepository.uuidFlow,
        preferencesRepository.rememberedUuidFlow,
        preferencesRepository.serverUrlFlow,
        preferencesRepository.lastSyncStatusFlow,
        preferencesRepository.notificationsEnabledFlow
    ) { uuid, rememberedUuid, serverUrl, lastSyncStatus, notificationsEnabled ->
        PrefsState(uuid, rememberedUuid, serverUrl, lastSyncStatus, notificationsEnabled)
    }

    val uiState: StateFlow<MainDashboardUiState> = combine(
        prefsFlow,
        playerStateDao.getLatestStateFlow(),
        filtersFlow
    ) { prefs, entityList, filters ->
        val hasUuid = !prefs.uuid.isNullOrBlank()
        val currentServerUrl = prefs.serverUrl ?: ""
        val activeUuid = prefs.uuid?.trim().orEmpty()
        val entity = if (activeUuid.isNotEmpty()) {
            entityList.find { it.uuid.equals(activeUuid, ignoreCase = true) } ?: entityList.firstOrNull()
        } else {
            entityList.firstOrNull()
        }
        if (entity == null) {
            MainDashboardUiState(
                uuid = prefs.uuid ?: "",
                rememberedUuid = prefs.rememberedUuid ?: "",
                serverUrl = currentServerUrl,
                hasConfiguredUuid = hasUuid,
                lastSyncStatus = prefs.lastSyncStatus,
                notificationsEnabled = prefs.notificationsEnabled,
                searchQuery = filters.searchQuery,
                potionSearchQuery = filters.potionSearchQuery,
                questSearchQuery = filters.questSearchQuery,
                questFilter = filters.questFilter,
                bankSortMode = filters.bankSortMode
            )
        } else {
            parseEntityToUiState(
                prefs.uuid ?: entity.uuid,
                prefs.rememberedUuid ?: "",
                currentServerUrl,
                prefs.lastSyncStatus,
                prefs.notificationsEnabled,
                entity,
                filters
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = sharingStarted,
        initialValue = MainDashboardUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
    }

    fun onPotionSearchQueryChanged(query: String) {
        potionSearchQueryFlow.value = query
    }

    fun onQuestSearchQueryChanged(query: String) {
        questSearchQueryFlow.value = query
    }

    fun onQuestFilterChanged(filter: QuestFilter) {
        questFilterFlow.value = filter
    }

    fun onBankSortModeChanged(mode: BankSortMode) {
        bankSortModeFlow.value = mode
    }

    fun saveSetup(serverUrl: String, uuid: String, rememberUuid: Boolean = true) {
        viewModelScope.launch {
            preferencesRepository.saveSetup(serverUrl.trim(), uuid.trim())
            if (rememberUuid) {
                preferencesRepository.saveRememberedUuid(uuid.trim())
            } else {
                preferencesRepository.clearRememberedUuid()
            }
        }
    }

    fun saveServerUrl(serverUrl: String) {
        viewModelScope.launch {
            preferencesRepository.saveServerUrl(serverUrl.trim())
        }
    }

    fun saveUuid(uuid: String) {
        viewModelScope.launch {
            preferencesRepository.saveUuid(uuid.trim())
        }
    }

    fun clearUuid() {
        viewModelScope.launch {
            preferencesRepository.clearUuid()
        }
    }

    fun clearRememberedUuid() {
        viewModelScope.launch {
            preferencesRepository.clearRememberedUuid()
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveNotificationsEnabled(enabled)
        }
    }

    private fun parseEntityToUiState(
        uuid: String,
        rememberedUuid: String,
        serverUrl: String,
        lastSyncStatus: String?,
        notificationsEnabled: Boolean,
        entity: PlayerStateEntity,
        filters: FilterState
    ): MainDashboardUiState {
        // Parse raw bank and inventory entries
        val bankEntries: List<ItemEntryDto> = try {
            val listType = object : TypeToken<List<ItemEntryDto>>() {}.type
            gson.fromJson(entity.bankJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val inventoryEntries: List<ItemEntryDto> = try {
            val listType = object : TypeToken<List<ItemEntryDto>>() {}.type
            gson.fromJson(entity.inventoryJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val geOfferEntries: List<GeOfferDto> = try {
            val listType = object : TypeToken<List<GeOfferDto>>() {}.type
            gson.fromJson(entity.geOffersJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val potionEntries: List<PotionEntryDto> = try {
            val listType = object : TypeToken<List<PotionEntryDto>>() {}.type
            gson.fromJson(entity.potionStorageJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val skillEntries: List<SkillEntryDto> = try {
            val listType = object : TypeToken<List<SkillEntryDto>>() {}.type
            gson.fromJson(entity.skillsJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val questMap: Map<String, String> = try {
            val mapType = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(entity.questsJson, mapType) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }

        // Query manifest names and icons from Room on Dispatchers.IO
        val allIds = (
            bankEntries.map { it.id } +
            inventoryEntries.map { it.id } +
            geOfferEntries.map { it.itemId } +
            potionEntries.map { it.itemId } +
            FarmingPaymentCatalog.ALL_PAYMENT_ITEM_IDS
        ).distinct().filter { it > 0 }

        val dao = itemManifestDao
        val manifestMap: Map<Int, ItemManifestEntity> = if (dao != null && allIds.isNotEmpty()) {
            runCatching {
                kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                    allIds.chunked(500).flatMap { chunk ->
                        dao.getItemsByIds(chunk)
                    }.associateBy { it.id }
                }
            }.onFailure {
                Log.e("MainViewModel", "Failed to load item manifests from Room", it)
            }.getOrDefault(emptyMap())
        } else {
            emptyMap()
        }

        // Map to ItemUiModel with name, base64 icon, and gePrice
        val bankItems: List<ItemUiModel> = bankEntries.map { entry ->
            val manifest = manifestMap[entry.id]
            ItemUiModel(
                id = entry.id,
                quantity = entry.quantity,
                name = manifest?.name ?: "",
                iconBase64 = manifest?.iconBase64,
                gePrice = manifest?.gePrice ?: 0L
            )
        }

        // Parse Inventory (28 slots)
        val inventorySlots: List<InventorySlotUiModel> = (0 until 28).map { index ->
            val entry = inventoryEntries.getOrNull(index)
            if (entry != null && entry.id > 0 && entry.quantity > 0) {
                val manifest = manifestMap[entry.id]
                InventorySlotUiModel(
                    index,
                    ItemUiModel(
                        id = entry.id,
                        quantity = entry.quantity,
                        name = manifest?.name ?: "",
                        iconBase64 = manifest?.iconBase64,
                        gePrice = manifest?.gePrice ?: 0L
                    )
                )
            } else {
                InventorySlotUiModel(index, null)
            }
        }

        // Parse GE Offers (always 8 slots)
        val geOffers: List<GeOfferSlotUiModel> = (1..8).map { slotNum ->
            val offer = geOfferEntries.find { it.slot == slotNum } ?: geOfferEntries.getOrNull(slotNum - 1)
            if (offer != null && offer.itemId > 0 && !offer.state.equals("EMPTY", ignoreCase = true)) {
                val manifest = manifestMap[offer.itemId]
                val stateEnum = when (offer.state.uppercase()) {
                    "BUYING" -> GeOfferState.BUYING
                    "SELLING" -> GeOfferState.SELLING
                    "BOUGHT" -> GeOfferState.BOUGHT
                    "SOLD" -> GeOfferState.SOLD
                    "CANCELLED_BUY" -> GeOfferState.CANCELLED_BUY
                    "CANCELLED_SELL" -> GeOfferState.CANCELLED_SELL
                    else -> GeOfferState.EMPTY
                }
                GeOfferSlotUiModel(
                    slot = slotNum,
                    itemId = offer.itemId,
                    itemName = manifest?.name ?: "Item #${offer.itemId}",
                    iconBase64 = manifest?.iconBase64,
                    quantitySold = offer.quantitySold,
                    totalQuantity = offer.totalQuantity,
                    price = offer.price,
                    spent = offer.spent,
                    state = stateEnum
                )
            } else {
                GeOfferSlotUiModel(slot = slotNum)
            }
        }

        // Parse Skills
        val skills: List<SkillUiModel> = OsrsXpTable.SKILL_NAMES.map { skillName ->
            val entry = skillEntries.find { it.name.equals(skillName, ignoreCase = true) }
            val xp = entry?.xp ?: 0L
            val lvl = entry?.level ?: OsrsXpTable.levelForXp(xp)
            val boosted = entry?.boostedLevel ?: lvl
            SkillUiModel(
                name = skillName,
                level = lvl,
                boostedLevel = boosted,
                xp = xp
            )
        }

        // Parse Potion Storage
        val potionStorageItems: List<PotionStorageItemUiModel> = potionEntries.map { entry ->
            val manifest = manifestMap[entry.itemId]
            PotionStorageItemUiModel(
                itemId = entry.itemId,
                name = manifest?.name ?: "Potion #${entry.itemId}",
                iconBase64 = manifest?.iconBase64,
                totalDoses = entry.doses,
                gePrice = manifest?.gePrice ?: 0L
            )
        }

        // Parse Quests
        val quests: List<QuestUiModel> = questMap.map { (name, stateStr) ->
            val state = when (stateStr.uppercase()) {
                "COMPLETED", "FINISHED" -> QuestState.COMPLETED
                "IN_PROGRESS", "STARTED" -> QuestState.IN_PROGRESS
                else -> QuestState.NOT_STARTED
            }
            QuestUiModel(name = name, state = state)
        }.sortedWith(compareBy<QuestUiModel> { it.state }.thenBy { it.name })

        // Parse Farming Payments
        val bankAndInvQuantities = mutableMapOf<Int, Int>()
        for (item in bankEntries) {
            bankAndInvQuantities[item.id] = (bankAndInvQuantities[item.id] ?: 0) + item.quantity
        }
        for (item in inventoryEntries) {
            bankAndInvQuantities[item.id] = (bankAndInvQuantities[item.id] ?: 0) + item.quantity
        }

        val farmingPayments: List<FarmingPaymentUiModel> = FarmingPaymentCatalog.PAYMENTS.map { def ->
            val manifest = manifestMap[def.paymentItemId]
            val owned = bankAndInvQuantities[def.paymentItemId] ?: 0
            FarmingPaymentUiModel(
                cropName = def.cropName,
                patchCategory = def.patchCategory,
                farmingLevel = def.requiredLevel,
                paymentItemId = def.paymentItemId,
                paymentItemName = manifest?.name ?: def.fallbackItemName,
                paymentIconBase64 = manifest?.iconBase64,
                requiredQuantity = def.paymentQuantity,
                ownedInBankAndInv = owned,
                hasEnough = owned >= def.paymentQuantity
            )
        }

        // Parse Varbits
        val varbits: Map<String, Int> = try {
            val mapType = object : TypeToken<Map<String, Int>>() {}.type
            gson.fromJson(entity.varbitsJson, mapType) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }

        // Parse Miscellania
        val rawApproval = varbits["72"]?.let { (it * 100) / 127 }
            ?: (varbits["3948"] ?: varbits["4000"] ?: 0)
        val approvalPercent = if (rawApproval > 100) (rawApproval * 100) / 127 else rawApproval
        val cofferAmount = (varbits["74"] ?: varbits["3949"] ?: varbits["4001"] ?: 0).toLong()
        val miscellania = MiscellaniaUiModel(approvalPercent, cofferAmount)

        // Parse Slayer
        val slayer: SlayerUiModel = try {
            val type = object : TypeToken<Map<String, Any?>>() {}.type
            val map: Map<String, Any?> = gson.fromJson(entity.slayerJson, type) ?: emptyMap()
            val taskName = map["taskName"]?.toString() ?: "No Active Task"
            val amount = when (val amt = map["amount"]) {
                is Number -> amt.toInt()
                is String -> amt.toIntOrNull() ?: 0
                else -> 0
            }
            val taskLocation = map["taskLocation"]?.toString() ?: ""
            SlayerUiModel(taskName, amount, taskLocation)
        } catch (e: Exception) {
            SlayerUiModel()
        }

        // Farming patches come straight from the plugin's per-region observations (stored in the raw payload)
        val farmingPatches: List<FarmingPatchUiModel> = try {
            gson.fromJson(entity.rawJson, StateResponse::class.java)?.data?.farming.orEmpty()
                .map(FarmingPatchUiModel::from)
                .sortedWith(compareBy({ it.regionName }, { it.patchLabel }))
        } catch (e: Exception) {
            emptyList()
        }

        // Parse Birdhouse Spaces
        val birdHouseSpaces = listOf(
            Triple("1626", "567", "Mushroom Meadow (North)"),
            Triple("1627", "568", "Mushroom Meadow (South)"),
            Triple("1628", "569", "Verdant Valley (Northeast)"),
            Triple("1629", "570", "Verdant Valley (Southwest)")
        )

        val birdHouses = birdHouseSpaces.map { (realVarpId, fallbackVarpId, spaceName) ->
            val value = varbits[realVarpId] ?: varbits[fallbackVarpId] ?: 0
            val primaryId = if (varbits.containsKey(realVarpId)) realVarpId else fallbackVarpId
            if (value == 0) {
                BirdHouseUiModel(primaryId, spaceName, "No House", "Empty (Unbuilt)", isReady = false, isSeeded = false)
            } else {
                val houseType = when ((value - 1) / 3) {
                    0 -> "Regular"
                    1 -> "Oak"
                    2 -> "Willow"
                    3 -> "Teak"
                    4 -> "Maple"
                    5 -> "Mahogany"
                    6 -> "Yew"
                    7 -> "Magic"
                    8 -> "Redwood"
                    else -> "Bird"
                }
                val isSeeded = (value % 3) == 2
                val isBuiltOnly = (value % 3) == 1
                val isReady = !isSeeded && !isBuiltOnly && value > 0
                val stateText = when {
                    isSeeded -> "Seeded (50m timer active)"
                    isBuiltOnly -> "Built (Needs Seeds)"
                    isReady -> "Ready to Harvest!"
                    else -> "State $value"
                }
                BirdHouseUiModel(
                    varpId = primaryId,
                    spaceName = spaceName,
                    houseType = "$houseType Birdhouse",
                    stateText = stateText,
                    isReady = isReady,
                    isSeeded = isSeeded
                )
            }
        }

        // Parse Dailies & Weeklies
        val tasks = mutableListOf<DailyTaskUiModel>()

        // 1. Zaff's Battlestaves
        val stavesClaimed = (varbits["4539"] ?: 0) == 1
        val stavesCount = when {
            varbits["4482"] == 1 -> 120
            varbits["4481"] == 1 -> 60
            varbits["4480"] == 1 -> 30
            varbits["4479"] == 1 -> 15
            else -> 15
        }
        val staffGePrice = manifestMap[1391]?.gePrice?.takeIf { it > 7000 } ?: 8200L
        val staffProfit = (staffGePrice - 7000L).coerceAtLeast(0L) * stavesCount
        tasks.add(
            DailyTaskUiModel(
                id = "zaff_staves",
                title = "Zaff's Battlestaves",
                subtitle = "Varrock Square ($stavesCount discounted staves)",
                statusText = if (stavesClaimed) "Claimed today" else "Available ($stavesCount ready)",
                isAvailable = !stavesClaimed,
                category = ResetCategory.DAILY,
                estimatedProfitGp = if (!stavesClaimed) staffProfit else 0L
            )
        )

        // 2. NMZ Herb Boxes
        val herbBoxesClaimed = varbits["3961"] ?: 0
        val remainingBoxes = (15 - herbBoxesClaimed).coerceAtLeast(0)
        val nmzPoints = varbits["1060"] ?: 0
        val affordableBoxes = if (nmzPoints > 0) nmzPoints / 9500 else 0
        val canBuyCount = minOf(remainingBoxes, affordableBoxes)
        val nmzStatus = when {
            remainingBoxes == 0 -> "All 15 claimed today"
            affordableBoxes == 0 -> "$herbBoxesClaimed/15 claimed (Need 9.5K pts)"
            else -> "$herbBoxesClaimed/15 claimed (Can buy $canBuyCount with ${formatQuantity(nmzPoints)} pts)"
        }
        tasks.add(
            DailyTaskUiModel(
                id = "nmz_herb_boxes",
                title = "NMZ Herb Boxes",
                subtitle = "Nightmare Zone Chest (Up to 15/day)",
                statusText = nmzStatus,
                isAvailable = remainingBoxes > 0 && affordableBoxes > 0,
                category = ResetCategory.DAILY
            )
        )

        // 3. Tears of Guthix (Weekly)
        val togCountdown = varbits["5099"] ?: 0
        val togQpBeforeReturn = varbits["456"] ?: 0
        val togAvailable = togCountdown == 0 && togQpBeforeReturn == 0
        val togStatus = when {
            togAvailable -> "Ready to play!"
            togCountdown > 0 -> "Cooldown active ($togCountdown days)"
            togQpBeforeReturn > 0 -> "Need 1 QP or 100K XP"
            else -> "Unavailable"
        }
        tasks.add(
            DailyTaskUiModel(
                id = "tears_of_guthix",
                title = "Tears of Guthix",
                subtitle = "Juna (Chasm of Tears)",
                statusText = togStatus,
                isAvailable = togAvailable,
                category = ResetCategory.WEEKLY
            )
        )

        // 4. Bert's Sand
        val sandClaimed = (varbits["4549"] ?: 0) == 1
        tasks.add(
            DailyTaskUiModel(
                id = "bert_sand",
                title = "Bert's Sand",
                subtitle = "Yanille (84 buckets to bank)",
                statusText = if (sandClaimed) "Delivered today" else "Available (Talk to Bert or auto-delivered)",
                isAvailable = !sandClaimed,
                category = ResetCategory.DAILY
            )
        )

        // 5. Robin's Bonemeal & Slime
        val bonemealClaimed = (varbits["4543"] ?: 0) != 0
        val bonemealCount = when {
            varbits["4490"] == 1 -> 39
            varbits["4489"] == 1 -> 26
            else -> 13
        }
        tasks.add(
            DailyTaskUiModel(
                id = "robin_bonemeal",
                title = "Robin's Bonemeal",
                subtitle = "Port Phasmatys ($bonemealCount free pots of slime & bonemeal)",
                statusText = if (bonemealClaimed) "Claimed today" else "Available ($bonemealCount exchanges)",
                isAvailable = !bonemealClaimed,
                category = ResetCategory.DAILY
            )
        )

        // 6. Cromperty's Pure Essence
        val essenceClaimed = (varbits["4547"] ?: 0) == 1
        val essCount = when {
            varbits["4461"] == 1 -> 250
            varbits["4460"] == 1 -> 150
            else -> 100
        }
        tasks.add(
            DailyTaskUiModel(
                id = "cromperty_essence",
                title = "Cromperty's Essence",
                subtitle = "East Ardougne ($essCount pure essence)",
                statusText = if (essenceClaimed) "Claimed today" else "Available ($essCount essence)",
                isAvailable = !essenceClaimed,
                category = ResetCategory.DAILY
            )
        )

        // 7. Flax Keeper Bowstrings
        val flaxClaimed = (varbits["4559"] ?: 0) != 0
        val flaxCount = when {
            varbits["4478"] == 1 -> 250
            varbits["4477"] == 1 -> 120
            varbits["4476"] == 1 -> 60
            else -> 30
        }
        tasks.add(
            DailyTaskUiModel(
                id = "flax_keeper",
                title = "Flax Keeper",
                subtitle = "Seers' Village ($flaxCount flax exchanged to bowstrings)",
                statusText = if (flaxClaimed) "Exchanged today" else "Available ($flaxCount bowstrings)",
                isAvailable = !flaxClaimed,
                category = ResetCategory.DAILY
            )
        )

        // 8. Thirus's Dynamite
        val dynamiteClaimed = (varbits["7939"] ?: 0) == 1
        val dynCount = when {
            varbits["7928"] == 1 -> 80
            varbits["7927"] == 1 -> 40
            else -> 20
        }
        tasks.add(
            DailyTaskUiModel(
                id = "thirus_dynamite",
                title = "Thirus's Dynamite",
                subtitle = "Lovakengj ($dynCount free dynamite)",
                statusText = if (dynamiteClaimed) "Claimed today" else "Available ($dynCount dynamite)",
                isAvailable = !dynamiteClaimed,
                category = ResetCategory.DAILY
            )
        )

        // 9. Lundail's Runes
        val runesClaimed = (varbits["4540"] ?: 0) == 1
        tasks.add(
            DailyTaskUiModel(
                id = "lundail_runes",
                title = "Lundail's Free Runes",
                subtitle = "Mage Arena (Wilderness)",
                statusText = if (runesClaimed) "Collected today" else "Available (50-250 free runes)",
                isAvailable = !runesClaimed,
                category = ResetCategory.DAILY
            )
        )

        // 10. Rantz's Ogre Arrows
        val arrowsClaimed = (varbits["4563"] ?: 0) != 0
        tasks.add(
            DailyTaskUiModel(
                id = "rantz_arrows",
                title = "Rantz's Ogre Arrows",
                subtitle = "Feldip Hills (Free arrows / Western Diary)",
                statusText = if (arrowsClaimed) "Collected today" else "Available from Rantz",
                isAvailable = !arrowsClaimed,
                category = ResetCategory.DAILY
            )
        )

        val dailyTasks = tasks.sortedWith(
            compareByDescending<DailyTaskUiModel> { it.isAvailable }
                .thenBy { it.category }
                .thenBy { it.title }
        )

        return MainDashboardUiState(
            uuid = uuid,
            rememberedUuid = rememberedUuid,
            serverUrl = serverUrl,
            hasConfiguredUuid = uuid.isNotBlank(),
            lastUpdated = entity.updatedAt,
            lastSyncedAt = entity.lastSyncedAt,
            lastSyncStatus = lastSyncStatus,
            bankItems = bankItems,
            inventorySlots = inventorySlots,
            miscellania = miscellania,
            slayer = slayer,
            farmingPatches = farmingPatches,
            birdHouses = birdHouses,
            dailyTasks = dailyTasks,
            geOffers = geOffers,
            skills = skills,
            potionStorageItems = potionStorageItems,
            farmingPayments = farmingPayments,
            quests = quests,
            notificationsEnabled = notificationsEnabled,
            isLoading = false,
            searchQuery = filters.searchQuery,
            potionSearchQuery = filters.potionSearchQuery,
            questSearchQuery = filters.questSearchQuery,
            questFilter = filters.questFilter,
            bankSortMode = filters.bankSortMode
        )
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AppDatabase.getDatabase(application)
                    val prefs = DataStoreUserPreferencesRepository(application)
                    return MainViewModel(db.playerStateDao(), prefs, db.itemManifestDao()) as T
                }
            }
    }
}
