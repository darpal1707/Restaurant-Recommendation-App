package com.darpal.foodlabrinthnew.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class MotionMathTest {

    @Test
    fun `stiffness matches a unit-mass spring with the given response`() {
        // (2π / 0.4)² ≈ 246.74
        assertEquals(246.74f, MotionMath.stiffness(0.4f), 0.01f)
        // A quicker response needs a stiffer spring.
        assertTrue(MotionMath.stiffness(0.25f) > MotionMath.stiffness(0.4f))
    }

    @Test
    fun `projection follows scroll deceleration`() {
        // 1000 px/s at 0.998 → 1 * 0.998 / 0.002 = 499 px further on.
        assertEquals(499f, MotionMath.project(1000f), 0.01f)
        assertEquals(-499f, MotionMath.project(-1000f), 0.01f)
        assertEquals(0f, MotionMath.project(0f), 0f)
    }

    @Test
    fun `rubber band resists more the further it is pulled`() {
        val height = 200f
        val small = MotionMath.rubberBand(-20f, height)
        val large = MotionMath.rubberBand(-400f, height)
        // Keeps the direction, never moves as far as the finger.
        assertTrue(small < 0f && large < 0f)
        assertTrue(abs(small) < 20f)
        assertTrue(abs(large) < 400f)
        // Diminishing returns: 20x the pull gives far less than 20x the movement.
        assertTrue(abs(large) < abs(small) * 20f)
        // Never travels further than its own size, however far you pull.
        assertTrue(abs(MotionMath.rubberBand(-100_000f, height)) < height)
    }

    @Test
    fun `rubber band with no size does not move`() {
        assertEquals(0f, MotionMath.rubberBand(-50f, 0f), 0f)
    }
}
