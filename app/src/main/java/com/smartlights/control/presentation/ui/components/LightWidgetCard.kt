package com.smartlights.control.presentation.ui.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlights.control.domain.model.Light
import com.smartlights.control.presentation.ui.theme.BulbAmber
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme
import com.smartlights.control.presentation.ui.theme.SurfaceCard
import com.smartlights.control.presentation.ui.theme.TextPrimary
import com.smartlights.control.presentation.ui.theme.TextSecondary
import com.smartlights.control.presentation.ui.theme.WidgetFillBlue

@Composable
fun LightWidgetCard(
    light: Light?,
    onTogglePower: () -> Unit,
    onBrightnessChange: (Int) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (light == null) {
        Surface(
            modifier = modifier.defaultMinSize(minHeight = 104.dp),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text(text = "No light", color = TextSecondary, fontSize = 13.sp)
            }
        }
        return
    }

    val currentLight by rememberUpdatedState(light)
    val currentOnTogglePower by rememberUpdatedState(onTogglePower)
    val currentOnBrightnessChange by rememberUpdatedState(onBrightnessChange)

    var cardWidthPx by remember { mutableFloatStateOf(1f) }
    var dragAccumulated by remember { mutableFloatStateOf(0f) }
    var dragStartBrightness by remember { mutableIntStateOf(0) }
    var swipeTriggered by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }

    val fillFraction = when {
        !light.isOn -> 0f
        light.dimmable -> light.brightness.coerceIn(0, 100) / 100f
        else -> 1f
    }
    val statusText = when {
        !light.isOn -> if (light.dimmable) "Off · 0%" else "Off"
        light.dimmable -> "On · ${light.brightness}%"
        else -> "On"
    }
    val toggleAction = if (light.isOn) "Turn off" else "Turn on"

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 104.dp)
            .semantics { contentDescription = "${light.name}, $statusText. $toggleAction" },
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { cardWidthPx = it.width.toFloat().coerceAtLeast(1f) }
                .drawBehind {
                    if (fillFraction > 0f) {
                        drawRect(
                            color = WidgetFillBlue,
                            size = androidx.compose.ui.geometry.Size(size.width * fillFraction, size.height),
                        )
                    }
                }
                .pointerInput(light.id) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            dragAccumulated = 0f
                            dragStartBrightness = currentLight?.brightness ?: 0
                            swipeTriggered = false
                            isDragging = true
                        },
                        onDragEnd = { dragAccumulated = 0f; swipeTriggered = false; isDragging = false },
                        onDragCancel = { dragAccumulated = 0f; swipeTriggered = false; isDragging = false },
                        onHorizontalDrag = { _, dragAmount ->
                            dragAccumulated += dragAmount
                            val l = currentLight ?: return@detectHorizontalDragGestures
                            if (l.dimmable) {
                                val newBrightness = (dragStartBrightness + (dragAccumulated / cardWidthPx * 100f).toInt()).coerceIn(0, 100)
                                currentOnBrightnessChange(newBrightness)
                            } else {
                                val threshold = 24.dp.toPx()
                                if (!swipeTriggered && kotlin.math.abs(dragAccumulated) >= threshold) {
                                    swipeTriggered = true
                                    currentOnTogglePower()
                                }
                            }
                        },
                    )
                },
            verticalAlignment = Alignment.Bottom,
        ) {
            // Main tap area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                    .pointerInput(light.id) {
                        detectTapGestures(onTap = {
                            if (!isDragging) currentOnTogglePower()
                        })
                    }
                    .padding(12.dp),
            ) {
                Icon(
                    imageVector = if (light.isOn) Icons.Rounded.Lightbulb else Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = if (light.isOn) BulbAmber else TextSecondary,
                    modifier = Modifier.size(20.dp).align(Alignment.TopStart),
                )
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        text = light.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = statusText, color = TextSecondary, fontSize = 12.sp)
                }
            }

            // Chevron drawer trigger
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                    .fillMaxHeight()
                    .semantics { contentDescription = "Open ${light.name} details" }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.changes.any { it.pressed.not() && it.previousPressed }) {
                                    onOpenDrawer()
                                }
                            }
                        }
                    },
            ) {
                Text(text = ">", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(end = 12.dp))
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

