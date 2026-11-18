package com.smartlights.control.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.smartlights.control.domain.model.Light
import com.smartlights.control.presentation.ui.theme.AccentBlue
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme
import com.smartlights.control.presentation.ui.theme.SurfaceCard
import com.smartlights.control.presentation.ui.theme.TextPrimary
import com.smartlights.control.presentation.ui.theme.TextSecondary

@Composable
fun LightWidgetCard(
    light: Light?,
    onTogglePower: () -> Unit,
    onBrightnessChange: (Int) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.defaultMinSize(minHeight = 96.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        onClick = onOpenDrawer,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            if (light == null) {
                Text(
                    text = "No light",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        text = light.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val statusText = when {
                        !light.isOn -> if (light.dimmable) "Off. 0%" else "Off"
                        light.dimmable -> "On. ${light.brightness}%"
                        else -> "On"
                    }
                    Text(
                        text = statusText,
                        color = if (light.isOn) AccentBlue else TextSecondary,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun LightWidgetCardOnDimmablePreview() {
    SmartLightsTheme {
        LightWidgetCard(
            light = Light(id = "bed_light", name = "Bed light", dimmable = true, isOn = true, brightness = 75, lastBrightness = 75),
            onTogglePower = {},
            onBrightnessChange = {},
            onOpenDrawer = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun LightWidgetCardOffDimmablePreview() {
    SmartLightsTheme {
        LightWidgetCard(
            light = Light(id = "desk_lamp", name = "Desk lamp", dimmable = true, isOn = false, brightness = 0, lastBrightness = 50),
            onTogglePower = {},
            onBrightnessChange = {},
            onOpenDrawer = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun LightWidgetCardNonDimmablePreview() {
    SmartLightsTheme {
        LightWidgetCard(
            light = Light(id = "hallway", name = "Hallway", dimmable = false, isOn = true, brightness = 0, lastBrightness = 0),
            onTogglePower = {},
            onBrightnessChange = {},
            onOpenDrawer = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun LightWidgetCardNullPreview() {
    SmartLightsTheme {
        LightWidgetCard(
            light = null,
            onTogglePower = {},
            onBrightnessChange = {},
            onOpenDrawer = {},
        )
    }
}

