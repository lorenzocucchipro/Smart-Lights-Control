package com.smartlights.control.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.smartlights.control.presentation.mvi.ErrorBanner
import com.smartlights.control.presentation.ui.theme.ErrorCrimson
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme

@Composable
fun ErrorBannerCard(
    banner: ErrorBanner,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ErrorCrimson,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = banner.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = banner.subtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp),
            ) {
                Surface(
                    modifier = Modifier.size(22.dp),
                    shape = CircleShape,
                    color = Color.White,
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(22.dp)
                            .semantics { contentDescription = "Dismiss error" },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = ErrorCrimson,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun ErrorBannerCardPreview() {
    SmartLightsTheme {
        ErrorBannerCard(
            banner = ErrorBanner(),
            onDismiss = {},
        )
    }
}

