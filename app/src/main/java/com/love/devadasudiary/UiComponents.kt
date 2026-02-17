package com.love.devadasudiary

import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.LocalFireDepartment

import androidx.compose.material3.OutlinedButton

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.FavoriteBorder

import androidx.compose.foundation.text.selection.SelectionContainer

import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

// NOTE:
// RomanticFontFamily must exist in Theme.kt
// shimmerBrush() must exist in PremiumEffects.kt

// ═══════════════════════════════════════════════════════════
//                      MAIN SCREEN
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoveDiaryScreen(vm: PoetryViewModel)
 {
    val lineSpacing by vm.lineSpacing.collectAsState()
    val poemPadding by vm.poemPadding.collectAsState()
    val centerAlign by vm.centerAlign.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    fun toast(msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    val uiState by vm.uiState.collectAsState()
    val fontSize by vm.fontSize.collectAsState()
    val stripHtml by vm.stripHtml.collectAsState()
    val isDark by vm.isDarkTheme.collectAsState()
    val favoriteIds by vm.favoriteIds.collectAsState()
    val currentPoemId by vm.currentPoemId.collectAsState()

    val poems = vm.poems

    val currentPoem = remember(currentPoemId, poems) {
        poems.firstOrNull { it.id == currentPoemId } ?: poems.first()
    }

    var showSettings by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        RomanticBackground(isDark = isDark)

Scaffold(
    containerColor = Color.Transparent,
    topBar = {
        RomanticTopBar(
            title = currentPoem.title,
            subtitle = currentPoem.subtitle,
            isDark = isDark,
            isFavorite = favoriteIds.contains(currentPoemId),
            onToggleTheme = { vm.toggleTheme() },
            onToggleFavorite = { vm.toggleFavorite(currentPoemId) },
            onRefresh = { vm.refreshPoem() }, // keep top refresh
            onSettings = { showSettings = true }
        )
    },
    bottomBar = {
        RomanticBottomNavigation(
            poems = poems,
            currentPoemId = currentPoemId,
            favoriteIds = favoriteIds,
            onPoemSelected = { id ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                vm.selectPoemById(id)
            },
            onToggleFavorite = { id ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                vm.toggleFavorite(id)
            }
        )
    }
) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                RomanticHeader(title = "Made with 💖 by Devadasu...👀✨")

                Spacer(Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {

                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            (fadeIn(tween(500)) + slideInVertically(
                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                            ) { it / 8 }).togetherWith(
                                fadeOut(tween(300)) + slideOutVertically(
                                    animationSpec = tween(300)
                                ) { -it / 8 }
                            )
                        },
                        label = "poem_transition"
                    ) { state ->

                        when (state) {
                            is PoetryUiState.Loading -> RomanticLoadingCard()

                            is PoetryUiState.Error -> RomanticErrorCard(
                                title = state.title,
                                message = state.message,
                                onRetry = { vm.refreshPoem() }
                            )

                            is PoetryUiState.Success -> {
                                val poemText = remember(state.poem.content, stripHtml) {
                                    if (stripHtml) stripHtmlTags(state.poem.content)
                                    else state.poem.content
                                }
                                
                                RomanticPoemCard(
                                    poem = poemText,
                                    fontSize = fontSize,
                                    lineSpacing = lineSpacing,
                                    horizontalPadding = poemPadding,
                                    centerAlign = centerAlign
                                )
                                                        
                            }
                        }
                    }
                }

//                Spacer(Modifier.height(14.dp))
  //              RomanticFooter()
            }
        }
    }

            if (showSettings) {
                RomanticSettingsSheet(
                    fontSize = fontSize,
                    onFontSizeChange = { vm.setFontSize(it) },

                    lineSpacing = lineSpacing,
                    onLineSpacingChange = { vm.setLineSpacing(it) },

                    poemPadding = poemPadding,
                    onPoemPaddingChange = { vm.setPoemPadding(it) },

                    centerAlign = centerAlign,
                    onCenterAlignChange = { vm.setCenterAlign(it) },

                    stripHtml = stripHtml,
                    onStripHtmlChange = { vm.setStripHtml(it) },

                    onDismiss = { showSettings = false }
                )
            }

}

