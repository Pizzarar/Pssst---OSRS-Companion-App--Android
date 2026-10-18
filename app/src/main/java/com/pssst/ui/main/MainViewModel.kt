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
            inventoryEntries.map { it.manifestId } +
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

        val pricesAsOfMs: Long = itemManifestDao?.let { manifestDao ->
            runCatching {
                kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) { manifestDao.latestPriceTimestamp() }
            }.getOrDefault(0L)
        } ?: 0L

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
                // A noted item has no name or price of its own; look up the unnoted item it stands for
                val manifest = manifestMap[entry.manifestId]
                InventorySlotUiModel(
                    index,
                    ItemUiModel(
                        id = entry.id,
                        quantity = entry.quantity,
                        name = manifest?.name?.let { if (entry.baseId != null) "$it (noted)" else it } ?: "",
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

        // Miscellania, as RuneLite's Kingdom plugin reads it: approval is out of 127, the coffer is the raw coin count.
        // Both are last-seen values (the game only updates them near the kingdom).
        val approvalPercent = ((varbits["72"] ?: 0) * 100) / 127
        val cofferAmount = (varbits["74"] ?: 0).toLong()
        val miscellania = MiscellaniaUiModel(approvalPercent, cofferAmount)

        // Everything the plugin sends as structured sections lives in the raw payload; parse it once
        val payload: StateDataDto? = try {
            gson.fromJson(entity.rawJson, StateResponse::class.java)?.data
        } catch (e: Exception) {
            null
        }

        // Slayer: the plugin resolves the task and area names from the game's tables
        val slayerEntry: SlayerEntryDto? = try {
            gson.fromJson(entity.slayerJson, SlayerEntryDto::class.java)
        } catch (e: Exception) {
            null
        }
        val slayer = SlayerUiModel(
            taskName = slayerEntry?.taskName?.takeIf { it.isNotBlank() } ?: "No Active Task",
            amount = slayerEntry?.amount ?: 0,
            taskLocation = slayerEntry?.taskLocation.orEmpty(),
            initialAmount = slayerEntry?.initialAmount ?: 0,
            points = slayerEntry?.points ?: 0,
            streak = slayerEntry?.streak ?: 0
        )

        val varps: Map<String, Int> = payload?.varps.orEmpty()

        // Farming patches come straight from the plugin's per-region observations (stored in the raw payload)
        val farmingPatches: List<FarmingPatchUiModel> = try {
            payload?.farming.orEmpty()
                .map(FarmingPatchUiModel::from)
                .sortedWith(compareBy({ it.regionName }, { it.patchLabel }))
        } catch (e: Exception) {
            emptyList()
        }

        // Birdhouses come from the plugin's Fossil Island observations (stored in the raw payload)
        val birdHouses: List<BirdHouseUiModel> = try {
            payload?.birdhouses.orEmpty()
                .map(BirdHouseUiModel::from)
        } catch (e: Exception) {
            emptyList()
        }

        // Dailies follow RuneLite's Daily Tasks plugin (see DailyTasks); nothing in them is guessed
        val dailyTasks = DailyTasks.evaluate(varbits, varps)

        return MainDashboardUiState(
            uuid = uuid,
            rememberedUuid = rememberedUuid,
            serverUrl = serverUrl,
            hasConfiguredUuid = uuid.isNotBlank(),
            lastUpdated = entity.updatedAt,
            lastSyncedAt = entity.lastSyncedAt,
            gameObservedAt = payload?.observedAt?.takeIf { it > 0 } ?: entity.updatedAt,
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
            pricesAsOfMs = pricesAsOfMs,
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
