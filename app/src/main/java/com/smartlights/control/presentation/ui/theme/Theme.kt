package com.smartlights.control.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SmartLightsDarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    secondary = WidgetFillBlue,
    onSecondary = TextPrimary,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = ErrorCrimson,
    onError = TextPrimary,
)

@Composable
fun SmartLightsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartLightsDarkColorScheme,
        typography = SmartLightsTypography,
        content = content,
    )
}
