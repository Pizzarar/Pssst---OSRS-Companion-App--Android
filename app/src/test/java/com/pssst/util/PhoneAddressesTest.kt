package com.pssst.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneAddressesTest {
    @Test
    fun homeWifiFirstThenMeshAndNothingElse() {
        val ranked = PhoneAddresses.rank(
            listOf("100.101.102.103", "8.8.8.8", "192.168.1.42", "10.0.0.7", "192.168.1.42", "172.40.0.1", "169.254.1.1")
        )
        assertEquals(listOf("192.168.1.42", "10.0.0.7", "100.101.102.103"), ranked.map { it.host })
        assertEquals(listOf(PhoneAddresses.LABEL_HOME, PhoneAddresses.LABEL_HOME, PhoneAddresses.LABEL_MESH), ranked.map { it.label })
    }

    @Test
    fun noAddressesMeansAnEmptyList() {
        assertEquals(emptyList<PhoneAddresses.Address>(), PhoneAddresses.rank(listOf("garbage", "1.2.3")))
    }
}
