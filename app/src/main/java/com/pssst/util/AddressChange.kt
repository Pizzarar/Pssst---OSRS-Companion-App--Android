package com.pssst.util

/** Spots when this phone's home Wi-Fi address is no longer the one RuneLite was sending to. */
object AddressChange {
    /**
     * @return the phone's new home address when RuneLite last delivered to a different home address that this phone no
     * longer has; null otherwise (nothing delivered yet, delivered over a mesh VPN or the emulator, or unchanged)
     */
    fun changedTo(lastDeliveredTo: String?, current: List<PhoneAddresses.Address>): String? {
        if (lastDeliveredTo == null) return null
        val wasHome = PhoneAddresses.rank(listOf(lastDeliveredTo)).firstOrNull()?.label == PhoneAddresses.LABEL_HOME
        if (!wasHome) return null
        if (current.any { it.host == lastDeliveredTo }) return null
        return current.firstOrNull { it.label == PhoneAddresses.LABEL_HOME }?.host
    }
}
