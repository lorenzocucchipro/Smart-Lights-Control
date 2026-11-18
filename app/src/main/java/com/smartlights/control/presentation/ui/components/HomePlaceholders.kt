package com.smartlights.control.presentation.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartlights.control.presentation.ui.theme.BgPrimary
import com.smartlights.control.presentation.ui.theme.BorderSubtle
import com.smartlights.control.presentation.ui.theme.BulbAmber
import com.smartlights.control.presentation.ui.theme.SurfaceCard
import com.smartlights.control.presentation.ui.theme.SurfaceContainer
import com.smartlights.control.presentation.ui.theme.TextPrimary
import com.smartlights.control.presentation.ui.theme.TextSecondary

@Composable
fun HouseHeroIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp),
    ) {
        val w = size.width
        val h = size.height
        drawRect(
            brush = Brush.verticalGradient(colors = listOf(Color(0xFF0D1117), Color(0xFF12161C))),
            size = size,
        )
        drawRect(color = Color(0xFF0F1318), topLeft = Offset(0f, h * 0.82f), size = Size(w, h * 0.18f))
        val backLeft = w * 0.25f; val backTop = h * 0.28f
        val backWidth = w * 0.50f; val backHeight = h * 0.42f
        drawRoundRect(color = Color(0xFF1A2030), topLeft = Offset(backLeft, backTop), size = Size(backWidth, backHeight), cornerRadius = CornerRadius(8f, 8f))
        val bwX = backLeft + backWidth * 0.30f; val bwY = backTop + backHeight * 0.18f
        val bwW = backWidth * 0.40f; val bwH = backHeight * 0.35f
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(BulbAmber.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(bwX + bwW / 2, bwY + bwH / 2), radius = bwW * 1.2f,
            ),
            topLeft = Offset(bwX - bwW * 0.3f, bwY - bwH * 0.3f),
            size = Size(bwW * 1.6f, bwH * 1.6f), cornerRadius = CornerRadius(4f, 4f),
        )
        drawRoundRect(color = BulbAmber.copy(alpha = 0.85f), topLeft = Offset(bwX, bwY), size = Size(bwW, bwH), cornerRadius = CornerRadius(4f, 4f))
        val fLeft = w * 0.14f; val fTop = h * 0.52f
        val fWidth = w * 0.72f; val fHeight = h * 0.30f
        drawRoundRect(color = Color(0xFF1E2535), topLeft = Offset(fLeft, fTop), size = Size(fWidth, fHeight), cornerRadius = CornerRadius(8f, 8f))
        val wW = fWidth * 0.22f; val wH = fHeight * 0.50f; val wY = fTop + fHeight * 0.20f
        drawRoundRect(color = BulbAmber.copy(alpha = 0.80f), topLeft = Offset(fLeft + fWidth * 0.10f, wY), size = Size(wW, wH), cornerRadius = CornerRadius(3f, 3f))
        drawRoundRect(color = BulbAmber.copy(alpha = 0.80f), topLeft = Offset(fLeft + fWidth * 0.68f, wY), size = Size(wW, wH), cornerRadius = CornerRadius(3f, 3f))
        val dW = fWidth * 0.14f; val dH = fHeight * 0.60f
        drawRoundRect(color = Color(0xFF0B0E14), topLeft = Offset(fLeft + (fWidth - dW) / 2, fTop + fHeight - dH), size = Size(dW, dH), cornerRadius = CornerRadius(3f, 3f))
    }
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.verticalGradient(colors = listOf(Color(0xFF101520), Color(0xFF1A2235)))),
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width; val h = size.height
                    val pp = Path().apply {
                        moveTo(w * 0.40f, h); lineTo(w * 0.60f, h)
                        lineTo(w * 0.70f, h * 0.55f); lineTo(w * 0.30f, h * 0.55f); close()
                    }
                    drawPath(pp, color = Color(0xFF232A34))
                    drawRoundRect(color = Color(0xFF1E2535), topLeft = Offset(w * 0.05f, h * 0.25f), size = Size(w * 0.30f, h * 0.75f), cornerRadius = CornerRadius(6f, 6f))
                    drawRoundRect(color = Color(0xFF1A2030), topLeft = Offset(w * 0.65f, h * 0.35f), size = Size(w * 0.30f, h * 0.65f), cornerRadius = CornerRadius(6f, 6f))
                    for (row in 0..2) {
                        for (col in 0..1) {
                            val alpha = if (row == 1 && col == 0) 0.9f else 0.3f
                            drawRoundRect(color = BulbAmber.copy(alpha = alpha), topLeft = Offset(w * 0.10f + col * w * 0.12f, h * 0.32f + row * h * 0.18f), size = Size(w * 0.08f, h * 0.10f), cornerRadius = CornerRadius(2f, 2f))
                        }
                    }
                }
                Text(text = "External", color = TextPrimary.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.BottomStart).padding(10.dp))
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

