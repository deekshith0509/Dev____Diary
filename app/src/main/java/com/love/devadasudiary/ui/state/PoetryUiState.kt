package com.love.devadasudiary.ui.state

import androidx.compose.runtime.Immutable
import com.love.devadasudiary.data.model.Poem

/**
 * UI states emitted by [com.love.devadasudiary.ui.PoetryViewModel].
 *
 * Made `Immutable` so Compose can skip unchanged recompositions.
 */
@Immutable
sealed interface PoetryUiState {
    data object Loading : PoetryUiState
    data class Success(val poem: Poem) : PoetryUiState
    data class Error(val title: String, val message: String) : PoetryUiState
}

/**
 * Reading-experience knobs that get persisted in DataStore.
 * Bundling them as one value avoids the seven-StateFlow soup the
 * earlier ViewModel exposed and makes `combine`-based debouncing trivial.
 */
@Immutable
data class ReadingSettings(
    val fontSize: Float,
    val lineSpacing: Float,
    val padding: Float,
    val centerAlign: Boolean
)
