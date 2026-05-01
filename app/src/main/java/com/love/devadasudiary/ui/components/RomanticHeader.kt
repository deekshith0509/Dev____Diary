package com.love.devadasudiary.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.love.devadasudiary.core.DiaryDimens

/**
 * Decorative app banner: a pulsing pill with a shimmering gradient and a
 * marquee that only kicks in if the title overflows. Width measurement
 * was previously broken — Spacer was layered on top of the Text and
 * "consumed" the size, so the marquee never settled. We now measure the
 * outer Box directly via [onSizeChanged].
 */
@Composable
fun RomanticHeader(title: String) {

    val infinite = rememberInfiniteTransition(label = "header")

    val shimmerX by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer"
    )

    val glow by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val pulse by infinite.animateFloat(
        initialValue = 0.992f,
        targetValue = 1.012f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    val marqueeP by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "marquee"
    )

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onBg = MaterialTheme.colorScheme.onBackground
    val surface = MaterialTheme.colorScheme.surface

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            primary.copy(alpha = 0.08f),
            secondary.copy(alpha = 0.45f),
            primary.copy(alpha = 0.08f)
        ),
        start = Offset(shimmerX - 1200f, 0f),
        end = Offset(shimmerX, 0f)
    )

    var textWidth by remember { mutableIntStateOf(0) }
    var boxWidth by remember { mutableIntStateOf(0) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    ) {
        val isSmall = maxWidth < 360.dp
        val headerFont = if (isSmall) 15.sp else 17.sp
        val subFont = if (isSmall) 11.sp else 12.sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DiaryDimens.HeaderHeight)
                    .scale(pulse),
                shape = RoundedCornerShape(50.dp),
                color = surface.copy(alpha = 0.88f),
                tonalElevation = 0.dp,
                shadowElevation = 18.dp,
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            primary.copy(alpha = 0.70f),
                            secondary.copy(alpha = 0.55f),
                            Color.Transparent
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(shimmerBrush)
                        .padding(horizontal = 18.dp)
                        .onSizeChanged { boxWidth = it.width },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = headerFont,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.4.sp
                        ),
                        color = onSurface,
                        modifier = Modifier
                            .onSizeChanged { textWidth = it.width }
                            .graphicsLayer {
                                translationX = when {
                                    boxWidth == 0 -> 0f
                                    textWidth <= boxWidth -> (boxWidth - textWidth) / 2f
                                    else -> boxWidth + (-textWidth - boxWidth.toFloat()) * marqueeP
                                }
                            }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Devadasu Diary • Love written in silence",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = subFont,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.1.sp
                ),
                color = onBg.copy(alpha = 0.55f + glow * 0.25f),
                textAlign = TextAlign.Center
            )
        }
    }
}