// ═══════════════════════════════════════════════════════════
//                   HEADER
// ═══════════════════════════════════════════════════════════
@Composable
fun RomanticHeader(title: String) {

    val infinite = rememberInfiniteTransition(label = "header")

    val shimmerX by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            tween(4200, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val glow by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    val pulse by infinite.animateFloat(
        initialValue = 0.992f,
        targetValue = 1.012f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val marqueeP by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(9000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "marquee"
    )

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onBg = MaterialTheme.colorScheme.onBackground
    val surface = MaterialTheme.colorScheme.surface

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(primary.copy(0.08f), secondary.copy(0.45f), primary.copy(0.08f)),
        start = Offset(shimmerX - 1200f, 0f),
        end = Offset(shimmerX, 0f)
    )

    var textW by remember { mutableFloatStateOf(0f) }
    var boxW by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    ) {
        val isSmall = maxWidth < 360.dp
        val hFont = if (isSmall) 15.sp else 17.sp
        val sFont = if (isSmall) 11.sp else 12.sp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .scale(pulse),
                shape = RoundedCornerShape(50.dp),
                color = surface.copy(alpha = 0.88f),
                tonalElevation = 0.dp,
                shadowElevation = 18.dp,
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(primary.copy(0.70f), secondary.copy(0.55f), Color.Transparent)
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(shimmerBrush)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = hFont,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.4.sp
                        ),
                        color = onSurface,
                        modifier = Modifier
                            .onSizeChanged { textW = it.width.toFloat() }
                            .graphicsLayer {
                                translationX = when {
                                    boxW == 0f -> 0f
                                    textW <= boxW -> (boxW - textW) / 2f
                                    else -> boxW + (-textW - boxW) * marqueeP
                                }
                            }
                    )

                    Spacer(
                        Modifier
                            .fillMaxSize()
                            .onSizeChanged { boxW = it.width.toFloat() }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Devadasu Diary • Love written in silence",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = sFont,
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


// ═══════════════════════════════════════════════════════════
//                   ANIMATED BACKGROUND
// ═══════════════════════════════════════════════════════════
@Composable
fun RomanticBackground(isDark: Boolean) {
val lightTop = Color(0xFFFFF1E6)      // champagne cream
val lightMid = Color(0xFFF6D3C6)      // warm blush beige
val lightBottom = Color(0xFFE7BFAF)   // soft rose-tan

val glowGold = Color(0xFFFFC86A)     // soft gold glow
val glowPink = Color(0xFFF48FB1)     // gentle rose
val accentHeart = Color(0xFFE84A8A)  // romantic accent
val starGold = Color(0xFFFFD38A)     // muted golden sparkle


    val stars = remember { List(120) { Star.random() } }
    val shootingStars = remember { List(5) { ShootingStar.random() } }
    val hearts = remember { List(14) { FloatingHeart.random() } }

    // Extra bokeh circles ONLY for light theme
    val bokeh = remember { List(18) { BokehCircle.random() } }

    Box(Modifier.fillMaxSize()) {

        // -------------------- BACKGROUND GRADIENT --------------------
        Canvas(Modifier.fillMaxSize()) {
            if (isDark) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF03010A),
                            Color(0xFF0D0620),
                            Color(0xFF05010D)
                        )
                    )
                )
            } else {
                // More romantic pastel gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lightTop,
                            lightMid,
                            lightBottom
                        )
                    )
                )


            }
        }

        // -------------------- SOFT GLOW BLOBS (LIGHT ONLY) --------------------
