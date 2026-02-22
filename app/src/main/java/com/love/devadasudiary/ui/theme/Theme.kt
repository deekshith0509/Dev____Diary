package com.love.devadasudiary.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Romantic custom colors (fallback if dynamic color not available)
val RomanticLightPalette = lightColorScheme(
    primary = Color(0xFFC2185B),      // Deep pink
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3E001D),

    secondary = Color(0xFF7C4DFF),    // Violet
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF28005A),

    tertiary = Color(0xFFFF6E40),     // Coral
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAD4),
    onTertiaryContainer = Color(0xFF410E00),

    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFFFF2F5),   // Soft pink
    onBackground = Color(0xFF2C001E),

    surface = Color(0xFFFFF2F5),
    onSurface = Color(0xFF2C001E),
    surfaceVariant = Color(0xFFF2DDE2),
    onSurfaceVariant = Color(0xFF514347),

    outline = Color(0xFF827379),
    outlineVariant = Color(0xFFD5C2C6),

    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFC2185B)
)

val RomanticDarkPalette = darkColorScheme(
    primary = Color(0xFFFF80AB),      // Light pink
    onPrimary = Color(0xFF5E1133),
    primaryContainer = Color(0xFF8F0044),
    onPrimaryContainer = Color(0xFFFFD9E2),

    secondary = Color(0xFFB388FF),    // Lavender
    onSecondary = Color(0xFF3E0079),
    secondaryContainer = Color(0xFF5B0EB8),
    onSecondaryContainer = Color(0xFFEADDFF),

    tertiary = Color(0xFFFF8A80),     // Light coral
    onTertiary = Color(0xFF690005),
    tertiaryContainer = Color(0xFF93000F),
    onTertiaryContainer = Color(0xFFFFDAD4),

    error = Color(0xFFCF6679),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF1A0F1C),   // Deep wine
    onBackground = Color(0xFFFFF0F5),

    surface = Color(0xFF2C1B2A),
    onSurface = Color(0xFFFFF0F5),
    surfaceVariant = Color(0xFF4F3F4A),
    onSurfaceVariant = Color(0xFFD2C2C7),

    outline = Color(0xFF9B8D91),
    outlineVariant = Color(0xFF4F3F4A),

    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFF80AB)
)

// Romantic serif font family (system default serif)
val RomanticFontFamily = FontFamily.Serif

@Composable
fun DevadasuDiaryTheme(
    isDark: Boolean,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> RomanticDarkPalette
        else -> RomanticLightPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RomanticTypography,
        content = content
    )
}


val RomanticTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RomanticFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)