package com.love.devadasudiary

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "shift"
    )

    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF4DA6).copy(alpha = 0.10f),
            Color(0xFFB388FF).copy(alpha = 0.45f),
            Color(0xFFFF4DA6).copy(alpha = 0.10f)
        ),
        start = Offset(shift - 1200f, 0f),
        end = Offset(shift, 0f)
    )
}
