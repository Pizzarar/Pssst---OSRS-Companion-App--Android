package com.pssst.receiver

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Remembers which of this phone's addresses RuneLite last delivered to, so the app can tell when it changed. */
object ReceiverPrefs {
    private const val FILE = "pssst_receiver"
    private const val KEY_LAST_DELIVERED_TO = "last_delivered_to"
    private var flow: MutableStateFlow<String?>? = null

    @Synchronized
    fun lastDeliveredTo(context: Context): StateFlow<String?> =
        flow ?: MutableStateFlow(
            context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_LAST_DELIVERED_TO, null)
        ).also { flow = it }

    @Synchronized
    fun saveLastDeliveredTo(context: Context, address: String) {
        val state = lastDeliveredTo(context) as MutableStateFlow
        if (state.value == address) return
        state.value = address
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_DELIVERED_TO, address).apply()
    }
}
