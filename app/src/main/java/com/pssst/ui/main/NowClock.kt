package com.pssst.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay

/** Wall-clock time that refreshes every [intervalMs], so time-dependent status (e.g. a crop finishing) updates on screen. */
@Composable
fun rememberNowMs(intervalMs: Long = 30_000L): State<Long> =
    produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            delay(intervalMs)
            value = System.currentTimeMillis()
        }
    }
