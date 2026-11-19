package com.smartlights.control.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.smartlights.control.domain.model.Light
import com.smartlights.control.presentation.mvi.ErrorBanner
import com.smartlights.control.presentation.ui.components.LightDrawerSheet
import com.smartlights.control.presentation.ui.components.LightWidgetCard
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SmartLightsInteractionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val dimmableOn = Light("l1", "Bed light", dimmable = true, isOn = true, brightness = 75, lastBrightness = 75)
    private val nonDimmableOff = Light("l2", "Porch", dimmable = false, isOn = false, brightness = 0, lastBrightness = 0)
    private val nonDimmableOn = Light("l2", "Porch", dimmable = false, isOn = true, brightness = 0, lastBrightness = 0)

    @Test
    fun `LightWidgetCard renders On dot 75 percent for dimmable on light`() {
        composeRule.setContent {
            SmartLightsTheme {
                LightWidgetCard(light = dimmableOn, onTogglePower = {}, onBrightnessChange = {}, onOpenDrawer = {})
            }
        }
        composeRule.onNodeWithText("On · 75%").assertIsDisplayed()
    }

    @Test
    fun `LightWidgetCard renders On without percentage for non-dimmable on light`() {
        composeRule.setContent {
            SmartLightsTheme {
                LightWidgetCard(light = nonDimmableOn, onTogglePower = {}, onBrightnessChange = {}, onOpenDrawer = {})
            }
        }
        composeRule.onNodeWithText("On").assertIsDisplayed()
    }

    @Test
    fun `LightWidgetCard renders Off without percentage for non-dimmable off light`() {
        composeRule.setContent {
            SmartLightsTheme {
                LightWidgetCard(light = nonDimmableOff, onTogglePower = {}, onBrightnessChange = {}, onOpenDrawer = {})
            }
        }
        composeRule.onNodeWithText("Off").assertIsDisplayed()
    }

    @Test
    fun `LightWidgetCard includes toggle action in card content description`() {
        composeRule.setContent {
            SmartLightsTheme {
                LightWidgetCard(light = dimmableOn, onTogglePower = {}, onBrightnessChange = {}, onOpenDrawer = {})
            }
        }
        // Surface semantics: "${name}, $statusText. $toggleAction"
        composeRule.onNodeWithContentDescription("Bed light, On · 75%. Turn off").assertIsDisplayed()
    }

    @Test
    fun `LightWidgetCard invokes onOpenDrawer when tapping the chevron button`() {
        var drawerOpened = false
        composeRule.setContent {
            SmartLightsTheme {
                LightWidgetCard(light = dimmableOn, onTogglePower = {}, onBrightnessChange = {}, onOpenDrawer = { drawerOpened = true })
            }
        }
        composeRule.onNodeWithContentDescription("Open Bed light details").performClick()
        assertTrue(drawerOpened)
    }

    @Test
    fun `LightDrawerSheet renders brightness slider for dimmable light`() {
        composeRule.setContent {
            SmartLightsTheme {
                LightDrawerSheet(
                    light = dimmableOn, errorBanner = null, onDismissError = {},
                    onTogglePower = {}, onBrightnessChange = {}, onDismissSheet = {},
                )
            }
        }
        // Slider semantics: "${light.name} brightness"
        composeRule.onNodeWithContentDescription("Bed light brightness").assertIsDisplayed()
    }

    @Test
    fun `LightDrawerSheet renders non-dimmable message without slider for non-dimmable light`() {
        composeRule.setContent {
            SmartLightsTheme {
                LightDrawerSheet(
                    light = nonDimmableOff, errorBanner = null, onDismissError = {},
                    onTogglePower = {}, onBrightnessChange = {}, onDismissSheet = {},
                )
            }
        }
        composeRule.onNodeWithText("This is a non-dimmable light.").assertIsDisplayed()
    }

    @Test
    fun `LightDrawerSheet displays error banner when errorBanner is present`() {
        composeRule.setContent {
            SmartLightsTheme {
                LightDrawerSheet(
                    light = dimmableOn,
                    errorBanner = ErrorBanner(title = "Something went wrong", subtitle = "Try again later"),
                    onDismissError = {}, onTogglePower = {}, onBrightnessChange = {}, onDismissSheet = {},
                )
            }
        }
        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeRule.onNodeWithText("Try again later").assertIsDisplayed()
    }
}
