package com.smartlights.control

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.smartlights.control.di.AppContainer
import com.smartlights.control.presentation.ui.HomeRoute
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme
import com.smartlights.control.presentation.viewmodel.LightsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LightsViewModel by viewModels {
        LightsViewModel.provideFactory(AppContainer.lightRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            SmartLightsTheme {
                HomeRoute(viewModel = viewModel)
            }
        }
    }
}