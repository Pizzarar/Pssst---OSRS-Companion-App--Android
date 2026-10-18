package com.pssst.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ListLayoutTest {
    private val dailies = listOf("herbs", "staves", "sand", "flax")
    private fun arrange(layout: ListLayout) = ListLayout.arrange(dailies, { it }, layout)

    @Test
    fun withNoChangesTheDefaultOrderStands() {
        assertEquals(dailies, arrange(ListLayout()).visible)
    }

    @Test
    fun movingDownAndUpReordersAndSticks() {
        var layout = ListLayout().move("sand", 1, dailies)
        assertEquals(listOf("herbs", "staves", "flax", "sand"), arrange(layout).visible)

        layout = layout.move("flax", -1, arrange(layout).visible)
        assertEquals(listOf("herbs", "flax", "staves", "sand"), arrange(layout).visible)
    }

    @Test
    fun movingPastTheEndsDoesNothing() {
        val layout = ListLayout()
        assertEquals(layout, layout.move("herbs", -1, dailies))
        assertEquals(layout, layout.move("flax", 1, dailies))
        assertEquals(layout, layout.move("missing", 1, dailies))
    }

    @Test
    fun hiddenRowsLeaveTheListAndComeBackInTheirPlace() {
        val hidden = ListLayout().hide("sand")
        assertEquals(listOf("herbs", "staves", "flax"), arrange(hidden).visible)
        assertEquals(listOf("sand"), arrange(hidden).hidden)

        assertEquals(dailies, arrange(hidden.show("sand")).visible)
    }

    @Test
    fun aRowThatDidNotExistWhenArrangedKeepsItsDefaultPlaceAfterTheRest() {
        val layout = ListLayout().move("flax", -3, dailies) // flax first
        val withNew = ListLayout.arrange(dailies + "battlestaves", { it }, layout)
        assertEquals(listOf("flax", "herbs", "staves", "sand", "battlestaves"), withNew.visible)
    }

    @Test
    fun rowsThatNoLongerExistAreIgnored() {
        val layout = ListLayout(order = listOf("gone", "sand", "herbs"), hidden = setOf("gone2"))
        assertEquals(listOf("sand", "herbs", "staves", "flax"), arrange(layout).visible)
    }
}
