package com.love.devadasudiary.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.love.devadasudiary.core.DiaryRanges
import com.love.devadasudiary.core.Haptics
import com.love.devadasudiary.data.PoemCatalog
import com.love.devadasudiary.data.PoetryRepository
import com.love.devadasudiary.data.model.Poem
import com.love.devadasudiary.data.prefs.PreferencesRepository
import com.love.devadasudiary.data.prefs.UserPreferences
import com.love.devadasudiary.ui.state.PoetryUiState
import com.love.devadasudiary.ui.state.ReadingSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Single source of UI truth. Three observable streams:
 *
 *  - [uiState]         : Loading / Success / Error for the current poem.
 *  - [readingSettings] : Font size, line spacing, padding, center align.
 *  - [appState]        : Theme, dynamic colors, haptics, favorites, selection.
 *
 * Concurrency: poem loads run as a single tracked [Job] so rapid taps on
 * the bottom navigation cancel the previous load instead of racing it.
 */
class PoetryViewModel(
    application: Application,
    private val poetryRepo: PoetryRepository,
    private val preferencesRepo: PreferencesRepository,
    private val haptics: Haptics
) : AndroidViewModel(application) {

    val poems: List<Poem> = PoemCatalog.poems

    // ---------------------------------------------------------------- state
    private val _uiState = MutableStateFlow<PoetryUiState>(PoetryUiState.Loading)
    val uiState: StateFlow<PoetryUiState> = _uiState.asStateFlow()

    val readingSettings: StateFlow<ReadingSettings> = preferencesRepo.preferences
        .map { it.toReadingSettings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = ReadingSettings(
                fontSize = com.love.devadasudiary.core.DiaryDefaults.FONT_SIZE_SP,
                lineSpacing = com.love.devadasudiary.core.DiaryDefaults.LINE_SPACING_SP,
                padding = com.love.devadasudiary.core.DiaryDefaults.PADDING_DP,
                centerAlign = com.love.devadasudiary.core.DiaryDefaults.CENTER_ALIGN
            )
        )

    val appState: StateFlow<AppState> = preferencesRepo.preferences
        .map { it.toAppState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = AppState(
                isDarkTheme = com.love.devadasudiary.core.DiaryDefaults.DARK_THEME,
                dynamicColor = com.love.devadasudiary.core.DiaryDefaults.DYNAMIC_COLOR,
                hapticsEnabled = com.love.devadasudiary.core.DiaryDefaults.HAPTICS_ENABLED,
                favoriteIds = emptySet(),
                currentPoemId = poems.first().id
            )
        )

    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            // Pull a one-shot snapshot to figure out which poem to open first.
            val snapshot: UserPreferences = preferencesRepo.preferences.first()
            val resumePoemId = snapshot.lastPoemId
                ?.takeIf { id -> poems.any { it.id == id } }
                ?: PoemCatalog.firstId()

            // Seed the AppState's currentPoemId before first emission settles.
            preferencesRepo.setLastPoemId(resumePoemId)
            loadPoem(resumePoemId, forceNetwork = false)
        }
    }

    // -------------------------------------------------- public commands ---

    fun toggleTheme() {
        viewModelScope.launch {
            val current = appState.value.isDarkTheme
            preferencesRepo.setDarkTheme(!current)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { preferencesRepo.setDynamicColor(enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepo.setHapticsEnabled(enabled) }
    }

    fun setFontSize(size: Float) {
        viewModelScope.launch {
            preferencesRepo.setFontSize(size.coerceIn(DiaryRanges.FONT_SIZE))
        }
    }

    fun setLineSpacing(value: Float) {
        viewModelScope.launch {
            preferencesRepo.setLineSpacing(value.coerceIn(DiaryRanges.LINE_SPACING))
        }
    }

    fun setPoemPadding(value: Float) {
        viewModelScope.launch {
            preferencesRepo.setPadding(value.coerceIn(DiaryRanges.PADDING))
        }
    }

    fun setCenterAlign(enabled: Boolean) {
        viewModelScope.launch { preferencesRepo.setCenterAlign(enabled) }
    }

    fun resetReadingSettings() {
        viewModelScope.launch {
            preferencesRepo.setFontSize(com.love.devadasudiary.core.DiaryDefaults.FONT_SIZE_SP)
            preferencesRepo.setLineSpacing(com.love.devadasudiary.core.DiaryDefaults.LINE_SPACING_SP)
            preferencesRepo.setPadding(com.love.devadasudiary.core.DiaryDefaults.PADDING_DP)
            preferencesRepo.setCenterAlign(com.love.devadasudiary.core.DiaryDefaults.CENTER_ALIGN)
        }
    }

    fun selectPoemById(poemId: String) {
        if (poems.none { it.id == poemId }) return
        if (poemId == appState.value.currentPoemId &&
            _uiState.value is PoetryUiState.Success) return
        viewModelScope.launch { preferencesRepo.setLastPoemId(poemId) }
        loadPoem(poemId, forceNetwork = false)
        tickIfEnabled()
    }

    fun refreshPoem() {
        loadPoem(appState.value.currentPoemId, forceNetwork = true)
        tickIfEnabled()
    }

    fun toggleFavorite(poemId: String) {
        if (poems.none { it.id == poemId }) return
        viewModelScope.launch {
            val current = appState.value.favoriteIds
            val updated = if (poemId in current) current - poemId else current + poemId
            preferencesRepo.setFavorites(updated)
        }
        tickIfEnabled()
    }

    // ------------------------------------------------------- internals ----

    private fun loadPoem(poemId: String, forceNetwork: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { PoetryUiState.Loading }

            val meta = PoemCatalog.findById(poemId) ?: run {
                _uiState.update {
                    PoetryUiState.Error("Missing page", "This poem doesn't exist.")
                }
                return@launch
            }

            try {
                if (!forceNetwork) {
                    val cached = poetryRepo.loadCachedPoem(meta.id)
                    if (!cached.isNullOrBlank()) {
                        _uiState.update {
                            PoetryUiState.Success(meta.copy(content = cached))
                        }
                        // Continue silently to refresh in background; do not
                        // overwrite cache UI on failure.
                        runCatching { poetryRepo.fetchPoemFromNetwork(meta.gistUrl) }
                            .onSuccess { fresh ->
                                if (fresh.isNotBlank() && fresh != cached) {
                                    poetryRepo.savePoem(meta.id, fresh)
                                    _uiState.update {
                                        PoetryUiState.Success(meta.copy(content = fresh))
                                    }
                                }
                            }
                        return@launch
                    }
                }

                val text = poetryRepo.fetchPoemFromNetwork(meta.gistUrl)
                if (text.isBlank()) throw IOException("Poem file is empty.")

                poetryRepo.savePoem(meta.id, text)
                _uiState.update { PoetryUiState.Success(meta.copy(content = text)) }

            } catch (ce: CancellationException) {
                throw ce // Don't swallow structured concurrency cancellation.
            } catch (e: Exception) {
                val cached = poetryRepo.loadCachedPoem(meta.id)
                if (!cached.isNullOrBlank()) {
                    _uiState.update {
                        PoetryUiState.Success(meta.copy(content = cached))
                    }
                } else {
                    _uiState.update {
                        PoetryUiState.Error(
                            title = "Couldn't reach the diary",
                            message = "Check your connection and tap Refresh.\n\n${e.message ?: "Unknown error"}"
                        )
                    }
                }
            }
        }
    }

    private fun tickIfEnabled() {
        if (appState.value.hapticsEnabled) haptics.tick()
    }

    private fun UserPreferences.toReadingSettings() = ReadingSettings(
        fontSize = fontSize,
        lineSpacing = lineSpacing,
        padding = padding,
        centerAlign = centerAlign
    )

    private fun UserPreferences.toAppState() = AppState(
        isDarkTheme = darkTheme,
        dynamicColor = dynamicColor,
        hapticsEnabled = hapticsEnabled,
        favoriteIds = favorites,
        currentPoemId = lastPoemId
            ?.takeIf { id -> poems.any { it.id == id } }
            ?: PoemCatalog.firstId()
    )

    companion object {
        /**
         * Keep state alive briefly across config changes so we don't
         * re-collect from DataStore on rotation.
         */
        private const val STOP_TIMEOUT_MS = 5_000L

        fun factory(application: Application): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PoetryViewModel(
                        application = application,
                        poetryRepo = PoetryRepository(application),
                        preferencesRepo = PreferencesRepository(application),
                        haptics = Haptics(application)
                    )
                }
            }

        @Suppress("UNCHECKED_CAST")
        fun <T : ViewModel> ViewModelProvider.Factory.cast(): T = this as T
    }
}

/**
 * Group of non-reading state so the screen can subscribe to one StateFlow
 * instead of seven, cutting recomposition fan-out.
 */
@androidx.compose.runtime.Immutable
data class AppState(
    val isDarkTheme: Boolean,
    val dynamicColor: Boolean,
    val hapticsEnabled: Boolean,
    val favoriteIds: Set<String>,
    val currentPoemId: String
)
