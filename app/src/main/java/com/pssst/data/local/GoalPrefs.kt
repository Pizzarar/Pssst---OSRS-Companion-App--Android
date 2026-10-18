package com.pssst.data.local

import android.content.Context
import com.google.gson.Gson
import com.pssst.util.GoalMath
import com.pssst.util.GoalPriceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GoalConfig(
    val source: Int = GoalMath.WHOLE_BANK,
    val priceMode: GoalPriceMode? = null,
    val targetItemId: Int? = null,
    val targetGp: Long? = null
) {
    val isConfigured: Boolean get() = targetItemId != null || (targetGp != null && targetGp > 0L)
}

object GoalPrefs {
    private const val FILE = "pssst_goal_prefs"
    private const val KEY = "goal_config"
    private val gson = Gson()
    private var flow: MutableStateFlow<GoalConfig>? = null

    @Synchronized
    fun flow(context: Context): StateFlow<GoalConfig> {
        val current = flow
        if (current != null) return current
        val initial = read(context)
        val created = MutableStateFlow(initial)
        flow = created
        return created
    }

    @Synchronized
    fun update(context: Context, change: (GoalConfig) -> GoalConfig) {
        val f = flow(context) as MutableStateFlow<GoalConfig>
        val next = change(f.value)
        f.value = next
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putString(KEY, gson.toJson(next))
            .apply()
    }

    private fun read(context: Context): GoalConfig {
        val json = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY, null)
            ?: return GoalConfig()
        return try {
            gson.fromJson(json, GoalConfig::class.java) ?: GoalConfig()
        } catch (_: Exception) {
            GoalConfig()
        }
    }
}
