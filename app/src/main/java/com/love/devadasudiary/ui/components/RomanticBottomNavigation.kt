package com.love.devadasudiary.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.love.devadasudiary.core.DiaryDimens
import com.love.devadasudiary.data.model.Poem

/**
 * Icons for poems in display order. Falls back to a heart for any extra
 * entries — previous code hard-coded ids "1"/"2"/"3" which broke as soon
 * as a fourth poem was added.
 */
private val PoemIcons: List<Pair<ImageVector, ImageVector>> = listOf(
    Icons.Default.AutoStories to Icons.Default.AutoStories,
    Icons.Default.MailOutline to Icons.Default.MailOutline,
    Icons.Default.LocalFireDepartment to Icons.Default.Whatshot
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RomanticBottomNavigation(
    poems: List<Poem>,
    isDark: Boolean,
    currentPoemId: String,
    favoriteIds: Set<String>,
    onPoemSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val navBg = if (isDark)
        MaterialTheme.colorScheme.surface.copy(alpha = 0.93f)
    else
        Color(0xFFFFF1E6).copy(alpha = 0.96f)

    val borderColor = if (isDark)
        Color.White.copy(alpha = 0.08f)
    else
        Color(0xFFFFC86A).copy(alpha = 0.22f)

    val selectedColor = if (isDark)
        MaterialTheme.colorScheme.primary
    else
        Color(0xFFE84A8A)

    val unselectedColor = if (isDark)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        Color(0xFF6B3A4A)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = DiaryDimens.NavBarCornerTop,
                    topEnd = DiaryDimens.NavBarCornerTop
                )
            ),
        color = navBg,
        tonalElevation = 0.dp,
        shadowElevation = if (isDark) 8.dp else 10.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            poems.forEachIndexed { index, poem ->
                val isFavorite = poem.id in favoriteIds
                val isSelected = poem.id == currentPoemId

                val itemScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.12f else 1f,
                    animationSpec = spring(
                        dampingRatio = 0.50f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "nav_${poem.id}"
                )

                val (baseIcon, selectedIcon) = PoemIcons.getOrNull(index)
                    ?: (Icons.Default.Favorite to Icons.Default.Favorite)

                val interactionSource = remember(poem.id) { MutableInteractionSource() }

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onPoemSelected(poem.id) },
                    modifier = Modifier
                        .scale(itemScale)
                        .combinedClickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onPoemSelected(poem.id) },
                            onLongClick = { onToggleFavorite(poem.id) }
                        ),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedColor,
                        selectedTextColor = selectedColor,
                        unselectedIconColor = unselectedColor,
                        unselectedTextColor = unselectedColor,
                        indicatorColor = if (isDark)
                            selectedColor.copy(alpha = 0.18f)
                        else
                            Color(0xFFFFC86A).copy(alpha = 0.25f)
                    ),
                    icon = {
                        BadgedBox(
                            badge = {
                                if (isFavorite) {
                                    Badge(containerColor = selectedColor) {
                                        Text("♥", color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
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
