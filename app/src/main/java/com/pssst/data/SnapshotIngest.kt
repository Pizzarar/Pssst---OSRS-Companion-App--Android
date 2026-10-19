package com.pssst.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.model.StateResponse
import com.pssst.notification.NotificationHelper
import com.pssst.util.StateDiffAnalyzer
import com.pssst.worker.SyncScheduler
import com.pssst.worker.SyncWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Saves a snapshot, however it arrived: sent by the RuneLite plugin to this phone ([com.pssst.receiver.PhoneReceiver])
 * or fetched from the player's own server ([SyncWorker]). One place for the rules: never replace newer data with older,
 * completion alerts, timed reminders, the Room row, prices, and the status line.
 */
class SnapshotIngest(
    private val context: Context,
    private val database: AppDatabase,
    private val preferences: UserPreferencesRepository,
    private val now: () -> Long = System::currentTimeMillis
) {
    enum class Outcome { SAVED, OLDER }

    private val gson = Gson()

    suspend fun ingest(uuid: String, stateResponse: StateResponse): Outcome {
        val dao = database.playerStateDao()
        val previousEntity = dao.getStateByUuid(uuid)
        val currentMs = now()
        val maxAllowedMs = currentMs + MAX_CLOCK_SKEW_MS

        // Icons are always saved even if the snapshot timestamp itself has not advanced
        applyIcons(stateResponse.data?.icons)

        // Never replace what is saved with something the game observed earlier (a stale or wrong PC).
        // Clamp far-future timestamps (> now + 5m) so a skewed PC clock or bad payload cannot permanently lock out sync.
        val rawNewObservedAt = stateResponse.data?.observedAt ?: 0L
        val newObservedAt = when {
            rawNewObservedAt <= 0L -> 0L
            rawNewObservedAt > maxAllowedMs -> currentMs
            else -> rawNewObservedAt
        }
        val rawSavedObservedAt = observedAtOf(previousEntity)
        val savedObservedAt = if (rawSavedObservedAt > maxAllowedMs) currentMs else rawSavedObservedAt
        if (newObservedAt > 0 && savedObservedAt > newObservedAt) {
            Log.w(TAG, "Ignoring a snapshot the game observed before the one already saved")
            preferences.saveSyncFailures(0)
            preferences.saveLastSyncStatus(SyncWorker.STATUS_OLDER)
            return Outcome.OLDER
        }

        // Prices first, so the screens that re-read on the next change see them with the snapshot
        val data = stateResponse.data
        val rawUpdatedAtMs = stateResponse.updatedAt.let { if (it in 1 until 100_000_000_000L) it * 1000 else it }
        val updatedAtMs = when {
            rawUpdatedAtMs <= 0L -> 0L
            rawUpdatedAtMs > maxAllowedMs -> currentMs
            else -> rawUpdatedAtMs
        }
        val prices = data?.prices.orEmpty().mapNotNull { (id, price) ->
            val itemId = id.toIntOrNull()
            if (itemId != null && itemId > 0 && price in 0L..MAX_VALID_GP) itemId to price else null
        }.toMap()
        if (prices.isNotEmpty()) {
            // Stamped with when the game saw them, not when they arrived
            val pricesAsOf = if (newObservedAt > 0) newObservedAt else if (updatedAtMs > 0) updatedAtMs else currentMs
            database.itemManifestDao().applyPrices(prices, pricesAsOf)
        }

        val notificationsEnabled = preferences.getNotificationsEnabled()

        // Detect completed timers and notify if enabled
        if (notificationsEnabled) {
            val alerts = StateDiffAnalyzer.findCompletedRunsOrPatches(previousEntity, stateResponse)
            alerts.forEachIndexed { index, alert ->
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = SyncWorker.NOTIFICATION_BASE_ID + index,
                    title = alert.title,
                    message = alert.message
                )
            }
        }

        // Keep timed reminders (growing farming patches, birdhouses) in step with the latest observations
        SyncScheduler.syncReminders(
            context,
            stateResponse.data?.farming.orEmpty(),
            stateResponse.data?.birdhouses.orEmpty(),
            notificationsEnabled
        )

        // The icons are in the manifest now; keeping them in every saved snapshot would only bloat it
        val sanitizedData = data?.copy(observedAt = newObservedAt, icons = null)
        val rawJson = gson.toJson(stateResponse.copy(data = sanitizedData, updatedAt = updatedAtMs))
        dao.upsert(
            PlayerStateEntity(
                uuid,
                updatedAtMs,
                rawJson,
                gson.toJson(data?.varbits ?: emptyMap<String, Int>()),
                data?.slayer?.let { gson.toJson(it) } ?: "{}",
                gson.toJson(data?.inventory ?: emptyList<Any>()),
                // Bank sharing off in the plugin: no bank in the payload, and none kept here either
                gson.toJson(data?.bank ?: emptyList<Any>()),
                currentMs,
                gson.toJson(data?.geOffers ?: emptyList<Any>()),
                gson.toJson(data?.skills ?: emptyList<Any>()),
                gson.toJson(data?.potionStorage ?: emptyList<Any>()),
                gson.toJson(data?.quests ?: emptyMap<String, String>())
            )
        )

        val invCount = data?.inventory?.size ?: 0
        val bankCount = data?.bank?.size ?: 0
        val slayerTaskName = data?.slayer?.taskName?.takeIf { it.isNotBlank() } ?: "none"
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(currentMs))
        preferences.saveSyncFailures(0)
        if (preferences.getRememberTokenPreference() && uuid.isNotBlank()) {
            preferences.saveRememberedUuid(uuid.trim())
        }
        preferences.saveLastSyncStatus("OK $timeStr inv=$invCount bank=$bankCount slayer=$slayerTaskName")
        Log.i(TAG, "Saved snapshot: inventory=$invCount bank=$bankCount slayer=$slayerTaskName")
        return Outcome.SAVED
    }

    private fun applyIcons(icons: Map<String, com.pssst.data.network.model.IconDto>?) {
        val entities = icons.orEmpty().entries.take(MAX_ICON_REQUESTS).mapNotNull { (id, icon) ->
            val itemId = id.toIntOrNull()?.takeIf { it > 0 } ?: return@mapNotNull null
            val png = icon.png?.trim()?.takeIf { isAcceptableIconBase64(it) } ?: return@mapNotNull null
            val cleanName = icon.name
                ?.filter { !it.isISOControl() }
                ?.trim()
                ?.take(MAX_ITEM_NAME_LENGTH)
                ?.takeIf { it.isNotBlank() }
                ?: "Item $itemId"
            com.pssst.data.local.ItemManifestEntity(itemId, cleanName, png, 0L)
        }
        if (entities.isNotEmpty()) database.itemManifestDao().applyIcons(entities)
    }

    /**
     * The item ids this snapshot (or the app itself) shows that have no icon yet, for the reply to RuneLite, which
     * draws them from its own client and sends them with the next update. At most [MAX_ICON_REQUESTS].
     * Small/immediately visible sets (goal target, active picker search results, skill icons, inventory, GE, timers)
     * are requested first so a large bank never starves them; any remaining capacity is used for bounded high-value
     * tradeable backfill that stops once enough icons are downloaded or all candidates have been attempted.
     */
    fun missingIcons(stateResponse: StateResponse): List<Int> {
        val data = stateResponse.data
        val wanted = LinkedHashSet<Int>()
        runCatching { com.pssst.data.local.GoalPrefs.flow(context).value.targetItemId }
            .getOrNull()
            ?.takeIf { it > 0 }
            ?.let { wanted += it }
        wanted += com.pssst.util.IconWishlist.snapshotPickerIds()
        data?.inventory.orEmpty().forEach { wanted += it.manifestId }
        data?.geOffers.orEmpty().forEach { if (it.itemId > 0) wanted += it.itemId }
        wanted += com.pssst.ui.main.SkillUiModel.SKILL_ITEM_IDS.values
        wanted += listOf(257, 21512, 11738, 995)
        data?.slayer?.let { slayer ->
            wanted += com.pssst.ui.main.SlayerUiModel.itemIdForTask(slayer.taskName, slayer.itemId)
            wanted += com.pssst.ui.main.SlayerUiModel.itemIdForTask(slayer.storedTaskName, slayer.storedItemId)
            wanted += com.pssst.ui.main.SlayerUiModel.ENCHANTED_GEM_ITEM_ID
        }
        wanted += com.pssst.util.FarmingPaymentCatalog.ALL_PAYMENT_ITEM_IDS
        wanted += com.pssst.util.DailyTasks.ALL_DAILY_ITEM_IDS
        wanted += com.pssst.ui.main.BirdHouseUiModel.ALL_BIRDHOUSE_ITEM_IDS
        data?.birdhouses.orEmpty().forEach { wanted += com.pssst.ui.main.BirdHouseUiModel.itemIdForType(it.houseType) }
        data?.farming.orEmpty().forEach { wanted += if (it.itemId > 0) it.itemId else 6055 }
        data?.potionStorage.orEmpty().forEach { wanted += it.itemId }
        data?.bank.orEmpty().forEach { wanted += it.id }

        val dao = database.itemManifestDao()
        val have = HashSet<Int>()
        wanted.toList().chunked(500).forEach { have += dao.idsWithIcon(it) }
        com.pssst.util.IconWishlist.pruneResolved(have)

        val primaryMissing = wanted.filter { it > 0 && it !in have }.take(MAX_ICON_REQUESTS)
        val remainingSlots = MAX_ICON_REQUESTS - primaryMissing.size
        if (remainingSlots <= 0) {
            return primaryMissing
        }
        val backfill = com.pssst.util.IconWishlist.selectBackfillIds(
            dao = dao,
            neededSlots = remainingSlots,
            alreadyWanted = primaryMissing.toSet()
        )
        return primaryMissing + backfill
    }

    private fun observedAtOf(entity: PlayerStateEntity?): Long =
        runCatching { gson.fromJson(entity?.rawJson, StateResponse::class.java)?.data?.observedAt }.getOrNull() ?: 0L

    companion object {
        private const val TAG = "SnapshotIngest"
        const val MAX_ICON_REQUESTS = 100
        const val MAX_ICON_DIMENSION = 128
        const val MAX_CLOCK_SKEW_MS = 5 * 60_000L
        const val MAX_VALID_GP = 2_147_483_647_000L
        const val MAX_ICON_BASE64 = 16 * 1024
        private const val MAX_ITEM_NAME_LENGTH = 128
        private val PNG_SIGNATURE = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)

        /**
         * Validates that a base64 icon string is within size bounds and, if it carries a PNG header,
         * that its IHDR dimensions are within [1..MAX_ICON_DIMENSION] to prevent decompression bombs.
         */
        internal fun isAcceptableIconBase64(base64: String): Boolean {
            if (base64.isBlank() || base64.length > MAX_ICON_BASE64) return false
            val bytes = try {
                java.util.Base64.getMimeDecoder().decode(base64)
            } catch (_: IllegalArgumentException) {
                return true
            }
            if (hasPngSignature(bytes)) {
                return hasSafePngDimensions(bytes)
            }
            return true
        }

        internal fun hasPngSignature(bytes: ByteArray): Boolean {
            if (bytes.size < PNG_SIGNATURE.size) return false
            for (i in PNG_SIGNATURE.indices) {
                if (bytes[i] != PNG_SIGNATURE[i]) return false
            }
            return true
        }

        internal fun hasSafePngDimensions(bytes: ByteArray): Boolean {
            if (bytes.size < 24) return false
            // IHDR chunk type at bytes 12..15 ("IHDR")
            if (bytes[12] != 'I'.code.toByte() ||
                bytes[13] != 'H'.code.toByte() ||
                bytes[14] != 'D'.code.toByte() ||
                bytes[15] != 'R'.code.toByte()
            ) {
                return false
            }
            val width = readBigEndianInt(bytes, 16)
            val height = readBigEndianInt(bytes, 20)
            return width in 1..MAX_ICON_DIMENSION && height in 1..MAX_ICON_DIMENSION
        }

        private fun readBigEndianInt(bytes: ByteArray, offset: Int): Int =
            ((bytes[offset].toInt() and 0xFF) shl 24) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
                (bytes[offset + 3].toInt() and 0xFF)
    }
}
