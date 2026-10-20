package com.pssst.worker

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log

/**
 * Decides when a change in the phone's connection is worth one sync: a new, validated network (joining the home Wi-Fi,
 * switching networks), not the constant signal-strength updates of one network. Rate-limited so flapping Wi-Fi
 * cannot cause a burst. Pure logic, so it is tested without Android.
 */
class NetworkChangeGate(
    private val minIntervalMs: Long = 60_000L,
    private val now: () -> Long = System::currentTimeMillis
) {
    private var seeded = false
    private var lastNetwork: Any? = null
    private var lastFiredAt: Long? = null

    /** @return true when this report should trigger a sync */
    @Synchronized
    fun onCapabilities(network: Any, validated: Boolean): Boolean {
        if (!validated) return false
        val first = !seeded
        seeded = true
        val sameNetwork = network == lastNetwork
        lastNetwork = network
        // The first report is the network the app started on; the app-open sync already covers it
        if (first || sameNetwork) return false
        val t = now()
        val last = lastFiredAt
        if (last != null && t - last < minIntervalMs) return false
        lastFiredAt = t
        return true
    }

    @Synchronized
    fun onLost() {
        lastNetwork = null
    }
}

/** Syncs when the phone gets onto a (new) network while the app process is alive. With the process gone, the periodic job is the catch-up. */
object NetworkSyncTrigger {
    private const val TAG = "NetworkSyncTrigger"

    fun register(app: Application) {
        try {
            val manager = app.getSystemService(ConnectivityManager::class.java) ?: return
            val gate = NetworkChangeGate()
            manager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                    val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    if (gate.onCapabilities(network, validated)) {
                        SyncScheduler.syncNow(app)
                    }
                }

                override fun onLost(network: Network) {
                    gate.onLost()
                }
            })
        } catch (e: Exception) {
            // Never let a connectivity quirk stop the app from starting
            Log.w(TAG, "Could not watch network changes", e)
        }
    }
}
