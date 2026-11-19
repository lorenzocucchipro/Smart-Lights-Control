package com.smartlights.control.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LightTest {

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private fun dimmableOn(brightness: Int = 75, lastBrightness: Int = 75) = Light(
        id = "light_1",
        name = "Test Light",
        dimmable = true,
        isOn = true,
        brightness = brightness,
        lastBrightness = lastBrightness,
    )

    private fun dimmableOff(lastBrightness: Int = 75) = Light(
        id = "light_1",
        name = "Test Light",
        dimmable = true,
        isOn = false,
        brightness = 0,
        lastBrightness = lastBrightness,
    )

    private fun nonDimmable(isOn: Boolean = false) = Light(
        id = "light_2",
        name = "Porch",
        dimmable = false,
        isOn = isOn,
        brightness = 0,
        lastBrightness = 0,
    )

    // ---------------------------------------------------------------------------
    // withToggledPower — dimmable
    // ---------------------------------------------------------------------------

    @Test
    fun `dimmable on-to-off sets isOn false brightness 0 preserves lastBrightness`() {
        val light = dimmableOn(brightness = 75, lastBrightness = 75)
        val toggled = light.withToggledPower()
        assertFalse(toggled.isOn)
        assertEquals(0, toggled.brightness)
        assertEquals(75, toggled.lastBrightness)
    }

    @Test
    fun `dimmable off-to-on restores lastBrightness`() {
        val light = dimmableOff(lastBrightness = 60)
        val toggled = light.withToggledPower()
        assertTrue(toggled.isOn)
        assertEquals(60, toggled.brightness)
        assertEquals(60, toggled.lastBrightness)
    }

    @Test
    fun `dimmable off-to-on with invalid lastBrightness defaults to 75`() {
        val light = dimmableOff(lastBrightness = 0)
        val toggled = light.withToggledPower()
        assertTrue(toggled.isOn)
        assertEquals(75, toggled.brightness)
    }

    @Test
    fun `dimmable toggle off then on round-trips to original lastBrightness`() {
        val original = dimmableOn(brightness = 50, lastBrightness = 50)
        val off = original.withToggledPower()
        val on = off.withToggledPower()
        assertTrue(on.isOn)
        assertEquals(50, on.brightness)
        assertEquals(50, on.lastBrightness)
    }

    // ---------------------------------------------------------------------------
    // withBrightness — dimmable
    // ---------------------------------------------------------------------------

    @Test
    fun `brightness clamped to 0 when below zero`() {
        val light = dimmableOn()
        val updated = light.withBrightness(-10)
        assertEquals(0, updated.brightness)
        assertFalse(updated.isOn)
    }

    @Test
    fun `brightness clamped to 100 when above 100`() {
        val light = dimmableOn()
        val updated = light.withBrightness(150)
        assertEquals(100, updated.brightness)
        assertTrue(updated.isOn)
    }

    @Test
    fun `setting brightness to 0 turns light off and preserves lastBrightness`() {
        val light = dimmableOn(brightness = 60, lastBrightness = 60)
        val updated = light.withBrightness(0)
        assertFalse(updated.isOn)
        assertEquals(0, updated.brightness)
        assertEquals(60, updated.lastBrightness)
    }

    @Test
    fun `setting brightness to positive value turns light on and updates lastBrightness`() {
        val light = dimmableOff(lastBrightness = 50)
        val updated = light.withBrightness(80)
        assertTrue(updated.isOn)
        assertEquals(80, updated.brightness)
        assertEquals(80, updated.lastBrightness)
    }

    @Test
    fun `setting brightness to 1 turns light on`() {
        val light = dimmableOff()
        val updated = light.withBrightness(1)
        assertTrue(updated.isOn)
        assertEquals(1, updated.brightness)
    }

    @Test
    fun `setting brightness to 100 turns light on`() {
        val light = dimmableOff()
        val updated = light.withBrightness(100)
        assertTrue(updated.isOn)
        assertEquals(100, updated.brightness)
    }

    // ---------------------------------------------------------------------------
    // Non-dimmable
    // ---------------------------------------------------------------------------

    @Test
    fun `non-dimmable toggles isOn from off to on`() {
        val light = nonDimmable(isOn = false)
        val toggled = light.withToggledPower()
        assertTrue(toggled.isOn)
    }

    @Test
    fun `non-dimmable toggles isOn from on to off`() {
        val light = nonDimmable(isOn = true)
        val toggled = light.withToggledPower()
        assertFalse(toggled.isOn)
    }

    @Test
    fun `non-dimmable ignores brightness changes`() {
        val light = nonDimmable(isOn = true)
        val updated = light.withBrightness(50)
        assertEquals(light, updated)
    }

    @Test
    fun `non-dimmable withBrightness does not alter any field`() {
        val light = nonDimmable(isOn = false)
        val updated = light.withBrightness(100)
        assertEquals(light, updated)
    }
}
