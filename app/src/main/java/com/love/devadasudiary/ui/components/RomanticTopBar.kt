package com.love.devadasudiary.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomanticTopBar(
    title: String,
    subtitle: String,
    isDark: Boolean,
    isFavorite: Boolean,
    onToggleTheme: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRefresh: () -> Unit,
    onSettings: () -> Unit
) {

    val shimmer = shimmerBrush()

    val infinite = rememberInfiniteTransition(label = "topbar")
    val glow by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    val iconFlip by animateFloatAsState(
        targetValue = if (isDark) 0f else 180f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "theme_flip"
    )

    CenterAlignedTopAppBar(
        title = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Row title aligned near theme toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Spacer(Modifier.width(4.dp))

                    // Love badge dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f + glow * 0.35f),
                                CircleShape
                            )
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        ),
                        modifier = Modifier
                            .background(
                                brush = shimmer,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Light
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )
            }
        },

        navigationIcon = {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDark) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    contentDescription = "Toggle Theme",
                    modifier = Modifier.graphicsLayer { rotationY = iconFlip }
                )
            }
        },

        actions = {

            // Favorite button
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Refresh Poem button
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Refresh Poem"
                )
            }

            // Settings button
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        )
    )
}