if (!isDark) {
    Canvas(Modifier.fillMaxSize()) {

        // golden glow (top)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowGold.copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.35f, size.height * 0.25f),
                radius = size.minDimension * 0.65f
            ),
            radius = size.minDimension * 0.65f,
            center = Offset(size.width * 0.35f, size.height * 0.25f)
        )

        // pink romantic glow (bottom)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowPink.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.70f, size.height * 0.80f),
                radius = size.minDimension * 0.70f
            ),
            radius = size.minDimension * 0.70f,
            center = Offset(size.width * 0.70f, size.height * 0.80f)
        )
    }
}

        // -------------------- BOKEH CIRCLES (LIGHT ONLY) --------------------
        if (!isDark) {
            Canvas(Modifier.fillMaxSize()) {
                bokeh.forEach { b ->

                    drawCircle(
                        color = b.color.copy(alpha = b.alpha),
                        radius = b.radius * size.minDimension,
                        center = Offset(b.x * size.width, b.y * size.height)
                    )
                }
            }
        }

        // -------------------- STARS --------------------
        Canvas(Modifier.fillMaxSize()) {
            stars.forEach { s ->

                val alpha = if (isDark)
                    (0.55f + 0.35f * sin(s.phase)).coerceIn(0.05f, 1.0f)
                else
                    (0.12f + 0.22f * sin(s.phase)).coerceIn(0.05f, 0.40f)

                val r = s.size * (0.95f + 0.15f * sin(s.phase))

                val starColor = if (isDark) {
                    Color.White.copy(alpha)
                } else {
                    starGold.copy(alpha = alpha * 0.55f)
                }



                drawCircle(
                    color = starColor,
                    radius = r,
                    center = Offset(s.x * size.width, s.y * size.height)
                )
            }
        }

        // -------------------- SHOOTING STARS --------------------
        Canvas(Modifier.fillMaxSize()) {
            shootingStars.forEach { st ->

                val p = 0.35f + (st.offset * 0.35f)

                val headX = st.startX * size.width + p * size.width * st.dx * 0.5f
                val headY = st.startY * size.height + p * size.height * st.dy * 0.4f

                val tailLen = size.width * 0.12f
                val tailX = headX - tailLen * st.dx
                val tailY = headY - tailLen * st.dy

                drawLine(
                    brush = Brush.linearGradient(
                        colors = if (isDark)
                            listOf(
                                Color.Transparent,
                                Color.White.copy(0.55f),
                                Color(0xFFFF4DA6).copy(0.85f)
                            )
                        else
                            listOf(
                                Color.Transparent,
                                Color.White.copy(0.35f),
                                Color(0xFFFF4DA6).copy(0.65f)
                            ),
                        start = Offset(tailX, tailY),
                        end = Offset(headX, headY)
                    ),
                    start = Offset(tailX, tailY),
                    end = Offset(headX, headY),
                    strokeWidth = if (isDark) 2.5f else 2.0f,
                    cap = StrokeCap.Round
                )

                drawCircle(
                    if (isDark) Color.White.copy(0.92f) else Color.White.copy(0.75f),
                    if (isDark) 4.5f else 3.8f,
                    Offset(headX, headY)
                )
            }
        }

        // -------------------- FLOATING HEARTS --------------------
        Canvas(Modifier.fillMaxSize()) {

        val heartColor = if (isDark) Color(0xFFFF6FAE) else accentHeart

                    

            hearts.forEach { h ->

                drawRomanticHeart(
                    center = Offset(h.x * size.width, h.y * size.height),
                    size = h.size * size.minDimension,
                    rotation = h.rotation,
                    color = heartColor.copy(alpha = if (isDark) 0.18f else 0.16f)

                )
            }
        }

        // -------------------- LIGHT THEME MIST OVERLAY --------------------
        if (!isDark) {
            Canvas(Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.Transparent,
                            Color(0xFFFFCFE3).copy(alpha = 0.10f)
                        )
                    )
                )
            }
        }
    }
}


// -------------------- BOKEH MODEL --------------------
data class BokehCircle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val color: Color
) {
    companion object {
        fun random(): BokehCircle {
            val colors = listOf(
                Color(0xFFFF6FB1),
                Color(0xFFFFC48A),
                Color(0xFFB388FF),
                Color.White
            )

            return BokehCircle(
                x = Math.random().toFloat(),
                y = Math.random().toFloat(),
                radius = (0.03f + Math.random().toFloat() * 0.08f),
                alpha = (0.05f + Math.random().toFloat() * 0.10f),
                color = colors.random()
            )
        }
    }
}



