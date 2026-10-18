package com.pssst.data.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StateApiServiceTest {

    @Test
    fun normalizeBaseUrl_rewritesZeroAddressToEmulatorLoopback() {
        assertEquals(
            "http://10.0.2.2:8787",
            DynamicStateApiService.normalizeBaseUrl("http://0.0.0.0:8787")
        )
        assertEquals(
            "http://10.0.2.2:8787/state",
            DynamicStateApiService.buildStateUrl("http://0.0.0.0:8787")
        )
    }

    @Test
    fun normalizeBaseUrl_rewritesLocalhostToEmulatorLoopback() {
        assertEquals(
            "http://10.0.2.2:8787/api",
            DynamicStateApiService.normalizeBaseUrl("http://localhost:8787/api")
        )
        assertEquals(
            "http://10.0.2.2:8787/api/state",
            DynamicStateApiService.buildStateUrl("http://localhost:8787/api")
        )
    }

    @Test
    fun normalizeBaseUrl_rewrites127001ToEmulatorLoopback() {
        assertEquals(
            "http://10.0.2.2:8787",
            DynamicStateApiService.normalizeBaseUrl("http://127.0.0.1:8787")
        )
        assertEquals(
            "http://10.0.2.2:8787/state",
            DynamicStateApiService.buildStateUrl("127.0.0.1:8787")
        )
    }

    @Test
    fun normalizeBaseUrl_preserves10022() {
        assertEquals(
            "http://10.0.2.2:8787",
            DynamicStateApiService.normalizeBaseUrl("http://10.0.2.2:8787")
        )
        assertEquals(
            "http://10.0.2.2:8787/state",
            DynamicStateApiService.buildStateUrl("http://10.0.2.2:8787/")
        )
    }

    @Test
    fun normalizeBaseUrl_preservesHttpsHost() {
        assertEquals(
            "https://example.com/",
            DynamicStateApiService.normalizeBaseUrl("https://example.com/")
        )
        assertEquals(
            "https://example.com/state",
            DynamicStateApiService.buildStateUrl("https://example.com/")
        )
    }

    @Test
    fun isHomeNetworkHost_coversHomeRangesAndTailscaleButNotTheInternet() {
        for (home in listOf("192.168.1.20", "10.0.0.7", "172.16.0.1", "172.31.255.254", "100.64.0.1", "100.127.1.1", "169.254.1.1", "pc.local")) {
            assertTrue(home, DynamicStateApiService.isHomeNetworkHost(home))
        }
        for (other in listOf("8.8.8.8", "172.32.0.1", "172.15.0.1", "100.63.0.1", "100.128.0.1", "pssst.workers.dev", "example.com", "1.2.3", "300.1.1.1")) {
            assertFalse(other, DynamicStateApiService.isHomeNetworkHost(other))
        }
    }
}
