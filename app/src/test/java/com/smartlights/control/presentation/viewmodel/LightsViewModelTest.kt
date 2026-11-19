package com.smartlights.control.presentation.viewmodel

import com.smartlights.control.data.repository.LightRepositoryImpl
import com.smartlights.control.data.service.LightService
import com.smartlights.control.data.service.MockLightService
import com.smartlights.control.domain.model.Light
import com.smartlights.control.domain.repository.LightRepository
import com.smartlights.control.presentation.mvi.LightsIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    val testDispatcher = StandardTestDispatcher()
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

@OptIn(ExperimentalCoroutinesApi::class)
class LightsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun makeService(failureRate: Double = 0.0) = MockLightService(
        failureRate = failureRate,
        delayFn = {},
    )

    private fun makeViewModel(service: LightService = makeService()): LightsViewModel {
        val repo: LightRepository = LightRepositoryImpl(service)
        return LightsViewModel(repository = repo, debounceMs = 250L)
    }

    private val bedLightId = "bed_light"

    private suspend fun TestScope.loadLights(vm: LightsViewModel) {
        vm.onIntent(LightsIntent.LoadLights)
        advanceUntilIdle()
    }

    @Test
    fun `optimistic power toggle succeeds and retains toggled state`() = runTest {
        val vm = makeViewModel()
        loadLights(vm)
        assertTrue(vm.uiState.value.lights.find { it.id == bedLightId }!!.isOn)
        vm.onIntent(LightsIntent.TogglePower(bedLightId))
        assertTrue(!vm.uiState.value.lights.find { it.id == bedLightId }!!.isOn) // optimistic
        advanceUntilIdle()
        assertTrue(!vm.uiState.value.lights.find { it.id == bedLightId }!!.isOn)
        assertNull(vm.uiState.value.errorBanner)
    }

    @Test
    fun `optimistic power toggle rolls back and emits ErrorBanner on failure`() = runTest {
        // Load with success first, then set failureRate to always fail
        val service = makeService(failureRate = 0.0)
        val vm = makeViewModel(service = service)
        loadLights(vm)
        val before = vm.uiState.value.lights.find { it.id == bedLightId }!!
        service.failureRate = 1.0
        vm.onIntent(LightsIntent.TogglePower(bedLightId))
        advanceUntilIdle()
        val after = vm.uiState.value.lights.find { it.id == bedLightId }!!
        assertEquals(before.isOn, after.isOn)
        assertNotNull(vm.uiState.value.errorBanner)
        assertEquals("Something went wrong", vm.uiState.value.errorBanner!!.title)
        assertEquals("Try again later", vm.uiState.value.errorBanner!!.subtitle)
    }

    @Test
    fun `multiple rapid UpdateBrightness updates UI immediately but debounces service call`() = runTest {
        var serviceCallCount = 0
        var lastServiceValue = -1
        val baseService = makeService()
        val trackingService = object : LightService {
            override suspend fun list() = baseService.list()
            override suspend fun get(id: String) = baseService.get(id)
            override suspend fun setPower(id: String, isOn: Boolean) = baseService.setPower(id, isOn)
            override suspend fun setBrightness(id: String, value: Int): Light {
                serviceCallCount++; lastServiceValue = value
                return baseService.setBrightness(id, value)
            }
        }
        val vm = makeViewModel(trackingService)
        loadLights(vm)

        vm.onIntent(LightsIntent.UpdateBrightness(bedLightId, 30))
        vm.onIntent(LightsIntent.UpdateBrightness(bedLightId, 50))
        vm.onIntent(LightsIntent.UpdateBrightness(bedLightId, 80))

        assertEquals(80, vm.uiState.value.lights.find { it.id == bedLightId }!!.brightness)
        assertEquals(0, serviceCallCount)

        advanceTimeBy(300L)
        advanceUntilIdle()

        assertEquals(1, serviceCallCount)
        assertEquals(80, lastServiceValue)
    }

    @Test
    fun `brightness failure rolls back to pre-drag snapshot and emits ErrorBanner`() = runTest {
        val service = makeService(failureRate = 0.0)
        val vm = makeViewModel(service = service)
        loadLights(vm)
        val snapshot = vm.uiState.value.lights.find { it.id == bedLightId }!!
        service.failureRate = 1.0
        vm.onIntent(LightsIntent.UpdateBrightness(bedLightId, 40))
        advanceTimeBy(300L)
        advanceUntilIdle()
        val rolledBack = vm.uiState.value.lights.find { it.id == bedLightId }!!
        assertEquals(snapshot.brightness, rolledBack.brightness)
        assertNotNull(vm.uiState.value.errorBanner)
    }

    @Test
    fun `OpenDrawer on failure keeps drawerLightId null and emits ErrorBanner`() = runTest {
        val service = makeService(failureRate = 0.0)
        val vm = makeViewModel(service = service)
        loadLights(vm)
        service.failureRate = 1.0
        vm.onIntent(LightsIntent.OpenDrawer(bedLightId))
        advanceUntilIdle()
        assertNull(vm.uiState.value.drawerLightId)
        assertNotNull(vm.uiState.value.errorBanner)
    }

    @Test
    fun `state updated while drawer is open is reflected on activeWidgetLight after CloseDrawer`() = runTest {
        val vm = makeViewModel()
        loadLights(vm)
        vm.onIntent(LightsIntent.OpenDrawer(bedLightId))
        advanceUntilIdle()
        assertNotNull(vm.uiState.value.drawerLightId)
        vm.onIntent(LightsIntent.TogglePower(bedLightId))
        advanceUntilIdle()
        vm.onIntent(LightsIntent.CloseDrawer)
        val widgetLight = vm.uiState.value.activeWidgetLight
        assertNotNull(widgetLight)
        assertTrue(!widgetLight!!.isOn)
    }
}