// ═══════════════════════════════════════════════════════════
//                    HEART DRAWING
// ═══════════════════════════════════════════════════════════

private fun Path.buildHeart(center: Offset, size: Float) {
    reset()
    val x = center.x
    val y = center.y

    moveTo(x, y + size * 0.25f)

    cubicTo(
        x - size * 0.35f, y - size * 0.10f,
        x - size * 0.60f, y - size * 0.50f,
        x, y - size * 0.30f
    )

    cubicTo(
        x + size * 0.60f, y - size * 0.50f,
        x + size * 0.35f, y - size * 0.10f,
        x, y + size * 0.25f
    )

    close()
}

private fun DrawScope.drawRomanticHeart(
    center: Offset,
    size: Float,
    rotation: Float,
    color: Color
) {
    val path = Path().apply { buildHeart(center, size) }

    val matrix = Matrix().apply {
        translate(center.x, center.y)
        rotateZ(rotation)
        translate(-center.x, -center.y)
    }

    path.transform(matrix)

    drawPath(path, color)
    drawPath(
        path,
        color.copy(alpha = color.alpha * 0.65f),
        style = Stroke(width = size * 0.05f)
    )
}

// ═══════════════════════════════════════════════════════════
//                      TOP BAR
// ═══════════════════════════════════════════════════════════

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

@Composable
private fun BouncyIconButton(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.72f else 1f,
        animationSpec = spring(dampingRatio = 0.38f, stiffness = Spring.StiffnessHigh),
        label = "${desc}_scale"
    )

    IconButton(
        onClick = onClick,
        interactionSource = src,
        modifier = Modifier.scale(scale)
    ) {
        Icon(icon, contentDescription = desc)
    }
}

