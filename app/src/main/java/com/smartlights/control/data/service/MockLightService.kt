package com.smartlights.control.data.service

import com.smartlights.control.domain.model.Light
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

class MockLightService(
    var failureRate: Double = 0.10,
    private val delayRangeMs: LongRange = 300L..800L,
    private val random: Random = Random.Default,
    private val delayFn: suspend (Long) -> Unit = { kotlinx.coroutines.delay(it) },
) : LightService {

    private val mutex = Mutex()

    private val lights: MutableMap<String, Light> = mutableMapOf(
        "bed_light" to Light(
            id = "bed_light",
            name = "Bed light",
            dimmable = true,
            isOn = true,
            brightness = 75,
            lastBrightness = 75,
        ),
        "all_lights" to Light(
            id = "all_lights",
            name = "All the lights in the house",
            dimmable = true,
            isOn = true,
            brightness = 100,
            lastBrightness = 100,
        ),
        "porch_light" to Light(
            id = "porch_light",
            name = "Porch light",
            dimmable = false,
            isOn = false,
            brightness = 100,
            lastBrightness = 100,
        ),
    )

    private suspend fun simulateLatency() {
        val delay = random.nextLong(delayRangeMs.first, delayRangeMs.last + 1)
        delayFn(delay)
    }

    private fun maybeFailure() {
        if (random.nextDouble() < failureRate) {
            throw LightServiceException("Simulated device error")
        }
    }

    override suspend fun list(): List<Light> {
        simulateLatency()
        maybeFailure()
        return mutex.withLock { lights.values.toList() }
    }

    override suspend fun get(id: String): Light {
        simulateLatency()
        maybeFailure()
        return mutex.withLock {
            lights[id] ?: throw LightServiceException("Light not found: $id")
        }
    }

    override suspend fun setPower(id: String, isOn: Boolean): Light {
        simulateLatency()
        maybeFailure()
        return mutex.withLock {
            val current = lights[id] ?: throw LightServiceException("Light not found: $id")
            val updated = current.withToggledPower().let {
                // Respect the explicit isOn value rather than always toggling
                if (it.isOn != isOn) current.copy(
                    isOn = isOn,
                    brightness = if (!isOn) 0 else {
                        if (current.lastBrightness > 0) current.lastBrightness else 75
                    }
                ) else it
            }
            lights[id] = updated
            updated
        }
    }

    override suspend fun setBrightness(id: String, value: Int): Light {
        simulateLatency()
        maybeFailure()
        return mutex.withLock {
            val current = lights[id] ?: throw LightServiceException("Light not found: $id")
            val updated = current.withBrightness(value)
            lights[id] = updated
            updated
        }
    }
}
