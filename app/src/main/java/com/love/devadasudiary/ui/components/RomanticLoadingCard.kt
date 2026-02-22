package com.love.devadasudiary.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RomanticLoadingCard() {

    val infinite = rememberInfiniteTransition(label = "loading")

    val pulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glow by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
            ),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(
                    listOf(primary.copy(0.65f), secondary.copy(0.40f), androidx.compose.ui.graphics.Color.Transparent)
                )
            ),
            modifier = Modifier
                .padding(18.dp)
                .scale(pulse)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(contentAlignment = Alignment.Center) {

                    CircularProgressIndicator(
                        strokeWidth = 4.dp,
                        color = primary
                    )

                    Text(
                        text = "♥",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = primary.copy(alpha = 0.55f + glow * 0.35f)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Loading your feelings...",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "please wait...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Light
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }
    }
}