// ═══════════════════════════════════════════════════════════
//                  BOTTOM NAVIGATION
// ═══════════════════════════════════════════════════════════

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
                        val baseIcon = when (poem.title) {
                            "Devadasu Diary" -> Icons.Default.AutoStories
                            "Love Letter" -> Icons.Default.MailOutline
                            "Eternal" -> Icons.Default.LocalFireDepartment
                            else -> Icons.Default.Favorite
                        }

                        val selectedIcon = when (poem.title) {
                            "Devadasu Diary" -> Icons.Default.AutoStories
                            "Love Letter" -> Icons.Default.MailOutline
                            "Eternal" -> Icons.Default.Whatshot
                            else -> Icons.Default.Favorite
                        }

                        BadgedBox(
                            badge = {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = isFav,
                                    enter = scaleIn(spring(dampingRatio = 0.40f)) + fadeIn(),
                                    exit = scaleOut() + fadeOut()
                                ) {
                                    Badge(
                                        containerColor = selectedColor
                                    ) {
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


// ═══════════════════════════════════════════════════════════
//              FAB — ORBIT RINGS + SPIN + CLICK BURST
// ═══════════════════════════════════════════════════════════

@Composable
fun RomanticRefreshFAB(onRefresh: () -> Unit) {

    val infinite = rememberInfiniteTransition(label = "fab")

    val spin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(6000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "fab_spin"
    )

    val ringScale by infinite.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "ring_scale"
    )

    var clicked by remember { mutableStateOf(false) }

    val clickBurst by animateFloatAsState(
        targetValue = if (clicked) 0.80f else 1f,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessHigh),
        label = "fab_burst",
        finishedListener = { if (it < 0.85f) clicked = false }
    )

    val primary = MaterialTheme.colorScheme.primary

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = primary.copy(alpha = 0.16f * ringScale),
                radius = size.minDimension / 2f * ringScale,
                style = Stroke(2.5f)
            )

            drawCircle(
                color = primary.copy(alpha = 0.08f),
                radius = size.minDimension / 2f * (ringScale * 0.75f),
                style = Stroke(1.5f)
            )
        }

        FloatingActionButton(
            onClick = {
                clicked = true
                onRefresh()
            },
            containerColor = primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .size(62.dp)
                .scale(clickBurst)
                .graphicsLayer { rotationZ = spin },
            elevation = FloatingActionButtonDefaults.elevation(10.dp)
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = "Refresh",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
//                     POEM CARD
// ═══════════════════════════════════════════════════════════
@Composable
fun RomanticPoemCard(
    poem: String,
    fontSize: Float,
    lineSpacing: Float,
    horizontalPadding: Float,
    centerAlign: Boolean
) {
    val lines = remember(poem) { poem.split("\n") }

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = surface.copy(alpha = 0.90f)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(primary.copy(0.65f), secondary.copy(0.30f), Color.Transparent)
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(primary.copy(0.06f), Color.Transparent, secondary.copy(0.04f))
                    )
                )
        ) {
            SelectionContainer {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    item { Spacer(Modifier.height(10.dp)) }

                    itemsIndexed(lines, key = { idx, _ -> idx }) { idx, rawLine ->

                        val line = rawLine.replace("\t", "    ")

                        val isBlank = line.trim().isEmpty()
                        val prevBlank = idx > 0 && lines[idx - 1].trim().isEmpty()

                        if (isBlank) {
                            Spacer(
                                Modifier.height(
                                    if (prevBlank) (fontSize * 1.2f).dp
                                    else (fontSize * 0.75f).dp
                                )
                            )
                            return@itemsIndexed
                        }

                        val isBigTitle = line.startsWith("####")
                        val isSectionTitle = line.startsWith("##")
                        val isQuote = line.trimStart().startsWith("> ")
                        val isBullet = line.trimStart().startsWith("- ") || line.trimStart().startsWith("• ")
                        val isIndented = line.startsWith("    ")

                        val cleanedText = when {
                            isBigTitle -> line.removePrefix("####").trim()
                            isSectionTitle -> line.removePrefix("##").trim()
                            isQuote -> line.trimStart().removePrefix("> ").trim()
                            isBullet -> line.trimStart()
                            isIndented -> line.removePrefix("    ")
                            else -> line.trim()
                        }

                        val textAlign = when {
                            isBigTitle -> TextAlign.Center
                            isSectionTitle -> TextAlign.Center
                            isQuote -> TextAlign.Start
                            isBullet -> TextAlign.Start
                            isIndented -> TextAlign.Start
                            else -> if (centerAlign) TextAlign.Center else TextAlign.Start
                        }

                        val leftPad = when {
                            isQuote -> 14.dp
                            isBullet -> 10.dp
                            isIndented -> 20.dp
                            else -> 0.dp
                        }

                        val finalFontSize = when {
                            isBigTitle -> (fontSize + 7).sp
                            isSectionTitle -> (fontSize + 4).sp
                            isQuote -> (fontSize + 1).sp
                            else -> fontSize.sp
                        }

                        val finalWeight = when {
                            isBigTitle -> FontWeight.SemiBold
                            isSectionTitle -> FontWeight.Medium
                            else -> FontWeight.Light
                        }

                        val finalStyle = when {
                            isQuote -> FontStyle.Italic
                            else -> FontStyle.Normal
                        }

                        val finalAlpha = when {
                            isBigTitle -> 1f
                            isSectionTitle -> 0.95f
                            isQuote -> 0.85f
                            isIndented -> 0.90f
                            else -> 0.93f
                        }

                        // ✅ THIS IS THE REAL FIX: lineSpacing is used here
                        val lineHeight = when {
                            isBigTitle -> (fontSize + lineSpacing + 10).sp
                            isSectionTitle -> (fontSize + lineSpacing + 7).sp
                            else -> (fontSize + lineSpacing).sp
                        }

                        if (isQuote) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height((fontSize * 2.4f).dp)
                                        .background(primary.copy(alpha = 0.45f), RoundedCornerShape(2.dp))
                                )

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = cleanedText,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = leftPad),
                                    style = TextStyle(
                                        fontSize = finalFontSize,
                                        fontFamily = RomanticFontFamily,
                                        fontWeight = finalWeight,
                                        fontStyle = finalStyle,
                                        color = onSurface.copy(alpha = finalAlpha),
                                        lineHeight = lineHeight,
                                        letterSpacing = 0.3.sp,
                                        textAlign = textAlign
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = cleanedText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = leftPad),
                                style = TextStyle(
                                    fontSize = finalFontSize,
                                    fontFamily = RomanticFontFamily,
                                    fontWeight = finalWeight,
                                    fontStyle = finalStyle,
                                    color = onSurface.copy(alpha = finalAlpha),
                                    lineHeight = lineHeight,
                                    letterSpacing = if (isBigTitle) 1.3.sp else 0.4.sp,
                                    textAlign = textAlign
                                )
                            )
                        }
                    }

                    item { Spacer(Modifier.height(10.dp)) }
                }
            }
        }
    }
}


