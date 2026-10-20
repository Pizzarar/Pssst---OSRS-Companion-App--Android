package com.pssst.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AddressChangeTest {
    private val home = PhoneAddresses.Address("192.168.1.57", PhoneAddresses.LABEL_HOME)
    private val mesh = PhoneAddresses.Address("100.101.102.103", PhoneAddresses.LABEL_MESH)

    @Test
    fun aNewHomeAddressIsReported() {
        assertEquals("192.168.1.57", AddressChange.changedTo("192.168.1.42", listOf(home, mesh)))
    }

    @Test
    fun theSameAddressIsNotAChange() {
        assertNull(AddressChange.changedTo("192.168.1.57", listOf(home)))
    }

    @Test
    fun nothingDeliveredYetOrDeliveredOverMeshOrTheEmulatorIsNotAChange() {
        assertNull(AddressChange.changedTo(null, listOf(home)))
        assertNull(AddressChange.changedTo("100.101.102.103", listOf(home)))
        assertNull(AddressChange.changedTo("127.0.0.1", listOf(home)))
    }

    @Test
    fun awayFromHomeWithNoHomeAddressThereIsNothingToSuggest() {
        assertNull(AddressChange.changedTo("192.168.1.42", listOf(mesh)))
        assertNull(AddressChange.changedTo("192.168.1.42", emptyList()))
    }
}
