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
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

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
    data class Error(
        val title: String,
        val message: String,
        val canRetry: Boolean = true
    ) : PoetryUiState()
}

// ----------------------------
// VIEWMODEL
// ----------------------------
class PoetryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PoetryRepository(app.applicationContext)

    // ----------------------------
    // YOUR POEM COLLECTION
    // ----------------------------
    val poems: List<Poem> = listOf(
        Poem(
            id = "1",
            title = "Devadasu Diary",
            subtitle = "Words written from silence",
            gistUrl = "https://gist.githubusercontent.com/deekshith0509/41a9e59c134f6586450a3cfecfc42f14/raw/gistfile1.txt"
        ),
        Poem(
            id = "2",
            title = "Love Letter",
            subtitle = "A confession in ink",
            gistUrl = "https://gist.githubusercontent.com/deekshith0509/80ddbe213135d1dc5c9066273f1df9d4/raw/loveletter.txt"
        ),
        Poem(
            id = "3",
            title = "Eternal",
            subtitle = "A forever kind of pain",
            gistUrl = "https://gist.githubusercontent.com/deekshith0509/1c0bdf62612a1c765ee2299fcf9191eb/raw/Eternal.txt"
        )
    )

    // ----------------------------
    // DATASTORE KEYS
    // ----------------------------
    private val FONT_SIZE_KEY = floatPreferencesKey("font_size")
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    private val STRIP_HTML_KEY = booleanPreferencesKey("strip_html")
    private val FAVORITE_IDS_KEY = stringSetPreferencesKey("favorite_ids")
    private val LAST_POEM_ID_KEY = stringPreferencesKey("last_poem_id")

    // NEW SETTINGS KEYS
    private val LINE_SPACING_KEY = floatPreferencesKey("line_spacing")
    private val POEM_PADDING_KEY = floatPreferencesKey("poem_padding")
    private val CENTER_ALIGN_KEY = booleanPreferencesKey("center_align")

    // ----------------------------
    // INTERNAL STATE
    // ----------------------------
    private val _uiState = MutableStateFlow<PoetryUiState>(PoetryUiState.Loading)
    val uiState: StateFlow<PoetryUiState> = _uiState.asStateFlow()

    private val _fontSize = MutableStateFlow(18f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _lineSpacing = MutableStateFlow(12f)
    val lineSpacing: StateFlow<Float> = _lineSpacing.asStateFlow()

    private val _poemPadding = MutableStateFlow(22f)
    val poemPadding: StateFlow<Float> = _poemPadding.asStateFlow()

    private val _centerAlign = MutableStateFlow(true)
    val centerAlign: StateFlow<Boolean> = _centerAlign.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    /**
     * WARNING:
     * Stripping HTML can break poem formatting.
     * Default = false for poetry apps.
     */
    private val _stripHtml = MutableStateFlow(false)
    val stripHtml: StateFlow<Boolean> = _stripHtml.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _currentPoemId = MutableStateFlow(poems.first().id)
    val currentPoemId: StateFlow<String> = _currentPoemId.asStateFlow()

    private var currentRawContent: String = ""

    // ----------------------------
    // INIT
    // ----------------------------
    init {
        restorePreferencesAndLoad()
    }

    private fun restorePreferencesAndLoad() {
        viewModelScope.launch {

            val prefs = getApplication<Application>()
                .applicationContext
                .dataStore
                .data
                .first()

            _fontSize.value = prefs[FONT_SIZE_KEY] ?: 18f
            _isDarkTheme.value = prefs[DARK_THEME_KEY] ?: true
            _stripHtml.value = prefs[STRIP_HTML_KEY] ?: false
            _favoriteIds.value = prefs[FAVORITE_IDS_KEY] ?: emptySet()

            // NEW SETTINGS RESTORE
            _lineSpacing.value = prefs[LINE_SPACING_KEY] ?: 12f
            _poemPadding.value = prefs[POEM_PADDING_KEY] ?: 22f
            _centerAlign.value = prefs[CENTER_ALIGN_KEY] ?: true

            val lastPoemId = prefs[LAST_POEM_ID_KEY]
            if (!lastPoemId.isNullOrBlank() && poems.any { it.id == lastPoemId }) {
                _currentPoemId.value = lastPoemId
            }

            loadPoemById(_currentPoemId.value, forceNetwork = false)
        }
    }

    // ----------------------------
    // UI CONTROLS
    // ----------------------------
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        savePreference(DARK_THEME_KEY, _isDarkTheme.value)
    }

    fun setFontSize(size: Float) {
        val safeSize = size.coerceIn(14f, 34f)
        _fontSize.value = safeSize
        savePreference(FONT_SIZE_KEY, safeSize)
    }

    fun setLineSpacing(value: Float) {
        val safe = value.coerceIn(8f, 22f)
        _lineSpacing.value = safe
        savePreference(LINE_SPACING_KEY, safe)
    }

    fun setPoemPadding(value: Float) {
        val safe = value.coerceIn(10f, 40f)
        _poemPadding.value = safe
        savePreference(POEM_PADDING_KEY, safe)
    }

    fun setCenterAlign(enabled: Boolean) {
        _centerAlign.value = enabled
        savePreference(CENTER_ALIGN_KEY, enabled)
    }

    fun setStripHtml(enabled: Boolean) {
        _stripHtml.value = enabled
        savePreference(STRIP_HTML_KEY, enabled)
        updateDisplayedPoem()
    }

    fun selectPoemById(poemId: String) {
        if (poems.none { it.id == poemId }) return

        _currentPoemId.value = poemId
        savePreference(LAST_POEM_ID_KEY, poemId)

        loadPoemById(poemId, forceNetwork = false)
        vibrateSoft()
    }

    fun refreshPoem() {
        loadPoemById(_currentPoemId.value, forceNetwork = true)
        vibrateSoft()
    }

    fun toggleFavorite(poemId: String) {
        val newSet = _favoriteIds.value.toMutableSet()

        if (newSet.contains(poemId)) newSet.remove(poemId)
        else newSet.add(poemId)

        _favoriteIds.value = newSet

        viewModelScope.launch {
            getApplication<Application>()
                .applicationContext
                .dataStore
                .edit { prefs ->
                    prefs[FAVORITE_IDS_KEY] = newSet
                }
        }

        vibrateSoft()
    }

    // ----------------------------
    // POEM LOADING LOGIC
    // ----------------------------
    private fun loadPoemById(poemId: String, forceNetwork: Boolean) {
        viewModelScope.launch {

            _uiState.value = PoetryUiState.Loading

            val poemMeta = poems.firstOrNull { it.id == poemId }
            if (poemMeta == null) {
                _uiState.value = PoetryUiState.Error(
                    title = "Missing page",
                    message = "This poem does not exist in your diary list."
                )
                return@launch
            }

            try {

                // Cache first (if allowed)
                if (!forceNetwork) {
                    val cached = repo.loadCachedPoem(poemMeta.id)
                    if (!cached.isNullOrBlank()) {
                        currentRawContent = cached
                        updateDisplayedPoem(poemMeta.copy(content = cached))
                        return@launch
                    }
                }

                // Fetch from network
                val networkText = withContext(Dispatchers.IO) {
                    repo.fetchPoemFromNetwork(poemMeta.gistUrl)
                }

                val cleaned = cleanPoem(networkText)

                // Detect wrong URL (HTML response)
                if (looksLikeHtml(cleaned)) {
                    throw Exception("Wrong Gist URL (you are fetching HTML instead of RAW text).")
                }

                if (cleaned.isBlank()) {
                    throw Exception("Your poem file is empty.")
                }

                currentRawContent = cleaned
                repo.savePoem(poemMeta.id, cleaned)

                updateDisplayedPoem(poemMeta.copy(content = cleaned))

            } catch (e: Exception) {

                // Offline fallback: try cache
                val cached = repo.loadCachedPoem(poemMeta.id)

                if (!cached.isNullOrBlank()) {
                    currentRawContent = cached
                    updateDisplayedPoem(poemMeta.copy(content = cached))
                } else {
                    _uiState.value = PoetryUiState.Error(
                        title = "Love couldn’t reach the page",
                        message = buildRomanticErrorMessage(e),
                        canRetry = true
                    )
                }
            }
        }
    }

    private fun updateDisplayedPoem(poemOverride: Poem? = null) {

        val poemMeta = poemOverride ?: poems.first { it.id == _currentPoemId.value }
        val baseText = poemOverride?.content ?: currentRawContent

        val finalText = if (_stripHtml.value) {
            stripHtmlSafely(baseText)
        } else {
            baseText
        }

        _uiState.value = PoetryUiState.Success(
            poemMeta.copy(content = finalText)
        )
    }

    // ----------------------------
    // COPY / SHARE
    // ----------------------------
    fun copyPoemToClipboard() {
        val text = currentRawContent
        if (text.isBlank()) return

        val clipboard = getApplication<Application>()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText("Devadasu Diary Poem", text)
        )

        vibrateSoft()
    }

    fun sharePoem(context: Context) {
        val text = currentRawContent
        if (text.isBlank()) return

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        context.startActivity(Intent.createChooser(intent, "Share this love..."))
        vibrateSoft()
    }

    // ----------------------------
    // HELPERS
    // ----------------------------
    private fun cleanPoem(text: String): String {
        return text.replace("\uFEFF", "")
    }

    private fun looksLikeHtml(text: String): Boolean {
        val t = text.trim().lowercase()
        return t.startsWith("<!doctype html") ||
                t.startsWith("<html") ||
                t.contains("<head") ||
                t.contains("<body") ||
                t.contains("</html>")
    }

    private fun stripHtmlSafely(text: String): String {
        if (!text.contains('<')) return text

        val withoutTags = Pattern.compile("<[^>]*>").matcher(text).replaceAll("")

        return withoutTags
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("\r\n", "\n")
    }

    private fun buildRomanticErrorMessage(e: Exception): String {
        val raw = e.message ?: "Unknown error"

        return """
            The diary couldn’t open...
            
            ✦ Reason:
            $raw
            
            ✦ Fix:
            • Check internet connection
            • Ensure your Gist URL is RAW
            • Tap Refresh
            
            — Devadasu Diary
        """.trimIndent()
    }

    private fun vibrateSoft() {
        val context = getApplication<Application>().applicationContext

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(18)
            }
        } catch (_: Exception) {
            // ignore vibration errors
        }
    }

    private fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            getApplication<Application>()
                .applicationContext
                .dataStore
                .edit { prefs ->
                    prefs[key] = value
                }
        }
    }
}
