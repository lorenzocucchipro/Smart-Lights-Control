package com.smartlights.control.di

import com.smartlights.control.data.repository.LightRepositoryImpl
import com.smartlights.control.data.service.MockLightService
import com.smartlights.control.domain.repository.LightRepository

object AppContainer {
    val mockLightService = MockLightService()
    val lightRepository: LightRepository = LightRepositoryImpl(mockLightService)
}
