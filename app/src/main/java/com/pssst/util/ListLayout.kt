package com.pssst.util

/**
 * How the player arranged one list: their own order (keys first in this order; anything new keeps its default place
 * after them) and the rows they hid. Pure, so it is unit-tested; [com.pssst.data.local.ListLayouts] stores it.
 */
data class ListLayout(
    val order: List<String> = emptyList(),
    val hidden: Set<String> = emptySet()
) {
    /** [all] is every row in the player's order (shown and hidden), for the edit view. */
    data class Arranged<T>(val visible: List<T>, val hidden: List<T>, val all: List<T> = visible + hidden)

    /** The player dragged rows into this order (every key, shown and hidden). */
    fun withOrder(keys: List<String>) = copy(order = keys)

    fun setShown(key: String, shown: Boolean) = if (shown) show(key) else hide(key)

    /** Moves [key] one place up (delta -1) or down (+1) among the rows currently shown. */
    fun move(key: String, delta: Int, visibleKeys: List<String>): ListLayout {
        val list = visibleKeys.toMutableList()
        val from = list.indexOf(key)
        val to = from + delta
        if (from < 0 || to !in list.indices) return this
        list.removeAt(from)
        list.add(to, key)
        // Hidden rows keep their place in the saved order, after the shown ones
        return copy(order = list + order.filter { it !in list })
    }

    fun hide(key: String) = copy(hidden = hidden + key)

    fun show(key: String) = copy(hidden = hidden - key)

    companion object {
        fun <T> arrange(items: List<T>, key: (T) -> String, layout: ListLayout): Arranged<T> {
            val byKey = items.associateBy(key)
            val ordered = layout.order.mapNotNull { byKey[it] } + items.filter { key(it) !in layout.order }
            return Arranged(
                visible = ordered.filter { key(it) !in layout.hidden },
                hidden = ordered.filter { key(it) in layout.hidden },
                all = ordered
            )
        }
    }
}
