package com.pssst.data.network

import org.junit.Assert.assertEquals
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
}
