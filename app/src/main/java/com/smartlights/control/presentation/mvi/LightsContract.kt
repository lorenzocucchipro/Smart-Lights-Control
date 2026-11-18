package com.smartlights.control.presentation.mvi

import com.smartlights.control.domain.model.Light

data class ErrorBanner(
    val title: String = "Something went wrong",
    val subtitle: String = "Try again later",
    val id: Long = System.nanoTime(),
)

data class LightsUiState(
    val lights: List<Light> = emptyList(),
    val activeWidgetLightId: String = "bed_light",
    val drawerLightId: String? = null,
    val isLoading: Boolean = true,
    val isOpeningDrawer: Boolean = false,
    val errorBanner: ErrorBanner? = null,
) {
    val activeWidgetLight: Light?
        get() = lights.find { it.id == activeWidgetLightId } ?: lights.firstOrNull()

    val drawerLight: Light?
        get() = drawerLightId?.let { id -> lights.find { it.id == id } }
}

sealed interface LightsIntent {
    data object LoadLights : LightsIntent
    data class SelectWidgetLight(val lightId: String) : LightsIntent
    data class TogglePower(val lightId: String) : LightsIntent
    data class UpdateBrightness(val lightId: String, val brightness: Int) : LightsIntent
    data class OpenDrawer(val lightId: String) : LightsIntent
    data object CloseDrawer : LightsIntent
    data object DismissError : LightsIntent
}
