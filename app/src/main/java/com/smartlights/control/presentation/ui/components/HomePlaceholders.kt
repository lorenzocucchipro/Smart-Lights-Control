package com.smartlights.control.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlights.control.R
import com.smartlights.control.presentation.ui.theme.AccentBlue
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
        modifier = modifier
            .heightIn(min = 108.dp)
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 6.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.height(18.dp))
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.defaultMinSize(minWidth = 44.dp, minHeight = 44.dp),
            ) {
                Text(text = ">", color = TextSecondary, fontSize = 14.sp)
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
        LazyRow(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val drawables = listOf(R.drawable.hose_one, R.drawable.house_two)
            items(drawables.size) { index ->
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth(0.90f)
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp)),
                ) {
                    Image(
                        painter = painterResource(id = drawables[index]),
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
                            .padding(12.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun AssistantBottomBar(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxWidth().imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.weight(1f).height(46.dp).border(1.dp, BorderSubtle, RoundedCornerShape(23.dp)),
                shape = RoundedCornerShape(23.dp),
                color = SurfaceContainer,
            ) {
                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = text,
                        onValueChange = { text = it },
                        singleLine = true,
                        textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                        cursorBrush = SolidColor(AccentBlue),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(text = "Message Qe'ra...", color = TextSecondary, fontSize = 14.sp)
                            }
                            innerTextField()
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(modifier = Modifier.size(46.dp), shape = CircleShape, color = Color.White) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice input", tint = BgPrimary, modifier = Modifier.size(22.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
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

