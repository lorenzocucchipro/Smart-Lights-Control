package com.smartlights.control.data.service

import com.smartlights.control.domain.model.Light

class LightServiceException(message: String) : Exception(message)

interface LightService {
    suspend fun list(): List<Light>
    suspend fun get(id: String): Light
    suspend fun setPower(id: String, isOn: Boolean): Light
    suspend fun setBrightness(id: String, value: Int): Light
}
