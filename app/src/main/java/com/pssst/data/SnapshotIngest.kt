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

        // Never replace what is saved with something the game observed earlier (a stale or wrong PC)
        val newObservedAt = stateResponse.data?.observedAt ?: 0L
        val savedObservedAt = observedAtOf(previousEntity)
        if (newObservedAt > 0 && savedObservedAt > newObservedAt) {
            Log.w(TAG, "Ignoring a snapshot the game observed before the one already saved")
            preferences.saveSyncFailures(0)
            preferences.saveLastSyncStatus(SyncWorker.STATUS_OLDER)
            return Outcome.OLDER
        }

        // Icons and prices first, so the screens that re-read on the next change see them with the snapshot
        applyIcons(stateResponse.data?.icons)
        val data = stateResponse.data
        val updatedAtMs = stateResponse.updatedAt.let { if (it in 1 until 100_000_000_000L) it * 1000 else it }
        val prices = data?.prices.orEmpty().mapNotNull { (id, price) -> id.toIntOrNull()?.let { it to price } }.toMap()
        if (prices.isNotEmpty()) {
            // Stamped with when the game saw them, not when they arrived
            val pricesAsOf = if (newObservedAt > 0) newObservedAt else if (updatedAtMs > 0) updatedAtMs else now()
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
        val rawJson = gson.toJson(stateResponse.copy(data = data?.copy(icons = null)))
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
                now(),
                gson.toJson(data?.geOffers ?: emptyList<Any>()),
                gson.toJson(data?.skills ?: emptyList<Any>()),
                gson.toJson(data?.potionStorage ?: emptyList<Any>()),
                gson.toJson(data?.quests ?: emptyMap<String, String>())
            )
        )

        val invCount = data?.inventory?.size ?: 0
        val bankCount = data?.bank?.size ?: 0
        val slayerTaskName = data?.slayer?.taskName?.takeIf { it.isNotBlank() } ?: "none"
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(now()))
        preferences.saveSyncFailures(0)
        preferences.saveLastSyncStatus("OK $timeStr inv=$invCount bank=$bankCount slayer=$slayerTaskName")
        Log.i(TAG, "Saved snapshot: inventory=$invCount bank=$bankCount slayer=$slayerTaskName")
        return Outcome.SAVED
    }

    private fun applyIcons(icons: Map<String, com.pssst.data.network.model.IconDto>?) {
        val entities = icons.orEmpty().mapNotNull { (id, icon) ->
            val itemId = id.toIntOrNull() ?: return@mapNotNull null
            val png = icon.png?.takeIf { it.isNotBlank() && it.length <= MAX_ICON_BASE64 } ?: return@mapNotNull null
            com.pssst.data.local.ItemManifestEntity(itemId, icon.name?.takeIf { it.isNotBlank() } ?: "Item $itemId", png, 0L)
        }
        if (entities.isNotEmpty()) database.itemManifestDao().applyIcons(entities)
    }

    /**
     * The item ids this snapshot (or the app itself) shows that have no icon yet, for the reply to RuneLite, which
     * draws them from its own client and sends them with the next update. At most [MAX_ICON_REQUESTS].
     */
    fun missingIcons(stateResponse: StateResponse): List<Int> {
        val data = stateResponse.data
        val wanted = LinkedHashSet<Int>()
        data?.bank.orEmpty().forEach { wanted += it.id }
        data?.inventory.orEmpty().forEach { wanted += it.manifestId }
        data?.potionStorage.orEmpty().forEach { wanted += it.itemId }
        data?.geOffers.orEmpty().forEach { if (it.itemId > 0) wanted += it.itemId }
        wanted += com.pssst.util.FarmingPaymentCatalog.ALL_PAYMENT_ITEM_IDS
        wanted += com.pssst.util.IconWishlist.IDS
        val have = HashSet<Int>()
        wanted.toList().chunked(500).forEach { have += database.itemManifestDao().idsWithIcon(it) }
        return wanted.filter { it > 0 && it !in have }.take(MAX_ICON_REQUESTS)
    }

    private fun observedAtOf(entity: PlayerStateEntity?): Long =
        runCatching { gson.fromJson(entity?.rawJson, StateResponse::class.java)?.data?.observedAt }.getOrNull() ?: 0L

    companion object {
        private const val TAG = "SnapshotIngest"
        const val MAX_ICON_REQUESTS = 100
        private const val MAX_ICON_BASE64 = 16 * 1024
    }
}
