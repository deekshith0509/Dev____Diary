package com.love.devadasudiary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.LaunchedEffect
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated romantic background.
 *
 * Previously the stars/shooting-stars/hearts were sampled with a fixed
 * Random offset and never repainted, so the "twinkle" and motion never
 * actually happened. We now drive a single shared `frameTime` clock via
 * [withFrameMillis] and feed it into every per-particle position/opacity
 * formula — so the canvas redraws once per frame and the field actually
 * moves.
 */
@Composable
fun RomanticBackground(isDark: Boolean) {

    val stars         = remember { List(STAR_COUNT)        { Star.random() } }
    val shootingStars = remember { List(SHOOTING_COUNT)    { ShootingStar.random() } }
    val hearts        = remember { List(HEART_COUNT)       { FloatingHeart.random() } }
    val bokeh         = remember { List(BOKEH_COUNT)       { BokehCircle.random() } }

    var time by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) {
            withFrameMillis { now -> time = now - start }
        }
    }
    val tSec = time / 1000f

    Box(Modifier.fillMaxSize()) {

        // ---- Static gradient backdrop -----------------------------------
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = if (isDark) DarkBackdrop else LightBackdrop
                )
            )
        }

        // ---- Light-only romantic glow blobs -----------------------------
        if (!isDark) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GlowGold.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width * 0.35f, size.height * 0.25f),
                        radius = size.minDimension * 0.65f
                    ),
                    radius = size.minDimension * 0.65f,
                    center = Offset(size.width * 0.35f, size.height * 0.25f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GlowPink.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.70f, size.height * 0.80f),
                        radius = size.minDimension * 0.70f
                    ),
                    radius = size.minDimension * 0.70f,
                    center = Offset(size.width * 0.70f, size.height * 0.80f)
                )

                bokeh.forEach { b ->
                    drawCircle(
                        color = b.color.copy(alpha = b.alpha),
                        radius = b.radius * size.minDimension,
                        center = Offset(b.x * size.width, b.y * size.height)
                    )
                }
            }
        }

        // ---- Twinkling stars --------------------------------------------
        Canvas(Modifier.fillMaxSize()) {
            stars.forEach { s ->
                val phase = s.phase + tSec * s.twinkleSpeed
                val alpha = if (isDark)
                    (0.55f + 0.35f * sin(phase)).coerceIn(0.05f, 1.0f)
                else
                    (0.12f + 0.22f * sin(phase)).coerceIn(0.05f, 0.40f)
                val r = s.size * (0.95f + 0.15f * sin(phase))
                val color = if (isDark) Color.White.copy(alpha = alpha)
                            else StarGold.copy(alpha = alpha * 0.55f)
                drawCircle(
                    color = color,
                    radius = r,
                    center = Offset(s.x * size.width, s.y * size.height)
                )
            }
        }

        // ---- Shooting stars ---------------------------------------------
        Canvas(Modifier.fillMaxSize()) {
            shootingStars.forEach { st ->
                // Looping progress in [0,1) — actually moves each frame now.
                val raw = (tSec * st.speed * 0.18f + st.offset) % 1f
                val p = 0.35f + raw * 0.35f

                val headX = st.startX * size.width + p * size.width * st.dx * 0.5f
                val headY = st.startY * size.height + p * size.height * st.dy * 0.4f

                val tailLen = size.width * 0.12f
                val tailX = headX - tailLen * st.dx
                val tailY = headY - tailLen * st.dy

                drawLine(
                    brush = Brush.linearGradient(
                        colors = if (isDark) DarkTrail else LightTrail,
                        start = Offset(tailX, tailY),
                        end = Offset(headX, headY)
                    ),
                    start = Offset(tailX, tailY),
                    end = Offset(headX, headY),
                    strokeWidth = if (isDark) 2.5f else 2.0f,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = if (isDark) Color.White.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.75f),
                    radius = if (isDark) 4.5f else 3.8f,
                    center = Offset(headX, headY)
                )
            }
        }

        // ---- Floating hearts --------------------------------------------
        Canvas(Modifier.fillMaxSize()) {
            val heartColor = if (isDark) Color(0xFFFF6FAE) else AccentHeart
            hearts.forEach { h ->
                val driftY = (h.y + tSec * 0.015f * h.speed) % 1f
                val driftX = h.x + 0.02f * sin(tSec * h.speed * 0.6f + h.offset * 6.28f)
                val rot = h.rotation + tSec * 8f * h.speed
                drawRomanticHeart(
                    center = Offset(driftX * size.width, driftY * size.height),
                    sizePx = h.size * size.minDimension,
                    rotationDegrees = rot,
                    color = heartColor.copy(alpha = if (isDark) 0.18f else 0.16f)
                )
            }
        }

        // ---- Light theme mist overlay -----------------------------------
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

// ---------------- particle data ----------------

private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val phase: Float,
    val twinkleSpeed: Float
) {
    companion object {
        fun random() = Star(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            size = Random.nextFloat() * 2.0f + 0.6f,
            phase = Random.nextFloat() * TWO_PI,
            twinkleSpeed = 0.6f + Random.nextFloat() * 1.2f
        )
    }
}

private data class ShootingStar(
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

private data class FloatingHeart(
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

private data class BokehCircle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val color: Color
) {
    companion object {
        private val Palette = listOf(
            Color(0xFFFF6FB1),
            Color(0xFFFFC48A),
            Color(0xFFB388FF),
            Color.White
        )

        fun random() = BokehCircle(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            radius = 0.03f + Random.nextFloat() * 0.08f,
            alpha = 0.05f + Random.nextFloat() * 0.10f,
            color = Palette.random()
        )
    }
}

// ---------------- heart drawing ----------------

private fun DrawScope.drawRomanticHeart(
    center: Offset,
    sizePx: Float,
    rotationDegrees: Float,
    color: Color
) {
    // Reused path: avoids per-frame Path() allocation in the original code.
    val path = heartPath(center, sizePx).apply {
        transform(
            Matrix().apply {
                translate(center.x, center.y)
                rotateZ(rotationDegrees)
                translate(-center.x, -center.y)
            }
        )
    }
    drawPath(path, color)
    drawPath(path, color.copy(alpha = color.alpha * 0.65f), style = Stroke(width = sizePx * 0.05f))
}

private fun heartPath(center: Offset, size: Float): Path {
    val x = center.x
    val y = center.y
    return Path().apply {
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
}

// ---------------- constants ----------------

private const val STAR_COUNT = 120
private const val SHOOTING_COUNT = 5
private const val HEART_COUNT = 14
private const val BOKEH_COUNT = 18
private const val TWO_PI = (2.0 * Math.PI).toFloat()

private val DarkBackdrop = listOf(
    Color(0xFF03010A), Color(0xFF0D0620), Color(0xFF05010D)
)
private val LightBackdrop = listOf(
    Color(0xFFFFF1E6), Color(0xFFF6D3C6), Color(0xFFE7BFAF)
)

private val DarkTrail = listOf(
    Color.Transparent,
    Color.White.copy(alpha = 0.55f),
    Color(0xFFFF4DA6).copy(alpha = 0.85f)
)
private val LightTrail = listOf(
    Color.Transparent,
    Color.White.copy(alpha = 0.35f),
    Color(0xFFFF4DA6).copy(alpha = 0.65f)
)

private val GlowGold = Color(0xFFFFC86A)
private val GlowPink = Color(0xFFF48FB1)
private val AccentHeart = Color(0xFFE84A8A)
private val StarGold = Color(0xFFFFD38A)
