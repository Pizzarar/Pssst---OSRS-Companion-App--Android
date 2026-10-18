package com.pssst.data.local

import android.content.Context
import com.google.gson.Gson
import com.pssst.util.ListLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Keeps each list's arrangement (order, hidden rows) on the phone. Small JSON per list in SharedPreferences. */
object ListLayouts {
    private const val FILE = "pssst_list_layouts"
    private val gson = Gson()
    private val flows = mutableMapOf<String, MutableStateFlow<ListLayout>>()

    private data class Stored(val order: List<String>? = null, val hidden: List<String>? = null)

    @Synchronized
    fun flow(context: Context, listId: String): StateFlow<ListLayout> =
        flows.getOrPut(listId) { MutableStateFlow(read(context, listId)) }

    @Synchronized
    fun update(context: Context, listId: String, change: (ListLayout) -> ListLayout) {
        val state = flows.getOrPut(listId) { MutableStateFlow(read(context, listId)) }
        val next = change(state.value)
        state.value = next
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putString(listId, gson.toJson(Stored(next.order, next.hidden.toList())))
            .apply()
    }

    private fun read(context: Context, listId: String): ListLayout {
        val json = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(listId, null)
            ?: return ListLayout()
        return try {
            val stored = gson.fromJson(json, Stored::class.java)
            ListLayout(stored?.order.orEmpty(), stored?.hidden.orEmpty().toSet())
        } catch (e: Exception) {
            ListLayout()
        }
    }
}