// ═══════════════════════════════════════════════════════════
//                    LOADING CARD
// ═══════════════════════════════════════════════════════════
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
                    listOf(primary.copy(0.65f), secondary.copy(0.40f), Color.Transparent)
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



// ═══════════════════════════════════════════════════════════
//                     ERROR CARD
// ═══════════════════════════════════════════════════════════

@Composable
fun RomanticErrorCard(title: String, message: String, onRetry: () -> Unit) {

    val shakeAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        repeat(4) { i ->
            shakeAnim.animateTo(if (i % 2 == 0) 14f else -14f, tween(70))
        }
        shakeAnim.animateTo(0f, tween(100))
    }

    val infinite = rememberInfiniteTransition(label = "error")
    val iconPulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "err_pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationX = shakeAnim.value },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.93f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.38f))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {

                Icon(
                    Icons.Default.HeartBroken,
                    contentDescription = null,
                    modifier = Modifier.size(68.dp).scale(iconPulse),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Try Again", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
//                       FOOTER
// ═══════════════════════════════════════════════════════════

/**
@Composable
fun RomanticFooter() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {

        /**
        Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f))


        Spacer(Modifier.width(8.dp))
        Text(
            text = "written by my soul",
            style = MaterialTheme.typography.labelLarge.copy(
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            )
        )
        Spacer(Modifier.width(8.dp))


        Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f))
      */
    }
}
*/

