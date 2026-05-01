package com.love.devadasudiary.data.model

import androidx.compose.runtime.Immutable

/**
 * A single poem in the diary.
 *
 * @property id          Stable identifier used for caching and selection.
 * @property title       Title displayed in the top bar / nav bar.
 * @property subtitle    Italic subtitle shown below the title.
 * @property gistUrl     Raw markdown source.
 * @property content     Live markdown body (empty for the catalog stub).
 */
@Immutable
data class Poem(
    val id: String,
    val title: String,
    val subtitle: String,
    val gistUrl: String,
    val content: String = ""
)
