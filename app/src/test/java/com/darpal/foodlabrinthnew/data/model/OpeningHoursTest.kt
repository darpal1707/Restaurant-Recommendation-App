package com.darpal.foodlabrinthnew.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OpeningHoursTest {

    @Test
    fun `parses day and time rules for display`() {
        assertEquals(
            listOf(
                OpeningHours("Mon–Fri", "11:00 – 22:00"),
                OpeningHours("Sat, Sun", "12:00 – 15:00, 17:00 – 23:00"),
                OpeningHours("Holidays", "Closed"),
            ),
            OpeningHours.parse("Mo-Fr 11:00-22:00; Sa,Su 12:00-15:00,17:00-23:00; PH off"),
        )
    }

    @Test
    fun `special and unknown values`() {
        assertEquals(listOf(OpeningHours("Every day", "Open 24 hours")), OpeningHours.parse("24/7"))
        assertEquals(listOf(OpeningHours("", "sunrise-sunset")), OpeningHours.parse("sunrise-sunset"))
        assertEquals(emptyList<OpeningHours>(), OpeningHours.parse(null))
    }

    @Test
    fun `isOpenAt checks time ranges on any day`() {
        val hours = "Mo-Fr 11:00-14:30,17:00-22:00; Su off"
        assertEquals(true, OpeningHours.isOpenAt(hours, 12 * 60 + 30))
        assertEquals(false, OpeningHours.isOpenAt(hours, 8 * 60 + 30))
        assertEquals(false, OpeningHours.isOpenAt(hours, 22 * 60))
    }

    @Test
    fun `isOpenAt handles midnight, 24-7 and unknown values`() {
        assertEquals(true, OpeningHours.isOpenAt("Fr-Sa 18:00-02:00", 23 * 60 + 30))
        assertEquals(true, OpeningHours.isOpenAt("Fr-Sa 18:00-02:00", 60))
        assertEquals(false, OpeningHours.isOpenAt("Fr-Sa 18:00-02:00", 12 * 60))
        assertEquals(true, OpeningHours.isOpenAt("Mo-Su 00:00-24:00", 23 * 60 + 59))
        assertEquals(true, OpeningHours.isOpenAt("24/7", 3 * 60))
        assertNull(OpeningHours.isOpenAt("by appointment", 12 * 60))
        assertNull(OpeningHours.isOpenAt(null, 12 * 60))
    }
}
