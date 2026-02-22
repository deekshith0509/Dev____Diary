package com.love.devadasudiary.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.love.devadasudiary.Poem

@Composable
fun RomanticBottomNavigation(
    poems: List<Poem>,
    currentPoemId: String,
    favoriteIds: Set<String>,
    onPoemSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f

    val navBg = if (isDark)
        MaterialTheme.colorScheme.surface.copy(alpha = 0.93f)
    else
        Color(0xFFFFF1E6).copy(alpha = 0.96f)   // champagne cream

    val borderColor = if (isDark)
        Color.White.copy(alpha = 0.08f)
    else
        Color(0xFFFFC86A).copy(alpha = 0.22f)   // soft gold border

    val selectedColor = if (isDark)
        MaterialTheme.colorScheme.primary
    else
        Color(0xFFE84A8A) // romantic rose accent

    val unSelectedColor = if (isDark)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        Color(0xFF6B3A4A) // warm muted text

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)),
        color = navBg,
        tonalElevation = 0.dp,
        shadowElevation = if (isDark) 8.dp else 10.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        NavigationBar(
            containerColor = Color.Transparent, // important, Surface handles bg
            tonalElevation = 0.dp
        ) {
            poems.forEach { poem ->
                val isFav = favoriteIds.contains(poem.id)
                val isSelected = poem.id == currentPoemId

                val itemScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.12f else 1f,
                    animationSpec = spring(
                        dampingRatio = 0.50f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "nav_${poem.id}"
                )

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onPoemSelected(poem.id) },
                    modifier = Modifier.scale(itemScale),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedColor,
                        selectedTextColor = selectedColor,
                        unselectedIconColor = unSelectedColor,
                        unselectedTextColor = unSelectedColor,
                        indicatorColor = if (isDark)
                            selectedColor.copy(alpha = 0.18f)
                        else
                            Color(0xFFFFC86A).copy(alpha = 0.25f) // gold highlight
                    ),
                    icon = {
                        val baseIcon = when (poem.id) {
                            "1" -> Icons.Default.AutoStories
                            "2" -> Icons.Default.MailOutline
                            "3" -> Icons.Default.LocalFireDepartment
                            else -> Icons.Default.Favorite
                        }

                        val selectedIcon = when (poem.id) {
                            "1" -> Icons.Default.AutoStories
                            "2" -> Icons.Default.MailOutline
                            "3" -> Icons.Default.Whatshot
                            else -> Icons.Default.Favorite
                        }

                        BadgedBox(
                            badge = {
                                if (isFav) {
                                    Badge(
                                        containerColor = selectedColor
                                    ) {
                                        Text("♥", color = Color.White)
                                    }
                                }
                            }
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = if (isSelected) selectedIcon else baseIcon,
                                contentDescription = poem.title
                            )
                        }
                    },
                    label = {
                        Text(
                            text = poem.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                )
            }
        }
    }
}