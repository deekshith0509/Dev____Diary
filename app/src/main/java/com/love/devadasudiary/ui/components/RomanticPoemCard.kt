package com.love.devadasudiary.ui.components

import android.content.Intent
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.love.devadasudiary.core.DiaryDimens
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin

private const val MENU_ID_SHARE = Menu.FIRST + 100

/**
 * Renders a poem inside a styled card. Backed by a [TextView] driven by
 * Markwon for proper Telugu/RTL layout — Compose's text engine doesn't
 * shape complex scripts as well in this version of Material 3.
 *
 * Optimizations vs. the previous version:
 *  - Markdown is only re-parsed when [poem] actually changes.
 *  - TextView style mutation only happens when the related parameter
 *    changes (cheap diff check via tags).
 *  - The custom selection callback is a top-level class so the Composable
 *    body doesn't re-allocate it on every recomposition.
 */
@Composable
fun RomanticPoemCard(
    poem: String,
    fontSize: Float,
    lineSpacing: Float,
    horizontalPadding: Float,
    centerAlign: Boolean
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val markwon = remember(context) {
        Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .build()
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(DiaryDimens.CardCornerLarge),
        colors = CardDefaults.cardColors(containerColor = surface.copy(alpha = 0.92f)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    primary.copy(alpha = 0.65f),
                    secondary.copy(alpha = 0.30f),
                    Color.Transparent
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            primary.copy(alpha = 0.06f),
                            Color.Transparent,
                            secondary.copy(alpha = 0.04f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = horizontalPadding.dp, vertical = 20.dp)
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    factory = { ctx ->
                        TextView(ctx).apply {
                            overScrollMode = View.OVER_SCROLL_NEVER
                            typeface = Typeface.SERIF
                            setTextIsSelectable(true)
                            customSelectionActionModeCallback =
                                ShareSelectionCallback(this)
                        }
                    },
                    update = { textView ->
                        // Apply the easy properties unconditionally — they're cheap.
                        textView.setTextColor(onSurface.toArgb())
                        textView.highlightColor = primary.copy(alpha = 0.35f).toArgb()
                        textView.textAlignment =
                            if (centerAlign) View.TEXT_ALIGNMENT_CENTER
                            else View.TEXT_ALIGNMENT_TEXT_START

                        // Diff text/font/spacing so we don't re-parse markdown on
                        // every theme/recomposition tick.
                        val newSig = TextSignature(poem, fontSize, lineSpacing)
                        val prev = textView.getTag(R_TAG) as? TextSignature
                        if (prev?.poem != newSig.poem) {
                            markwon.setMarkdown(textView, newSig.poem)
                        }
                        if (prev?.fontSize != newSig.fontSize) {
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSig.fontSize)
                        }
                        if (prev?.lineSpacing != newSig.lineSpacing ||
                            prev.fontSize != newSig.fontSize) {
                            // Multiplier tied to font size; recompute when either changes.
                            val multiplier = (newSig.lineSpacing / newSig.fontSize)
                                .coerceAtLeast(1.0f)
                            textView.setLineSpacing(0f, multiplier)
                        }
                        textView.setTag(R_TAG, newSig)
                    }
                )
            }
        }
    }
}

/** Snapshot of the inputs that affect TextView mutation. */
private data class TextSignature(
    val poem: String,
    val fontSize: Float,
    val lineSpacing: Float
)

private const val R_TAG = 0x0F002001

/**
 * Adds a "Share" item to the text-selection action mode and dispatches
 * the selected substring through ACTION_SEND.
 */
private class ShareSelectionCallback(
    private val textView: TextView
) : ActionMode.Callback2() {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        menu.add(Menu.NONE, MENU_ID_SHARE, Menu.NONE, "Share")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (item.itemId != MENU_ID_SHARE) return false

        val full = textView.text?.toString().orEmpty()
        val start = textView.selectionStart.coerceIn(0, full.length)
        val end = textView.selectionEnd.coerceIn(0, full.length)
        val selected = if (start < end) full.substring(start, end) else null

        if (!selected.isNullOrBlank()) {
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, selected)
            }
            // FLAG_ACTIVITY_NEW_TASK guards against the AndroidView's host
            // context not being an Activity (e.g. inside a Dialog).
            val chooser = Intent.createChooser(send, "Share this love...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            textView.context.startActivity(chooser)
        }
        mode.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) = Unit
}
