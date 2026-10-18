package com.pssst.worker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkChangeGateTest {

    private var clock = 1_000_000L
    private val gate = NetworkChangeGate(minIntervalMs = 60_000L, now = { clock })

    @Test
    fun theNetworkTheAppStartedOnDoesNotTriggerASecondSync() {
        assertFalse(gate.onCapabilities("home-wifi", validated = true))
    }

    @Test
    fun joiningANewValidatedNetworkTriggersOneSync() {
        gate.onCapabilities("mobile", validated = true) // the starting network
        clock += 120_000

        assertTrue(gate.onCapabilities("home-wifi", validated = true))
    }

    @Test
    fun signalStrengthUpdatesOnTheSameNetworkNeverTrigger() {
        gate.onCapabilities("mobile", validated = true)
        clock += 120_000
        assertTrue(gate.onCapabilities("home-wifi", validated = true))

        repeat(20) {
            clock += 120_000
            assertFalse(gate.onCapabilities("home-wifi", validated = true))
        }
    }

    @Test
    fun aNetworkThatIsNotValidatedYetWaitsUntilItIs() {
        gate.onCapabilities("mobile", validated = true)
        clock += 120_000

        assertFalse(gate.onCapabilities("home-wifi", validated = false))
        assertTrue(gate.onCapabilities("home-wifi", validated = true))
    }

    @Test
    fun flappingWifiCannotCauseABurst() {
        gate.onCapabilities("mobile", validated = true)
        clock += 120_000
        assertTrue(gate.onCapabilities("home-wifi", validated = true))

        gate.onLost()
        clock += 10_000
        assertFalse("a reconnect 10 s later is inside the minimum interval", gate.onCapabilities("home-wifi", validated = true))

        gate.onLost()
        clock += 120_000
        assertTrue(gate.onCapabilities("home-wifi", validated = true))
    }

    @Test
    fun comingBackAfterALostNetworkTriggers() {
        gate.onCapabilities("home-wifi", validated = true)
        gate.onLost()
        clock += 120_000

        assertTrue(gate.onCapabilities("home-wifi", validated = true))
    }
}
