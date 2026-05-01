package com.love.devadasudiary.core

import androidx.compose.ui.unit.dp

/**
 * Centralized defaults, ranges and dimensions for the Devadasu Diary app.
 *
 * Why a single object: settings ranges, defaults and shared dimensions were
 * previously duplicated across the ViewModel, settings sheet and UI components,
 * causing slider/clamp drift (e.g. line spacing slider ran 6..30 while VM
 * coerced 8..30, silently dropping the lower notch).
 */
object DiaryDefaults {
    const val FONT_SIZE_SP: Float = 14f
    const val LINE_SPACING_SP: Float = 14f
    const val PADDING_DP: Float = 21f
    const val CENTER_ALIGN: Boolean = false
    const val DARK_THEME: Boolean = true
    const val DYNAMIC_COLOR: Boolean = false
    const val HAPTICS_ENABLED: Boolean = true
}

object DiaryRanges {
    val FONT_SIZE: ClosedFloatingPointRange<Float> = 10f..34f
    val LINE_SPACING: ClosedFloatingPointRange<Float> = 8f..30f
    val PADDING: ClosedFloatingPointRange<Float> = 0f..48f

    /** Step counts kept consistent across UI sliders & internal validation. */
    const val FONT_SIZE_STEPS: Int = 11
    const val LINE_SPACING_STEPS: Int = 10
    const val PADDING_STEPS: Int = 11
}

object DiaryDimens {
    val ScreenHorizontalPadding = 10.dp
    val ScreenVerticalPadding = 14.dp
    val CardCornerLarge = 28.dp
    val CardCornerMedium = 18.dp
    val NavBarCornerTop = 26.dp
    val HeaderHeight = 54.dp
    val SettingsSheetCorner = 28.dp
}

object DiaryTimings {
    /** Settings persistence debounce — avoid one DataStore write per slider tick. */
    const val SETTINGS_WRITE_DEBOUNCE_MS: Long = 200L

    /** Network call timeouts. */
    const val CONNECT_TIMEOUT_SEC: Long = 15L
    const val READ_TIMEOUT_SEC: Long = 20L
    const val WRITE_TIMEOUT_SEC: Long = 15L

    /** Brief vibration for tactile selection feedback. */
    const val HAPTIC_DURATION_MS: Long = 18L
}
