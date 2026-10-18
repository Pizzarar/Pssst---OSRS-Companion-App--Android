package com.pssst.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AddressChangeTest {
    private val home = PhoneAddresses.Address("192.168.1.57", PhoneAddresses.LABEL_HOME)
    private val tailscale = PhoneAddresses.Address("100.101.102.103", PhoneAddresses.LABEL_TAILSCALE)

    @Test
    fun aNewHomeAddressIsReported() {
        assertEquals("192.168.1.57", AddressChange.changedTo("192.168.1.42", listOf(home, tailscale)))
    }

    @Test
    fun theSameAddressIsNotAChange() {
        assertNull(AddressChange.changedTo("192.168.1.57", listOf(home)))
    }

    @Test
    fun nothingDeliveredYetOrDeliveredOverTailscaleOrTheEmulatorIsNotAChange() {
        assertNull(AddressChange.changedTo(null, listOf(home)))
        assertNull(AddressChange.changedTo("100.101.102.103", listOf(home)))
        assertNull(AddressChange.changedTo("127.0.0.1", listOf(home)))
    }

    @Test
    fun awayFromHomeWithNoHomeAddressThereIsNothingToSuggest() {
        assertNull(AddressChange.changedTo("192.168.1.42", listOf(tailscale)))
        assertNull(AddressChange.changedTo("192.168.1.42", emptyList()))
    }
}
