package com.love.devadasudiary.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private const val SHIMMER_TRAVEL_PX = 1200f
private const val SHIMMER_DURATION_MS = 2600

private val ShimmerColors = listOf(
    Color(0xFFFF4DA6).copy(alpha = 0.10f),
    Color(0xFFB388FF).copy(alpha = 0.45f),
    Color(0xFFFF4DA6).copy(alpha = 0.10f)
)

/**
 * A reusable shimmer brush that travels horizontally on a fixed cadence.
 * Used by the top bar title pill.
 */
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = SHIMMER_TRAVEL_PX,
        animationSpec = infiniteRepeatable(
            tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "shift"
    )
    return Brush.linearGradient(
        colors = ShimmerColors,
        start = Offset(shift - SHIMMER_TRAVEL_PX, 0f),
        end = Offset(shift, 0f)
    )
}
