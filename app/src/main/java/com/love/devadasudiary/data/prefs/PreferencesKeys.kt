package com.love.devadasudiary.data.prefs

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

/**
 * All DataStore keys the app reads/writes. Defined as top-level so that
 * a new instance is not allocated on every ViewModel construction (which
 * was happening on every config change).
 */
internal object PreferencesKeys {
    val FONT_SIZE      = floatPreferencesKey("font_size")
    val LINE_SPACING   = floatPreferencesKey("line_spacing")
    val PADDING        = floatPreferencesKey("poem_padding")
    val CENTER_ALIGN   = booleanPreferencesKey("center_align")
    val DARK_THEME     = booleanPreferencesKey("dark_theme")
    val DYNAMIC_COLOR  = booleanPreferencesKey("dynamic_color")
    val HAPTICS        = booleanPreferencesKey("haptics_enabled")
    val FAVORITES      = stringSetPreferencesKey("favorite_ids")
    val LAST_POEM      = stringPreferencesKey("last_poem_id")
}
