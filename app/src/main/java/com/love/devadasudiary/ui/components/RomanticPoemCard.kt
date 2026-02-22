package com.love.devadasudiary.ui.components

import android.content.Intent
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin

private const val MENU_SHARE = Menu.FIRST + 100

@Composable
fun RomanticPoemCard(
    poem: String,
    fontSize: Float,
    lineSpacing: Float,
    horizontalPadding: Float,
    centerAlign: Boolean
) {
    val primary   = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface   = MaterialTheme.colorScheme.surface
    val context   = LocalContext.current
    val scrollState = rememberScrollState()

    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .build()
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = surface.copy(alpha = 0.92f)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(primary.copy(0.65f), secondary.copy(0.30f), Color.Transparent)
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(primary.copy(0.06f), Color.Transparent, secondary.copy(0.04f))
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

                            setTextColor(onSurface.toArgb())
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)

                            // Line spacing multiplier
                            setLineSpacing(0f, lineSpacing / fontSize)

                            typeface = Typeface.SERIF
                            overScrollMode = android.view.View.OVER_SCROLL_NEVER

                            textAlignment =
                                if (centerAlign)
                                    TEXT_ALIGNMENT_CENTER
                                else
                                    TEXT_ALIGNMENT_TEXT_START

                            // Enable text selection
                            setTextIsSelectable(true)

                            // Selection highlight color
                            highlightColor =
                                primary.copy(alpha = 0.35f).toArgb()

                            // Custom Share Option
                            customSelectionActionModeCallback =
                                object : ActionMode.Callback2() {

                                    override fun onCreateActionMode(
                                        mode: ActionMode,
                                        menu: Menu
                                    ): Boolean {
                                        menu.add(
                                            Menu.NONE,
                                            MENU_SHARE,
                                            Menu.NONE,
                                            "Share"
                                        ).setShowAsAction(
                                            MenuItem.SHOW_AS_ACTION_ALWAYS
                                        )
                                        return true
                                    }

                                    override fun onPrepareActionMode(
                                        mode: ActionMode,
                                        menu: Menu
                                    ) = false

                                    override fun onActionItemClicked(
                                        mode: ActionMode,
                                        item: MenuItem
                                    ): Boolean {

                                        if (item.itemId == MENU_SHARE) {

                                            val start =
                                                selectionStart.coerceAtLeast(0)
                                            val end =
                                                selectionEnd.coerceAtLeast(0)

                                            val selected =
                                                if (start < end)
                                                    text?.substring(start, end)
                                                else null

                                            if (!selected.isNullOrBlank()) {
                                                ctx.startActivity(
                                                    Intent.createChooser(
                                                        Intent(Intent.ACTION_SEND).apply {
                                                            type = "text/plain"
                                                            putExtra(
                                                                Intent.EXTRA_TEXT,
                                                                selected
                                                            )
                                                        },
                                                        "Share this love..."
                                                    )
                                                )
                                            }

                                            mode.finish()
                                            return true
                                        }

                                        return false
                                    }

                                    override fun onDestroyActionMode(
                                        mode: ActionMode
                                    ) {}
                                }

                            markwon.setMarkdown(this, poem)
                        }
                    },

                    update = { textView ->

                        textView.setTextColor(onSurface.toArgb())
                        textView.setTextSize(
                            TypedValue.COMPLEX_UNIT_SP,
                            fontSize
                        )

                        textView.setLineSpacing(
                            0f,
                            lineSpacing / fontSize
                        )

                        textView.textAlignment =
                            if (centerAlign)
                                TEXT_ALIGNMENT_CENTER
                            else
                                TEXT_ALIGNMENT_TEXT_START

                        markwon.setMarkdown(textView, poem)
                    }
                )
            }
        }
    }
}