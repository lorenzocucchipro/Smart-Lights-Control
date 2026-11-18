package com.smartlights.control.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlights.control.R
import com.smartlights.control.presentation.ui.theme.BgPrimary
import com.smartlights.control.presentation.ui.theme.BorderSubtle
import com.smartlights.control.presentation.ui.theme.SurfaceCard
import com.smartlights.control.presentation.ui.theme.SurfaceContainer
import com.smartlights.control.presentation.ui.theme.TextPrimary
import com.smartlights.control.presentation.ui.theme.TextSecondary
import com.smartlights.control.presentation.ui.theme.SmartLightsTheme

@Composable
fun HouseHeroIllustration(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.home_header),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp),
    )
}

@Composable
fun StaticControlCard(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.defaultMinSize(minHeight = 96.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp).align(Alignment.TopStart))
            Text(text = ">", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.align(Alignment.TopEnd))
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                if (subtitle != null) Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ExternalCameraPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainer,
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hose_one),
                    contentDescription = "External camera",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                Text(
                    text = "External",
                    color = TextPrimary.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                )
            }
        }
    }
}

@Composable
fun AssistantBottomBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.weight(1f).height(46.dp).border(1.dp, BorderSubtle, RoundedCornerShape(23.dp)),
                shape = RoundedCornerShape(23.dp),
                color = SurfaceContainer,
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Text(text = "Message Qe'ra...", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(modifier = Modifier.size(46.dp), shape = CircleShape, color = Color.White) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice input", tint = BgPrimary, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun HouseHeroIllustrationPreview() {
    SmartLightsTheme { HouseHeroIllustration() }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun StaticControlCardPreview() {
    SmartLightsTheme {
        StaticControlCard(
            icon = Icons.Default.Lightbulb,
            title = "Entrance window",
            subtitle = "Closed",
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun ExternalCameraPlaceholderPreview() {
    SmartLightsTheme { ExternalCameraPlaceholder() }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E14)
@Composable
private fun AssistantBottomBarPreview() {
    SmartLightsTheme { AssistantBottomBar() }
}

