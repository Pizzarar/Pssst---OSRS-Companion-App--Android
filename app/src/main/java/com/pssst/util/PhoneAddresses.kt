package com.pssst.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address

/** The addresses to type into RuneLite's "Phone Address": this phone's home Wi-Fi address and its Tailscale address. */
object PhoneAddresses {
    data class Address(val host: String, val label: String)

    const val LABEL_HOME = "Home Wi-Fi"
    const val LABEL_TAILSCALE = "Tailscale"

    const val LABEL_EMULATOR = "Emulator: on the PC run adb forward tcp:8789 tcp:8789 first"

    /** What to show the player: the real addresses, or on an emulator the forwarded loopback address. */
    fun forDisplay(context: Context): List<Address> =
        if (isEmulator()) listOf(Address("127.0.0.1", LABEL_EMULATOR)) else current(context)

    /** The emulator's own address (10.0.2.x) can't be reached from the PC; it needs adb forward instead. */
    fun isEmulator(): Boolean {
        val fingerprint = android.os.Build.FINGERPRINT.orEmpty()
        val product = android.os.Build.PRODUCT.orEmpty()
        return fingerprint.startsWith("generic") || fingerprint.contains("emulator") ||
            product.contains("sdk_gphone") || product.contains("emulator") || product.startsWith("sdk")
    }

    /** Reads the phone's current addresses. Cheap; call it when the screen shows. */
    fun current(context: Context): List<Address> = try {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val found = mutableListOf<String>()
        @Suppress("DEPRECATION") // allNetworks is the simplest way to see Wi-Fi and the VPN together
        for (network in manager?.allNetworks.orEmpty()) {
            val caps = manager?.getNetworkCapabilities(network) ?: continue
            if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                !caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) &&
                !caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            ) continue
            manager.getLinkProperties(network)?.linkAddresses?.forEach { link ->
                (link.address as? Inet4Address)?.hostAddress?.let { found += it }
            }
        }
        rank(found)
    } catch (e: Exception) {
        emptyList()
    }

    /** Home-network IPv4 first (192.168, then 10.x, then 172.16-31), then Tailscale's 100.64.0.0/10. Others are dropped. */
    fun rank(hosts: List<String>): List<Address> =
        hosts.distinct().mapNotNull { host ->
            val o = host.split('.').mapNotNull { it.toIntOrNull() }
            if (o.size != 4) return@mapNotNull null
            when {
                o[0] == 192 && o[1] == 168 -> Triple(0, host, LABEL_HOME)
                o[0] == 10 -> Triple(1, host, LABEL_HOME)
                o[0] == 172 && o[1] in 16..31 -> Triple(2, host, LABEL_HOME)
                o[0] == 100 && o[1] in 64..127 -> Triple(3, host, LABEL_TAILSCALE)
                else -> null
            }
        }.sortedBy { it.first }.map { Address(it.second, it.third) }
}
