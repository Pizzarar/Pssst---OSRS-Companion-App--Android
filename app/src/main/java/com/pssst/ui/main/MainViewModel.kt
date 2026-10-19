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
import com.pssst.data.local.GoalConfig
import com.pssst.data.local.GoalPrefs
import com.pssst.data.local.ItemManifestDao
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.local.PlayerStateDao
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.model.SlayerEntryDto
import com.pssst.data.network.model.StateDataDto
import com.pssst.data.network.model.StateResponse
import com.pssst.data.network.model.GeOfferDto
import com.pssst.data.network.model.ItemEntryDto
import com.pssst.data.network.model.PotionEntryDto
import com.pssst.data.network.model.SkillEntryDto
import com.pssst.util.DailyTasks
import com.pssst.util.FarmingPaymentCatalog
import com.pssst.util.OsrsXpTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val playerStateDao: PlayerStateDao,
    private val preferencesRepository: UserPreferencesRepository,
    private val itemManifestDao: ItemManifestDao? = null,
    private val application: Application? = null,
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

    private val goalConfigFlow: StateFlow<GoalConfig> = if (application != null) {
        GoalPrefs.flow(application)
    } else {
        MutableStateFlow(GoalConfig())
    }

    private val goalSearchQueryFlow = MutableStateFlow("")

    val goalSearchResultsFlow: StateFlow<List<ItemUiModel>> = goalSearchQueryFlow
        .map { query ->
            val clean = query.trim()
            if (clean.length < 2 || itemManifestDao == null) {
                emptyList()
            } else {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    runCatching {
                        val entities = itemManifestDao.searchItemsByName(clean, 10)
                        com.pssst.util.IconWishlist.recordPickerIds(
                            entities.filter { it.iconBase64.isNullOrBlank() }.map { it.id }
                        )
                        entities.map { entity ->
                            ItemUiModel(
                                id = entity.id,
                                quantity = 1,
                                name = entity.name,
                                iconBase64 = entity.iconBase64,
                                gePrice = entity.gePrice
                            )
                        }
                    }.getOrDefault(emptyList())
                }
            }
        }.stateIn(viewModelScope, sharingStarted, emptyList())

    val uiState: StateFlow<MainDashboardUiState> = combine(
        prefsFlow,
        playerStateDao.getLatestStateFlow(),
        filtersFlow,
        goalConfigFlow,
        goalSearchResultsFlow
    ) { prefs, entityList, filters, goalConfig, goalSearchResults ->
        val activeUuid = prefs.uuid?.trim().orEmpty()
        val hasUuid = activeUuid.isNotEmpty()
        val currentServerUrl = prefs.serverUrl ?: ""
        val entity = if (hasUuid) {
            entityList.find { it.uuid.equals(activeUuid, ignoreCase = true) }
        } else {
            null
        }
        if (entity == null) {
            MainDashboardUiState(
                uuid = activeUuid,
                rememberedUuid = prefs.rememberedUuid ?: "",
                serverUrl = currentServerUrl,
                hasConfiguredUuid = hasUuid,
                hasSyncedData = false,
                lastSyncStatus = prefs.lastSyncStatus,
                notificationsEnabled = prefs.notificationsEnabled,
                searchQuery = filters.searchQuery,
                potionSearchQuery = filters.potionSearchQuery,
                questSearchQuery = filters.questSearchQuery,
                questFilter = filters.questFilter,
                bankSortMode = filters.bankSortMode,
                goalConfig = goalConfig,
                goalItemSearchResults = goalSearchResults
            )
        } else {
            parseEntityToUiState(
                activeUuid,
                prefs.rememberedUuid ?: "",
                currentServerUrl,
                prefs.lastSyncStatus,
                prefs.notificationsEnabled,
                entity,
                filters,
                goalConfig,
                goalSearchResults
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

    fun onGoalSearchQueryChanged(query: String) {
        goalSearchQueryFlow.value = query
    }

    fun saveGoalConfig(config: GoalConfig) {
        config.targetItemId?.takeIf { it > 0 }?.let {
            com.pssst.util.IconWishlist.recordPickerIds(listOf(it))
        }
        if (application != null) {
            GoalPrefs.update(application) { config }
        } else if (goalConfigFlow is MutableStateFlow<GoalConfig>) {
            goalConfigFlow.value = config
        }
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
            val cleanUuid = uuid.trim()
            preferencesRepository.saveLastSyncStatus(null)
            preferencesRepository.saveSetup(serverUrl.trim(), cleanUuid)
            preferencesRepository.saveRememberTokenPreference(rememberUuid)
            val alreadyVerified = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching { playerStateDao.getStateByUuid(cleanUuid) != null }.getOrDefault(false)
            }
            if (!rememberUuid) {
                preferencesRepository.clearRememberedUuid()
            } else if (alreadyVerified) {
                preferencesRepository.saveRememberedUuid(cleanUuid)
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
            preferencesRepository.saveLastSyncStatus(null)
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
        filters: FilterState,
        goalConfig: GoalConfig,
        goalSearchResults: List<ItemUiModel>
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

        // Everything the plugin sends as structured sections lives in the raw payload; parse it once
        val payload: StateDataDto? = try {
            gson.fromJson(entity.rawJson, StateResponse::class.java)?.data
        } catch (e: Exception) {
            null
        }

        val haPricesMap = payload?.haPrices.orEmpty()
        val farmingItemIds = payload?.farming.orEmpty().map { if (it.itemId > 0) it.itemId else 6055 }
        val birdhouseItemIds = payload?.birdhouses.orEmpty().map { BirdHouseUiModel.itemIdForType(it.houseType) }

        val slayerEntry: SlayerEntryDto? = try {
            gson.fromJson(entity.slayerJson, SlayerEntryDto::class.java)
        } catch (e: Exception) {
            null
        } ?: payload?.slayer
        val slayerItemId = SlayerUiModel.itemIdForTask(slayerEntry?.taskName, slayerEntry?.itemId ?: 0)
        val slayerStoredItemId = SlayerUiModel.itemIdForTask(slayerEntry?.storedTaskName, slayerEntry?.storedItemId ?: 0)

        val topBarIconIds = listOf(257, 21512, 11738, 995)

        // Query manifest names and icons from Room on Dispatchers.IO
        val allIds = (
            bankEntries.map { it.id } +
            inventoryEntries.map { it.manifestId } +
            geOfferEntries.map { it.itemId } +
            potionEntries.map { it.itemId } +
            FarmingPaymentCatalog.ALL_PAYMENT_ITEM_IDS +
            DailyTasks.ALL_DAILY_ITEM_IDS +
            BirdHouseUiModel.ALL_BIRDHOUSE_ITEM_IDS +
            SkillUiModel.SKILL_ITEM_IDS.values +
            topBarIconIds +
            farmingItemIds +
            birdhouseItemIds +
            listOf(slayerItemId, slayerStoredItemId, SlayerUiModel.ENCHANTED_GEM_ITEM_ID) +
            listOfNotNull(goalConfig.targetItemId)
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

        val pricesAsOfMs: Long = itemManifestDao?.let { manifestDao ->
            runCatching {
                kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) { manifestDao.latestPriceTimestamp() }
            }.getOrDefault(0L)
        } ?: 0L

        // Map to ItemUiModel with name, base64 icon, gePrice, haPrice, and tab
        val bankItems: List<ItemUiModel> = bankEntries.map { entry ->
            val manifest = manifestMap[entry.id]
            ItemUiModel(
                id = entry.id,
                quantity = entry.quantity,
                name = manifest?.name ?: "",
                iconBase64 = manifest?.iconBase64,
                gePrice = manifest?.gePrice ?: 0L,
                haPrice = haPricesMap[entry.id.toString()] ?: 0L,
                tab = entry.tab
            )
        }

        // Parse Inventory (28 slots)
        val inventorySlots: List<InventorySlotUiModel> = (0 until 28).map { index ->
            val entry = inventoryEntries.getOrNull(index)
            if (entry != null && entry.id > 0 && entry.quantity > 0) {
                // A noted item has no name or price of its own; look up the unnoted item it stands for
                val manifest = manifestMap[entry.manifestId]
                InventorySlotUiModel(
                    index,
                    ItemUiModel(
                        id = entry.id,
                        quantity = entry.quantity,
                        name = manifest?.name?.let { if (entry.baseId != null) "$it (noted)" else it } ?: "",
                        iconBase64 = manifest?.iconBase64,
                        gePrice = manifest?.gePrice ?: 0L,
                        haPrice = haPricesMap[entry.manifestId.toString()] ?: 0L,
                        tab = null
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
                    itemName = manifest?.name ?: "Unknown item (${offer.itemId})",
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
            val skillItemId = SkillUiModel.SKILL_ITEM_IDS[skillName] ?: 0
            SkillUiModel(
                name = skillName,
                level = lvl,
                boostedLevel = boosted,
                xp = xp,
                itemId = skillItemId,
                iconBase64 = manifestMap[skillItemId]?.iconBase64
            )
        }

        // Parse Potion Storage
        val potionStorageItems: List<PotionStorageItemUiModel> = potionEntries.map { entry ->
            val manifest = manifestMap[entry.itemId]
            PotionStorageItemUiModel(
                itemId = entry.itemId,
                name = manifest?.name ?: "Unknown item (${entry.itemId})",
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
            // Noted items count under the unnoted id that the payment uses
            bankAndInvQuantities[item.manifestId] = (bankAndInvQuantities[item.manifestId] ?: 0) + item.quantity
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

        val varps: Map<String, Int> = payload?.varps.orEmpty()

        // Miscellania & Coffers, as RuneLite's Kingdom / BlastFurnace / NMZ plugins read them.
        // All are last-seen values updated when the player is near or opens the relevant coffer.
        val approvalPercent = ((varbits["72"] ?: 0) * 100) / 127
        val cofferAmount = (varbits["74"] ?: 0).toLong()
        val blastFurnaceCoffer = (varbits["5357"] ?: 0).toLong()
        val nmzCoffer = (varbits["3948"] ?: 0).toLong() * 1_000L
        val nmzRewardPoints = varps["1060"] ?: 0
        val pohServantUses = varbits["2191"] ?: 0
        val lmsCoffer = (varbits["5305"] ?: 0).toLong() * 1_000L
        val miscellania = MiscellaniaUiModel(
            approvalPercent = approvalPercent,
            cofferAmount = cofferAmount,
            blastFurnaceCoffer = blastFurnaceCoffer,
            nmzCoffer = nmzCoffer,
            nmzRewardPoints = nmzRewardPoints,
            pohServantUses = pohServantUses,
            lmsCoffer = lmsCoffer
        )

        // Slayer: the plugin resolves the task and area names from the game's tables
        val slayerStorageUnlocked = (slayerEntry?.storageUnlocked == true) || ((varbits["12442"] ?: 0) == 1)
        val slayer = SlayerUiModel(
            taskName = slayerEntry?.taskName?.takeIf { it.isNotBlank() } ?: "No Active Task",
            amount = slayerEntry?.amount ?: 0,
            taskLocation = slayerEntry?.taskLocation.orEmpty(),
            initialAmount = slayerEntry?.initialAmount ?: 0,
            points = slayerEntry?.points ?: 0,
            streak = slayerEntry?.streak ?: 0,
            itemId = slayerItemId,
            iconBase64 = manifestMap[slayerItemId]?.iconBase64 ?: manifestMap[SlayerUiModel.ENCHANTED_GEM_ITEM_ID]?.iconBase64,
            storageUnlocked = slayerStorageUnlocked,
            storedTaskName = slayerEntry?.storedTaskName.orEmpty(),
            storedAmount = slayerEntry?.storedAmount ?: 0,
            storedItemId = slayerStoredItemId,
            storedIconBase64 = manifestMap[slayerStoredItemId]?.iconBase64 ?: manifestMap[SlayerUiModel.ENCHANTED_GEM_ITEM_ID]?.iconBase64
        )

        // Farming patches come straight from the plugin's per-region observations (stored in the raw payload)
        val farmingPatches: List<FarmingPatchUiModel> = try {
            payload?.farming.orEmpty()
                .map { dto ->
                    val resolvedId = if (dto.itemId > 0) dto.itemId else 6055
                    FarmingPatchUiModel.from(dto, manifestMap[resolvedId]?.iconBase64)
                }
                .sortedWith(compareBy({ it.regionName }, { it.patchLabel }))
        } catch (e: Exception) {
            emptyList()
        }

        // Birdhouses come from the plugin's Fossil Island observations (stored in the raw payload)
        val birdHouses: List<BirdHouseUiModel> = try {
            payload?.birdhouses.orEmpty()
                .map { dto ->
                    val resolvedId = BirdHouseUiModel.itemIdForType(dto.houseType)
                    BirdHouseUiModel.from(dto, manifestMap[resolvedId]?.iconBase64)
                }
        } catch (e: Exception) {
            emptyList()
        }

        // Dailies follow RuneLite's Daily Tasks plugin (see DailyTasks); nothing in them is guessed
        val dailyTasks = DailyTasks.evaluate(varbits, varps) { itemId ->
            manifestMap[itemId]?.iconBase64
        }

        val topBarIcons = topBarIconIds.associateWith { manifestMap[it]?.iconBase64 }
        val isIronman = (varbits["1777"] ?: 0) > 0

        val goalTargetItem: ItemUiModel? = goalConfig.targetItemId?.let { targetId ->
            val manifest = manifestMap[targetId]
            ItemUiModel(
                id = targetId,
                quantity = 1,
                name = manifest?.name ?: "Unknown item ($targetId)",
                iconBase64 = manifest?.iconBase64,
                gePrice = manifest?.gePrice ?: 0L,
                haPrice = haPricesMap[targetId.toString()] ?: 0L
            )
        }

        return MainDashboardUiState(
            uuid = uuid,
            rememberedUuid = rememberedUuid,
            serverUrl = serverUrl,
            hasConfiguredUuid = uuid.isNotBlank(),
            hasSyncedData = true,
            lastUpdated = entity.updatedAt,
            lastSyncedAt = entity.lastSyncedAt,
            gameObservedAt = payload?.observedAt?.takeIf { it > 0 } ?: entity.updatedAt,
            bankShared = payload == null || payload.bank != null,
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
            topBarIcons = topBarIcons,
            notificationsEnabled = notificationsEnabled,
            pricesAsOfMs = pricesAsOfMs,
            isLoading = false,
            searchQuery = filters.searchQuery,
            potionSearchQuery = filters.potionSearchQuery,
            questSearchQuery = filters.questSearchQuery,
            questFilter = filters.questFilter,
            bankSortMode = filters.bankSortMode,
            isIronman = isIronman,
            goalConfig = goalConfig,
            goalTargetItem = goalTargetItem,
            goalItemSearchResults = goalSearchResults
        )
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AppDatabase.getDatabase(application)
                    val prefs = DataStoreUserPreferencesRepository(application)
                    return MainViewModel(db.playerStateDao(), prefs, db.itemManifestDao(), application = application) as T
                }
            }
    }
}
