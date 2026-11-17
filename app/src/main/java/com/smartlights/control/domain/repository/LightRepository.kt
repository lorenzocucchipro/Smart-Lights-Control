package com.smartlights.control.domain.repository

import com.smartlights.control.domain.model.Light

interface LightRepository {
    suspend fun getLights(): Result<List<Light>>
    suspend fun getLight(id: String): Result<Light>
    suspend fun setPower(id: String, isOn: Boolean): Result<Light>
    suspend fun setBrightness(id: String, value: Int): Result<Light>
}
