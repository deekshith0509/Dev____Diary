package com.love.devadasudiary

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.love.devadasudiary.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ----------------------------
// DATA MODEL
// ----------------------------
data class Poem(
    val id: String,
    val title: String,
    val subtitle: String,
    val gistUrl: String,
    val content: String = ""
)

// ----------------------------
// UI STATE
// ----------------------------
sealed class PoetryUiState {
    object Loading : PoetryUiState()
    data class Success(val poem: Poem) : PoetryUiState()
    data class Error(val title: String, val message: String) : PoetryUiState()
}

// ----------------------------
// VIEWMODEL
// ----------------------------
class PoetryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PoetryRepository(app.applicationContext)

val poems = listOf(

    Poem(
        id = "1",
        title = "దేవదాసు డైరీ",
        subtitle = "గత క్షణాల నిశ్శబ్ద జ్ఞాపకాలు",
        gistUrl = "https://gist.githubusercontent.com/deekshith0509/41a9e59c134f6586450a3cfecfc42f14/raw/gistfile1.txt"
    ),

    Poem(
        id = "2",
        title = "ప్రేమలేఖ",
        subtitle = "మనసులో దాచుకున్న స్వీకారం",
        gistUrl = "https://gist.githubusercontent.com/deekshith0509/80ddbe213135d1dc5c9066273f1df9d4/raw/loveletter.txt"
    ),

    Poem(
        id = "3",
        title = "ఇక నుంచి...",
        subtitle = "మనసును కఠినం చేసుకున్న రోజులు",
        gistUrl = "https://gist.githubusercontent.com/deekshith0509/1c0bdf62612a1c765ee2299fcf9191eb/raw/Eternal.txt"
    )
)

    // ----------------------------
    // DATASTORE KEYS
    // ----------------------------
    private val KEY_FONT_SIZE    = floatPreferencesKey("font_size")
    private val KEY_LINE_SPACING = floatPreferencesKey("line_spacing")
    private val KEY_PADDING      = floatPreferencesKey("poem_padding")
    private val KEY_CENTER_ALIGN = booleanPreferencesKey("center_align")
    private val KEY_DARK_THEME   = booleanPreferencesKey("dark_theme")
    private val KEY_FAVORITES    = stringSetPreferencesKey("favorite_ids")
    private val KEY_LAST_POEM    = stringPreferencesKey("last_poem_id")

    // ----------------------------
    // STATE
    // ----------------------------
    private val _uiState       = MutableStateFlow<PoetryUiState>(PoetryUiState.Loading)
    val uiState: StateFlow<PoetryUiState> = _uiState.asStateFlow()

    private val _fontSize      = MutableStateFlow(14f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _lineSpacing   = MutableStateFlow(14f)
    val lineSpacing: StateFlow<Float> = _lineSpacing.asStateFlow()

    private val _poemPadding   = MutableStateFlow(21f)
    val poemPadding: StateFlow<Float> = _poemPadding.asStateFlow()

    private val _centerAlign   = MutableStateFlow(false)
    val centerAlign: StateFlow<Boolean> = _centerAlign.asStateFlow()

    private val _isDarkTheme   = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _favoriteIds   = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _currentPoemId = MutableStateFlow(poems.first().id)
    val currentPoemId: StateFlow<String> = _currentPoemId.asStateFlow()

    private var currentRawContent = ""

    init {
        viewModelScope.launch {
            val prefs = getApplication<Application>().applicationContext.dataStore.data.first()

            _fontSize.value    = prefs[KEY_FONT_SIZE]    ?: 14f
            _lineSpacing.value = prefs[KEY_LINE_SPACING] ?: 14f
            _poemPadding.value = prefs[KEY_PADDING]      ?: 21f
            _centerAlign.value = prefs[KEY_CENTER_ALIGN] ?: false
            _isDarkTheme.value = prefs[KEY_DARK_THEME]   ?: true
            _favoriteIds.value = prefs[KEY_FAVORITES]    ?: emptySet()

            val lastId = prefs[KEY_LAST_POEM]
            if (!lastId.isNullOrBlank() && poems.any { it.id == lastId }) {
                _currentPoemId.value = lastId
            }

            loadPoem(_currentPoemId.value, forceNetwork = false)
        }
    }

    // ----------------------------
    // SETTINGS
    // ----------------------------
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        save(KEY_DARK_THEME, _isDarkTheme.value)
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size.coerceIn(10f, 34f)
        save(KEY_FONT_SIZE, _fontSize.value)
    }

    fun setLineSpacing(value: Float) {
        _lineSpacing.value = value.coerceIn(8f, 30f)
        save(KEY_LINE_SPACING, _lineSpacing.value)
    }

    fun setPoemPadding(value: Float) {
        _poemPadding.value = value.coerceIn(8f, 48f)
        save(KEY_PADDING, _poemPadding.value)
    }

    fun setCenterAlign(enabled: Boolean) {
        _centerAlign.value = enabled
        save(KEY_CENTER_ALIGN, enabled)
    }

    // ----------------------------
    // POEM SELECTION
    // ----------------------------
    fun selectPoemById(poemId: String) {
        if (poems.none { it.id == poemId }) return
        _currentPoemId.value = poemId
        save(KEY_LAST_POEM, poemId)
        loadPoem(poemId, forceNetwork = false)
        vibrate()
    }

    fun refreshPoem() {
        loadPoem(_currentPoemId.value, forceNetwork = true)
        vibrate()
    }

    fun toggleFavorite(poemId: String) {
        val updated = _favoriteIds.value.toMutableSet().also {
            if (poemId in it) it.remove(poemId) else it.add(poemId)
        }
        _favoriteIds.value = updated
        viewModelScope.launch {
            getApplication<Application>().applicationContext.dataStore.edit {
                it[KEY_FAVORITES] = updated
            }
        }
        vibrate()
    }



    // ----------------------------
    // LOADING
    // ----------------------------
    private fun loadPoem(poemId: String, forceNetwork: Boolean) {
        viewModelScope.launch {
            _uiState.value = PoetryUiState.Loading

            val meta = poems.firstOrNull { it.id == poemId } ?: run {
                _uiState.value = PoetryUiState.Error("Missing page", "This poem doesn't exist.")
                return@launch
            }

            try {
                if (!forceNetwork) {
                    val cached = repo.loadCachedPoem(meta.id)
                    if (!cached.isNullOrBlank()) {
                        currentRawContent = cached
                        _uiState.value = PoetryUiState.Success(meta.copy(content = cached))
                        return@launch
                    }
                }

                val text = withContext(Dispatchers.IO) {
                    repo.fetchPoemFromNetwork(meta.gistUrl)
                }.replace("\uFEFF", "")

                if (text.isBlank()) throw Exception("Poem file is empty.")

                currentRawContent = text
                repo.savePoem(meta.id, text)
                _uiState.value = PoetryUiState.Success(meta.copy(content = text))

            } catch (e: Exception) {
                val cached = repo.loadCachedPoem(meta.id)
                if (!cached.isNullOrBlank()) {
                    currentRawContent = cached
                    _uiState.value = PoetryUiState.Success(meta.copy(content = cached))
                } else {
                    _uiState.value = PoetryUiState.Error(
                        title = "Couldn't reach the diary",
                        message = "Check your connection and tap Refresh.\n\n${e.message}"
                    )
                }
            }
        }
    }

    // ----------------------------
    // HELPERS
    // ----------------------------
    private fun vibrate() {
        val ctx = getApplication<Application>().applicationContext
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(18)
            }
        } catch (_: Exception) {}
    }

    private fun <T> save(key: androidx.datastore.preferences.core.Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            getApplication<Application>().applicationContext.dataStore.edit { it[key] = value }
        }
    }
}