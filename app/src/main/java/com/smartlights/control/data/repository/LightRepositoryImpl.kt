package com.smartlights.control.data.repository

import com.smartlights.control.data.service.LightService
import com.smartlights.control.domain.model.Light
import com.smartlights.control.domain.repository.LightRepository

class LightRepositoryImpl(
    private val service: LightService,
    private val logError: (String, Throwable) -> Unit = { message, error ->
        runCatching { android.util.Log.w("LightRepository", message, error) }
    },
) : LightRepository {

    override suspend fun getLights(): Result<List<Light>> =
        runCatching { service.list() }
            .onFailure { logError("getLights failed", it) }

    override suspend fun getLight(id: String): Result<Light> =
        runCatching { service.get(id) }
            .onFailure { logError("getLight(id=$id) failed", it) }

    override suspend fun setPower(id: String, isOn: Boolean): Result<Light> =
        runCatching { service.setPower(id, isOn) }
            .onFailure { logError("setPower(id=$id, isOn=$isOn) failed", it) }

    override suspend fun setBrightness(id: String, value: Int): Result<Light> =
        runCatching { service.setBrightness(id, value) }
            .onFailure { logError("setBrightness(id=$id, value=$value) failed", it) }
}
