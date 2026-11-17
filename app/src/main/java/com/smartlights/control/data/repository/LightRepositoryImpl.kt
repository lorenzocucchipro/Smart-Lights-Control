package com.smartlights.control.data.repository

import com.smartlights.control.data.service.LightService
import com.smartlights.control.domain.model.Light
import com.smartlights.control.domain.repository.LightRepository

class LightRepositoryImpl(
    private val service: LightService,
) : LightRepository {

    override suspend fun getLights(): Result<List<Light>> =
        runCatching { service.list() }

    override suspend fun getLight(id: String): Result<Light> =
        runCatching { service.get(id) }

    override suspend fun setPower(id: String, isOn: Boolean): Result<Light> =
        runCatching { service.setPower(id, isOn) }

    override suspend fun setBrightness(id: String, value: Int): Result<Light> =
        runCatching { service.setBrightness(id, value) }
}
