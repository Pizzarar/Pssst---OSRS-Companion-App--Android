package com.pssst.util

import com.pssst.ui.main.ItemUiModel
import java.util.Locale

enum class GoalPriceMode {
    GE,
    HA
}

data class GoalProgress(
    val current: Long,
    val target: Long,
    val fraction: Float,
    val remaining: Long,
    val itemCount: Int,
    val top: List<ItemUiModel>,
    val tabsKnown: Boolean
)

object GoalMath {
    const val WHOLE_BANK = -1

    fun unitValue(item: ItemUiModel, mode: GoalPriceMode): Long =
        when (item.id) {
            995 -> 1L
            13204 -> 1_000L
            else -> if (mode == GoalPriceMode.GE) item.gePrice else item.haPrice
        }

    fun stackValue(item: ItemUiModel, mode: GoalPriceMode): Long =
        item.quantity.toLong() * unitValue(item, mode)

    fun progress(
        bank: List<ItemUiModel>,
        source: Int,
        mode: GoalPriceMode,
        target: Long
    ): GoalProgress {
        val tabsKnown = bank.any { it.tab != null }
        val filtered = if (source == WHOLE_BANK) {
            bank
        } else {
            bank.filter { it.tab == source }
        }

        val current = filtered.sumOf { stackValue(it, mode) }
        val targetClamped = target.coerceAtLeast(0L)
        val fraction = if (targetClamped <= 0L) {
            if (current > 0) 1f else 0f
        } else {
            (current.toDouble() / targetClamped.toDouble()).toFloat().coerceIn(0f, 1f)
        }
        val remaining = (targetClamped - current).coerceAtLeast(0L)
        val itemCount = filtered.size
        val top = filtered
            .filter { stackValue(it, mode) > 0 }
            .sortedByDescending { stackValue(it, mode) }
            .take(4)

        return GoalProgress(
            current = current,
            target = targetClamped,
            fraction = fraction,
            remaining = remaining,
            itemCount = itemCount,
            top = top,
            tabsKnown = tabsKnown
        )
    }

    /**
     * Parses human GP expressions like "150m", "1.2b", "500k", "2,500,000", "1000000", "20.0M GP".
     * Returns null if blank, malformed, or <= 0.
     */
    fun parseGp(text: String): Long? {
        val clean = text.trim()
            .replace(",", "")
            .lowercase(Locale.ROOT)
            .removeSuffix("coins")
            .trim()
            .removeSuffix("gp")
            .trim()
        if (clean.isEmpty()) return null

        try {
            return when {
                clean.endsWith("b") -> {
                    val num = clean.dropLast(1).trim().toDoubleOrNull() ?: return null
                    if (num <= 0.0) null else (num * 1_000_000_000.0).toLong()
                }
                clean.endsWith("m") -> {
                    val num = clean.dropLast(1).trim().toDoubleOrNull() ?: return null
                    if (num <= 0.0) null else (num * 1_000_000.0).toLong()
                }
                clean.endsWith("k") -> {
                    val num = clean.dropLast(1).trim().toDoubleOrNull() ?: return null
                    if (num <= 0.0) null else (num * 1_000.0).toLong()
                }
                else -> {
                    val value = clean.toLongOrNull() ?: clean.toDoubleOrNull()?.toLong() ?: return null
                    if (value <= 0L) null else value
                }
            }
        } catch (_: Exception) {
            return null
        }
    }

    fun tabLabel(bank: List<ItemUiModel>, tab: Int): String {
        if (tab == WHOLE_BANK) return "Whole bank"
        if (tab == 0) return "Main tab"
        val firstItem = bank.firstOrNull { it.tab == tab }
        return if (firstItem != null && firstItem.name.isNotBlank()) {
            "Tab $tab · ${firstItem.name}"
        } else {
            "Tab $tab"
        }
    }
}
