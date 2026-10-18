package com.pssst.util

import com.pssst.data.local.ItemManifestDao

/**
 * Tracks item IDs that picker UI flows need icons for, plus bounded background backfill state
 * so the phone requests high-value tradeable picker icons while syncing and deterministically
 * stops once enough icons are downloaded or all candidates have been attempted.
 */
object IconWishlist {
    /** Minimum GE value (100K GP) for background picker icon backfill candidates. */
    const val MIN_BACKFILL_GE_PRICE: Long = 100_000L

    /**
     * Maximum total icons in `item_manifest` before background backfill stops completely.
     * Explicit snapshot/picker/goal items are still requested above this threshold.
     */
    const val MAX_BACKFILL_TOTAL_ICONS: Int = 1_500

    private const val MAX_PICKER_WISHLIST_SIZE = 200
    private const val MAX_BACKFILL_QUERY_LIMIT = 500

    private val lock = Any()
    private val pickerWantedIds = LinkedHashSet<Int>()
    private val attemptedBackfillIds = HashSet<Int>()

    /** Records item IDs seen in a picker search or goal selection that currently lack an icon. */
    fun recordPickerIds(ids: Iterable<Int>) {
        synchronized(lock) {
            for (id in ids) {
                if (id <= 0) continue
                pickerWantedIds.remove(id)
                pickerWantedIds.add(id)
                while (pickerWantedIds.size > MAX_PICKER_WISHLIST_SIZE) {
                    val first = pickerWantedIds.iterator().next()
                    pickerWantedIds.remove(first)
                }
            }
        }
    }

    /** Removes IDs that now have icons stored in the manifest. */
    fun pruneResolved(resolvedIds: Collection<Int>) {
        if (resolvedIds.isEmpty()) return
        synchronized(lock) {
            pickerWantedIds.removeAll(resolvedIds.toSet())
        }
    }

    /** Returns a snapshot of picker-requested item IDs in insertion order. */
    fun snapshotPickerIds(): List<Int> {
        synchronized(lock) {
            return pickerWantedIds.toList()
        }
    }

    /**
     * Selects up to [neededSlots] high-value tradeable item IDs (`ge_price >= 100,000`) that still
     * lack an icon, stopping permanently once:
     * 1. `dao.countIcons() >= MAX_BACKFILL_TOTAL_ICONS`, OR
     * 2. Every qualifying item has either received an icon or already been requested once.
     */
    fun selectBackfillIds(
        dao: ItemManifestDao,
        neededSlots: Int,
        alreadyWanted: Set<Int>
    ): List<Int> {
        if (neededSlots <= 0) return emptyList()
        val totalIcons = dao.countIcons()
        if (totalIcons >= MAX_BACKFILL_TOTAL_ICONS) {
            return emptyList()
        }
        synchronized(lock) {
            val queryLimit = (neededSlots + attemptedBackfillIds.size + alreadyWanted.size)
                .coerceIn(neededSlots, MAX_BACKFILL_QUERY_LIMIT)
            val candidates = dao.highValueTradeableIdsWithoutIcon(MIN_BACKFILL_GE_PRICE, queryLimit)
            if (candidates.isEmpty()) return emptyList()
            val selected = ArrayList<Int>(neededSlots)
            for (id in candidates) {
                if (id > 0 && id !in alreadyWanted && id !in attemptedBackfillIds) {
                    selected.add(id)
                    attemptedBackfillIds.add(id)
                    if (selected.size >= neededSlots) break
                }
            }
            return selected
        }
    }

    /** Clears in-memory wishlist and backfill attempt tracking (for unit tests). */
    fun clearForTest() {
        synchronized(lock) {
            pickerWantedIds.clear()
            attemptedBackfillIds.clear()
        }
    }
}
