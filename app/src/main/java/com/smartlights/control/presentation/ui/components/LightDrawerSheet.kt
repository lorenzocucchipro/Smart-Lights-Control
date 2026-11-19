package com.smartlights.control.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlights.control.R
import com.smartlights.control.domain.model.Light
import com.smartlights.control.presentation.mvi.ErrorBanner
import com.smartlights.control.presentation.ui.theme.AccentBlue
import com.smartlights.control.presentation.ui.theme.BulbAmber
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme
import com.smartlights.control.presentation.ui.theme.TextPrimary
import com.smartlights.control.presentation.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
private fun BulbCard(light: Light) {
    val glowAlpha = if (light.isOn) {
        if (light.dimmable) light.brightness / 100f else 1f
    } else 0f
    Surface(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E242B),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (glowAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .drawBehind {
                            val brush = ShaderBrush(
                                RadialGradientShader(
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = size.minDimension / 2,
                                    colors = listOf(
                                        BulbAmber.copy(alpha = 0.45f * glowAlpha),
                                        Color.White.copy(alpha = 0.08f * glowAlpha),
                                        Color.Transparent,
                                    ),
                                    colorStops = listOf(0f, 0.5f, 1f),
                                )
                            )
                            drawCircle(brush = brush, radius = size.minDimension / 2)
                        },
                )
            }
            Image(
                painter = painterResource(id = if (light.isOn) R.drawable.bulb else R.drawable.bulb_off),
                contentDescription = if (light.isOn) "Light on" else "Light off",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(140.dp),
            )
        }
    }
}

@Composable
private fun DimmableSection(light: Light, onBrightnessChange: (Int) -> Unit) {
    val brightnessValue = if (light.isOn) light.brightness else 0
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Brightness",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Text(text = "$brightnessValue%", color = TextSecondary, fontSize = 14.sp)
    }
    Slider(
        value = brightnessValue.toFloat(),
        onValueChange = { raw ->
            val rounded = raw.roundToInt()
            if (rounded != brightnessValue) onBrightnessChange(rounded)
        },
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(
            thumbColor = AccentBlue,
            activeTrackColor = AccentBlue,
            inactiveTrackColor = Color(0xFF2A3140),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "${light.name} brightness" },
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "0%", color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "100%", color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun NonDimmableSection() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "This is a non-dimmable light.", color = TextSecondary, fontSize = 13.sp)
    }
}

@Preview
@Composable
private fun LightDrawerSheetDimmablePreview() {
    SmartLightsTheme {
        LightDrawerSheet(
            light = Light(id = "bed_light", name = "Bed light", dimmable = true, isOn = true, brightness = 75, lastBrightness = 75),
            errorBanner = null,
            onDismissError = {},
            onTogglePower = {},
            onBrightnessChange = {},
            onDismissSheet = {},
        )
    }
}

@Preview
@Composable
private fun LightDrawerSheetNonDimmablePreview() {
    SmartLightsTheme {
        LightDrawerSheet(
            light = Light(id = "hallway", name = "Hallway", dimmable = false, isOn = true, brightness = 0, lastBrightness = 0),
            errorBanner = null,
            onDismissError = {},
            onTogglePower = {},
            onBrightnessChange = {},
            onDismissSheet = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightDrawerSheet(
    light: Light,
    errorBanner: ErrorBanner?,
    onDismissError: () -> Unit,
    onTogglePower: () -> Unit,
    onBrightnessChange: (Int) -> Unit,
    onDismissSheet: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissSheet,
        sheetState = sheetState,
        containerColor = Color(0xFF14191F),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = light.name, color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (light.isOn) "On" else "Off",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                BulbCard(light = light)
                Spacer(modifier = Modifier.height(20.dp))
                if (light.dimmable) {
                    DimmableSection(light = light, onBrightnessChange = onBrightnessChange)
                } else {
                    NonDimmableSection()
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onTogglePower,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                ) {
                    Text(
                        text = if (light.isOn) "Turn off" else "Turn on",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            if (errorBanner != null) {
                ErrorBannerCard(
                    banner = errorBanner,
                    onDismiss = onDismissError,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                )
            }
        }
    }
}
