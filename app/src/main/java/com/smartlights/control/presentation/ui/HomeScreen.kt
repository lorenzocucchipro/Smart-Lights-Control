package com.smartlights.control.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Blinds
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartlights.control.domain.model.Light
import com.smartlights.control.presentation.mvi.LightsIntent
import com.smartlights.control.presentation.mvi.LightsUiState
import com.smartlights.control.presentation.ui.components.AssistantBottomBar
import com.smartlights.control.presentation.ui.components.ErrorBannerCard
import com.smartlights.control.presentation.ui.components.ExternalCameraPlaceholder
import com.smartlights.control.presentation.ui.components.HouseHeroIllustration
import com.smartlights.control.presentation.ui.components.LightWidgetCard
import com.smartlights.control.presentation.ui.components.StaticControlCard
import com.smartlights.control.presentation.ui.theme.BgPrimary
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme
import com.smartlights.control.presentation.ui.theme.SurfaceContainer
import com.smartlights.control.presentation.ui.theme.TextPrimary
import com.smartlights.control.presentation.ui.theme.TextSecondary
import com.smartlights.control.presentation.viewmodel.LightsViewModel

// ---------------------------------------------------------------------------
// Route — thin stateful wrapper; the only place that touches the ViewModel.
// --------------------------------------------------------------------------- — thin stateful wrapper; the only place that touches the ViewModel.
// ---------------------------------------------------------------------------
@Composable
fun HomeRoute(
    viewModel: LightsViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onIntent(LightsIntent.LoadLights) }
    HomeScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

// ---------------------------------------------------------------------------
// Screen — pure, stateless, previewable.
// ---------------------------------------------------------------------------
@Composable
fun HomeScreen(
    uiState: LightsUiState,
    onIntent: (LightsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pairMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(containerColor = BgPrimary) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                // Top bar
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = TextPrimary, modifier = Modifier.padding(end = 8.dp))
                    Text(text = "At home", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Box {
                        Surface(onClick = { pairMenuExpanded = true }, shape = RoundedCornerShape(20.dp), color = Color(0xFF1C2128)) {
                            Text(text = "Pair buttons", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                        DropdownMenu(expanded = pairMenuExpanded, onDismissRequest = { pairMenuExpanded = false }) {
                            uiState.lights.forEach { light ->
                                DropdownMenuItem(
                                    text = { Text(text = light.name) },
                                    onClick = { onIntent(LightsIntent.SelectWidgetLight(light.id)); pairMenuExpanded = false },
                                )
                            }
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "More options", tint = TextSecondary)
                    }
                }
                HouseHeroIllustration()
                // 2x2 grid
                Surface(shape = RoundedCornerShape(20.dp), color = SurfaceContainer) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            LightWidgetCard(
                                light = uiState.activeWidgetLight,
                                onTogglePower = { uiState.activeWidgetLight?.id?.let { onIntent(LightsIntent.TogglePower(it)) } },
                                onBrightnessChange = { b -> uiState.activeWidgetLight?.id?.let { onIntent(LightsIntent.UpdateBrightness(it, b)) } },
                                onOpenDrawer = { uiState.activeWidgetLight?.id?.let { onIntent(LightsIntent.OpenDrawer(it)) } },
                                modifier = Modifier.weight(1f),
                            )
                            StaticControlCard(icon = Icons.Default.Window, title = "Entrance window", subtitle = "Closed", modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StaticControlCard(icon = Icons.Default.Blinds, title = "Close all blinds", subtitle = null, modifier = Modifier.weight(1f))
                            StaticControlCard(icon = Icons.Default.Nightlight, title = "Night time", subtitle = null, modifier = Modifier.weight(1f))
                        }
                    }
                }
                ExternalCameraPlaceholder()
                AssistantBottomBar()
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Error banner overlay
            val banner = uiState.errorBanner
            if (banner != null && uiState.drawerLight == null) {
                ErrorBannerCard(
                    banner = banner,
                    onDismiss = { onIntent(LightsIntent.DismissError) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 72.dp),
                )
            }
        }
    }
}

private val previewLights = listOf(
    Light(id = "bed_light", name = "Bed light", dimmable = true, isOn = true, brightness = 75, lastBrightness = 75),
    Light(id = "desk_lamp", name = "Desk lamp", dimmable = true, isOn = false, brightness = 0, lastBrightness = 50),
    Light(id = "hallway", name = "Hallway", dimmable = false, isOn = true, brightness = 0, lastBrightness = 0),
)

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun HomeScreenPreview() {
    SmartLightsTheme {
        HomeScreen(
            uiState = LightsUiState(lights = previewLights, activeWidgetLightId = "bed_light"),
            onIntent = {},
        )
    }
}