package com.pssst.util

import com.pssst.data.network.model.FarmingEntryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FarmingTimingTest {
    private val tick = 20 * 60_000L

    // Ranarr at stage 1 of 5 (20 min ticks), seen 7 minutes into a tick
    private val observedAt = 833 * tick + 7 * 60_000L
    private val ranarr = FarmingEntryDto(
        regionId = 11062, varbitId = 4774, observedAt = observedAt, cropName = "Ranarr",
        cropState = "GROWING", stage = 1, stages = 5, tickMinutes = 20
    )

    @Test
    fun doneAt_countsRemainingTicksFromTheStartOfTheObservedTick() {
        // stages - 1 - stage = 3 ticks after the start of the tick the stage was seen in
        assertEquals(833 * tick + 3 * tick, FarmingTiming.doneAtMs(ranarr))
    }

    @Test
    fun doneAt_isAlreadyPastWhenObservedAtTheLastStage() {
        val last = ranarr.copy(stage = 4)
        assertEquals(833 * tick, FarmingTiming.doneAtMs(last))
    }

    @Test
    fun doneAt_isNullWhenNothingIsAdvancing() {
        assertNull(FarmingTiming.doneAtMs(ranarr.copy(cropState = "DISEASED", tickMinutes = 0)))
        assertNull(FarmingTiming.doneAtMs(ranarr.copy(cropState = null)))
        assertNull(FarmingTiming.doneAtMs(ranarr.copy(stage = 9)))
    }

    @Test
    fun stageAt_advancesOnTheGridNotFromTheObservationTime() {
        // 14 minutes after observing crosses the next 20-minute boundary (observed 7 min in) -> +1
        assertEquals(1, FarmingTiming.stageAt(ranarr, observedAt + 12 * 60_000L))
        assertEquals(2, FarmingTiming.stageAt(ranarr, observedAt + 14 * 60_000L))
        assertEquals(3, FarmingTiming.stageAt(ranarr, observedAt + 34 * 60_000L))
    }

    @Test
    fun stageAt_isCappedAtTheLastStageAndNeverGoesBackwards() {
        assertEquals(4, FarmingTiming.stageAt(ranarr, observedAt + 24 * 60 * 60_000L))
        assertEquals(1, FarmingTiming.stageAt(ranarr, observedAt - 60 * 60_000L))
        assertEquals(2, FarmingTiming.stageAt(ranarr.copy(tickMinutes = 0, stage = 2), observedAt + 99 * tick))
    }
}
