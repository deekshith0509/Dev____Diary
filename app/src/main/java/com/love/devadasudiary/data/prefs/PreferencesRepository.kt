package com.love.devadasudiary.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.love.devadasudiary.core.DiaryDefaults
import com.love.devadasudiary.core.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Centralizes all DataStore reads/writes for app preferences.
 *
 * Why: previously the ViewModel touched the raw DataStore from multiple
 * spots, with no error handling — any IOException at startup would crash
 * the app. This class collapses the surface to a single typed snapshot
 * (UserPreferences) plus per-key writers, with `.catch` fallbacks.
 */
class PreferencesRepository(context: Context) {

    private val appContext = context.applicationContext

    val preferences: Flow<UserPreferences> = appContext.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map(::toUserPreferences)
        .distinctUntilChanged()

    suspend fun setFontSize(value: Float) = put(PreferencesKeys.FONT_SIZE, value)
    suspend fun setLineSpacing(value: Float) = put(PreferencesKeys.LINE_SPACING, value)
    suspend fun setPadding(value: Float) = put(PreferencesKeys.PADDING, value)
    suspend fun setCenterAlign(value: Boolean) = put(PreferencesKeys.CENTER_ALIGN, value)
    suspend fun setDarkTheme(value: Boolean) = put(PreferencesKeys.DARK_THEME, value)
    suspend fun setDynamicColor(value: Boolean) = put(PreferencesKeys.DYNAMIC_COLOR, value)
    suspend fun setHapticsEnabled(value: Boolean) = put(PreferencesKeys.HAPTICS, value)
    suspend fun setLastPoemId(value: String) = put(PreferencesKeys.LAST_POEM, value)
    suspend fun setFavorites(value: Set<String>) = put(PreferencesKeys.FAVORITES, value)

    private suspend fun <T> put(key: Preferences.Key<T>, value: T) {
        appContext.dataStore.edit { it[key] = value }
    }

    private fun toUserPreferences(prefs: Preferences) = UserPreferences(
        fontSize       = prefs[PreferencesKeys.FONT_SIZE]     ?: DiaryDefaults.FONT_SIZE_SP,
        lineSpacing    = prefs[PreferencesKeys.LINE_SPACING]  ?: DiaryDefaults.LINE_SPACING_SP,
        padding        = prefs[PreferencesKeys.PADDING]       ?: DiaryDefaults.PADDING_DP,
        centerAlign    = prefs[PreferencesKeys.CENTER_ALIGN]  ?: DiaryDefaults.CENTER_ALIGN,
        darkTheme      = prefs[PreferencesKeys.DARK_THEME]    ?: DiaryDefaults.DARK_THEME,
        dynamicColor   = prefs[PreferencesKeys.DYNAMIC_COLOR] ?: DiaryDefaults.DYNAMIC_COLOR,
        hapticsEnabled = prefs[PreferencesKeys.HAPTICS]       ?: DiaryDefaults.HAPTICS_ENABLED,
        favorites      = prefs[PreferencesKeys.FAVORITES]     ?: emptySet(),
        lastPoemId     = prefs[PreferencesKeys.LAST_POEM]
    )
}

data class UserPreferences(
    val fontSize: Float,
    val lineSpacing: Float,
    val padding: Float,
    val centerAlign: Boolean,
    val darkTheme: Boolean,
    val dynamicColor: Boolean,
    val hapticsEnabled: Boolean,
    val favorites: Set<String>,
    val lastPoemId: String?
)
