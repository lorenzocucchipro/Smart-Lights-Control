package com.smartlights.control.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartlights.control.domain.model.Light
import com.smartlights.control.domain.repository.LightRepository
import com.smartlights.control.presentation.mvi.ErrorBanner
import com.smartlights.control.presentation.mvi.LightsIntent
import com.smartlights.control.presentation.mvi.LightsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LightsViewModel(
    private val repository: LightRepository,
    private val debounceMs: Long = 250L,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LightsUiState())
    val uiState: StateFlow<LightsUiState> = _uiState.asStateFlow()

    private val brightnessDebounceJobs: MutableMap<String, Job> = mutableMapOf()
    private val preDragSnapshots: MutableMap<String, Light> = mutableMapOf()
    private var errorDismissJob: Job? = null

    private fun emitErrorBanner() {
        errorDismissJob?.cancel()
        _uiState.update { it.copy(errorBanner = ErrorBanner()) }
        errorDismissJob = viewModelScope.launch {
            delay(3500L)
            _uiState.update { it.copy(errorBanner = null) }
        }
    }

    fun onIntent(intent: LightsIntent) {
        when (intent) {
            is LightsIntent.LoadLights -> handleLoadLights()
            is LightsIntent.SelectWidgetLight -> handleSelectWidgetLight(intent.lightId)
            is LightsIntent.TogglePower -> handleTogglePower(intent.lightId)
            is LightsIntent.UpdateBrightness -> handleUpdateBrightness(intent.lightId, intent.brightness)
            is LightsIntent.OpenDrawer -> handleOpenDrawer(intent.lightId)
            is LightsIntent.CloseDrawer -> _uiState.update { it.copy(drawerLightId = null) }
            is LightsIntent.DismissError -> {
                errorDismissJob?.cancel()
                _uiState.update { it.copy(errorBanner = null) }
            }
        }
    }

    private fun handleLoadLights() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getLights()
                .onSuccess { lights ->
                    _uiState.update { it.copy(lights = lights, isLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    emitErrorBanner()
                }
        }
    }

    private fun handleSelectWidgetLight(lightId: String) {
        _uiState.update { it.copy(activeWidgetLightId = lightId, errorBanner = null) }
    }

    private fun handleTogglePower(lightId: String) {
        brightnessDebounceJobs.remove(lightId)?.cancel()
        val current = _uiState.value.lights.find { it.id == lightId } ?: return
        val optimisticLight = current.withToggledPower()
        _uiState.update { state ->
            state.copy(lights = state.lights.map { if (it.id == lightId) optimisticLight else it })
        }
        viewModelScope.launch {
            repository.setPower(lightId, optimisticLight.isOn)
                .onSuccess { returned ->
                    _uiState.update { state ->
                        state.copy(lights = state.lights.map { if (it.id == lightId) returned else it })
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            lights = state.lights.map { if (it.id == lightId) current else it },
                        )
                    }
                    emitErrorBanner()
                }
        }
    }

    private fun handleUpdateBrightness(lightId: String, brightness: Int) {
        val current = _uiState.value.lights.find { it.id == lightId } ?: return
        if (!current.dimmable) return
        val dragStartSnapshot = preDragSnapshots.getOrPut(lightId) { current }
        val optimisticLight = current.withBrightness(brightness)
        _uiState.update { state ->
            state.copy(lights = state.lights.map { if (it.id == lightId) optimisticLight else it })
        }
        brightnessDebounceJobs[lightId]?.cancel()
        brightnessDebounceJobs[lightId] = viewModelScope.launch {
            delay(debounceMs)
            val clamped = brightness.coerceIn(0, 100)
            repository.setBrightness(lightId, clamped)
                .onSuccess { returned ->
                    preDragSnapshots.remove(lightId)
                    _uiState.update { state ->
                        state.copy(lights = state.lights.map { if (it.id == lightId) returned else it })
                    }
                }
                .onFailure {
                    preDragSnapshots.remove(lightId)
                    _uiState.update { state ->
                        state.copy(
                            lights = state.lights.map { if (it.id == lightId) dragStartSnapshot else it },
                        )
                    }
                    emitErrorBanner()
                }
        }
    }

    private fun handleOpenDrawer(lightId: String) {
        _uiState.update { it.copy(isOpeningDrawer = true) }
        viewModelScope.launch {
            repository.getLight(lightId)
                .onSuccess { light ->
                    _uiState.update { state ->
                        state.copy(
                            lights = state.lights.map { if (it.id == lightId) light else it },
                            drawerLightId = lightId,
                            isOpeningDrawer = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(drawerLightId = null, isOpeningDrawer = false)
                    }
                    emitErrorBanner()
                }
        }
    }

    companion object {
        fun provideFactory(repository: LightRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    LightsViewModel(repository) as T
            }
    }
}
