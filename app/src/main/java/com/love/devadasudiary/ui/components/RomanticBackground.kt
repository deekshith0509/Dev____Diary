package com.love.devadasudiary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin
import kotlin.random.Random

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
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = (0.03f + Random.nextFloat() * 0.08f),
                alpha = (0.05f + Random.nextFloat() * 0.10f),
                color = colors.random()
            )
        }
    }
}

// -------------------- HEART DRAWING --------------------
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

// -------------------- ANIMATION DATA CLASSES --------------------
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