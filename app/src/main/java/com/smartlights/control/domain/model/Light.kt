package com.smartlights.control.domain.model

data class Light(
    val id: String,
    val name: String,
    val dimmable: Boolean,
    val isOn: Boolean,
    val brightness: Int,       // 0..100; 0 when off, ignored when non-dimmable
    val lastBrightness: Int,   // 1..100, remembers last non-zero brightness for restoration
) {
    /**
     * Returns a copy of this light with power toggled, applying brightness-memory rules.
     */
    fun withToggledPower(): Light {
        return if (!dimmable) {
            copy(isOn = !isOn)
        } else if (isOn) {
            // Turning off: preserve lastBrightness, set brightness = 0
            copy(isOn = false, brightness = 0)
        } else {
            // Turning on: restore lastBrightness (default 75 if invalid)
            val restored = if (lastBrightness > 0) lastBrightness else 75
            copy(isOn = true, brightness = restored)
        }
    }

    /**
     * Returns a copy of this light with brightness updated, clamped to 0..100.
     * Non-dimmable lights are returned unchanged.
     */
    fun withBrightness(value: Int): Light {
        if (!dimmable) return this
        val clamped = value.coerceIn(0, 100)
        return if (clamped == 0) {
            copy(isOn = false, brightness = 0)
            // lastBrightness is preserved
        } else {
            copy(isOn = true, brightness = clamped, lastBrightness = clamped)
        }
    }
}