// ═══════════════════════════════════════════════════════════
//                  SETTINGS SHEET
// ═══════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomanticSettingsSheet(
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,

    lineSpacing: Float,
    onLineSpacingChange: (Float) -> Unit,

    poemPadding: Float,
    onPoemPaddingChange: (Float) -> Unit,

    centerAlign: Boolean,
    onCenterAlignChange: (Boolean) -> Unit,

    stripHtml: Boolean,
    onStripHtmlChange: (Boolean) -> Unit,

    onDismiss: () -> Unit
) {

    // Local state for smooth preview (no VM delay jitter)
    var localFontSize by remember(fontSize) { mutableStateOf(fontSize) }
    var localLineSpacing by remember(lineSpacing) { mutableStateOf(lineSpacing) }
    var localPadding by remember(poemPadding) { mutableStateOf(poemPadding) }
    var localAlign by remember(centerAlign) { mutableStateOf(centerAlign) }

    var showHint by remember { mutableStateOf(true) }

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface

    val previewText = remember {
        "ఇది నా మనసు రాసిన ప్రేమ...\nనా మాటల్లో నిన్ను దాచుకున్నాను..."
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Text(
                "✦ Settings ✦",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Tune your diary like a heartbeat...",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            // ✅ FIXED HEIGHT PREVIEW (no jumping)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(primary.copy(0.45f), secondary.copy(0.35f), Color.Transparent)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = previewText,
                        style = TextStyle(
                            fontSize = localFontSize.sp,
                            fontFamily = RomanticFontFamily,
                            fontWeight = FontWeight.Light,
                            color = onSurface.copy(alpha = 0.92f),
                            lineHeight = (localFontSize + localLineSpacing).sp,
                            textAlign = if (localAlign) TextAlign.Center else TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = localPadding.dp)
                    )

                    if (showHint) {
                        Text(
                            text = "Preview",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = FontStyle.Italic
                            ),
                            color = onSurface.copy(alpha = 0.55f),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------------- FONT SIZE ----------------
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Font Size", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${localFontSize.toInt()} sp",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primary
                    )
                )
            }

            Slider(
                value = localFontSize,
                onValueChange = {
                    localFontSize = it
                    onFontSizeChange(it)
                },
                valueRange = 14f..34f,
                steps = 9
            )

            Spacer(Modifier.height(14.dp))

            // ---------------- LINE SPACING ----------------
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Line Spacing", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${localLineSpacing.toInt()}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = secondary
                    )
                )
            }

            Slider(
                value = localLineSpacing,
                onValueChange = {
                    localLineSpacing = it
                    onLineSpacingChange(it)
                },
                valueRange = 6f..22f,
                steps = 7
            )

            Spacer(Modifier.height(14.dp))

            // ---------------- PADDING ----------------
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Reading Padding", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${localPadding.toInt()} dp",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primary
                    )
                )
            }

            Slider(
                value = localPadding,
                onValueChange = {
                    localPadding = it
                    onPoemPaddingChange(it)
                },
                valueRange = 0f..30f,
                steps = 6
            )

            Spacer(Modifier.height(12.dp))

            // ---------------- ALIGNMENT ----------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Center Align Text")
                Switch(
                    checked = localAlign,
                    onCheckedChange = {
                        localAlign = it
                        onCenterAlignChange(it)
                    }
                )
            }

            // ---------------- STRIP HTML ----------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Remove HTML Tags")
                Switch(
                    checked = stripHtml,
                    onCheckedChange = onStripHtmlChange
                )
            }

            // ---------------- PREVIEW HINT ----------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Preview Hint")
                Switch(
                    checked = showHint,
                    onCheckedChange = { showHint = it }
                )
            }

            Spacer(Modifier.height(18.dp))
                OutlinedButton(
                    onClick = {
                        onFontSizeChange(14f)
                        onLineSpacingChange(12f)
                        onPoemPaddingChange(22f)
                        onCenterAlignChange(false)
                        onStripHtmlChange(false)
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    border = BorderStroke(1.dp, primary.copy(alpha = 0.6f))
                ) {
                    Text(
                        "Reset to Default",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp,
                        color = primary
                    )
                }

                Spacer(Modifier.height(12.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    "Done",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}



// ═══════════════════════════════════════════════════════════
//                    HTML STRIPPER
// ═══════════════════════════════════════════════════════════

private fun stripHtmlTags(text: String): String =
    text.replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&gt;", ">")
        .trim()

// ═══════════════════════════════════════════════════════════
//                ANIMATION DATA CLASSES
// ═══════════════════════════════════════════════════════════

data class Star(val x: Float, val y: Float, val size: Float, val phase: Float) {
    companion object {
        fun random() = Star(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            size = Random.nextFloat() * 2.0f + 0.6f,
            phase = Random.nextFloat() * 10f
        )
    }
}

data class ShootingStar(
    val startX: Float,
    val startY: Float,
    val dx: Float,
    val dy: Float,
    val speed: Float,
    val offset: Float
) {
    companion object {
        fun random() = ShootingStar(
            startX = Random.nextFloat() * 0.8f,
            startY = Random.nextFloat() * 0.35f,
            dx = 0.9f,
            dy = 0.8f,
            speed = Random.nextFloat() * 2.0f + 0.7f,
            offset = Random.nextFloat()
        )
    }
}

data class FloatingHeart(
    val x: Float,
    val y: Float,
    val size: Float,
    val rotation: Float,
    val speed: Float,
    val offset: Float
) {
    companion object {
        fun random() = FloatingHeart(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            size = Random.nextFloat() * 0.06f + 0.03f,
            rotation = Random.nextFloat() * 360f,
            speed = Random.nextFloat() * 1.4f + 0.5f,
            offset = Random.nextFloat()
        )
    }
}
