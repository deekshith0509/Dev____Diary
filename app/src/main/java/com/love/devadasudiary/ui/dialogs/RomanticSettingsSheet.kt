package com.love.devadasudiary.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.love.devadasudiary.ui.theme.RomanticFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomanticSettingsSheet(
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,

    lineSpacing: Float,
    onLineSpacingChange: (Float) -> Unit,

    poemPadding: Float,
    onPoemPaddingChange: (Float) -> Unit,

    centerAlign: Boolean,
    onCenterAlignChange: (Boolean) -> Unit,

    onDismiss: () -> Unit
) {
    var localFontSize    by remember(fontSize)      { mutableFloatStateOf(fontSize) }
    var localLineSpacing by remember(lineSpacing)   { mutableFloatStateOf(lineSpacing) }
    var localPadding     by remember(poemPadding)   { mutableFloatStateOf(poemPadding) }
    var localAlign       by remember(centerAlign)   { mutableStateOf(centerAlign) }
    var showHint         by remember               { mutableStateOf(true) }

    val primary   = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface

    val previewText = remember {
        "ఇది నా మనసు రాసిన ప్రేమ...\nనా మాటల్లో నిన్ను దాచుకున్నాను..."
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Text(
                "✦ Settings ✦",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Tune your diary like a heartbeat...",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light
                ),
                color = onSurface.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            // Preview Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(primary.copy(0.45f), secondary.copy(0.35f), Color.Transparent)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = previewText,
                        style = TextStyle(
                            fontSize = localFontSize.sp,
                            fontFamily = RomanticFontFamily,
                            fontWeight = FontWeight.Light,
                            color = onSurface.copy(alpha = 0.92f),
                            lineHeight = (localFontSize + localLineSpacing).sp,
                            textAlign = if (localAlign) TextAlign.Center else TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = localPadding.dp)
                    )

                    if (showHint) {
                        Text(
                            text = "Preview",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = FontStyle.Italic
                            ),
                            color = onSurface.copy(alpha = 0.55f),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Font Size
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Font Size", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${localFontSize.toInt()} sp",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primary
                    )
                )
            }
            Slider(
                value = localFontSize,
                onValueChange = { localFontSize = it; onFontSizeChange(it) },
                valueRange = 10f..34f,
                steps = 11
            )

            Spacer(Modifier.height(14.dp))

            // Line Spacing
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Line Spacing", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${localLineSpacing.toInt()}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = secondary
                    )
                )
            }
            Slider(
                value = localLineSpacing,
                onValueChange = { localLineSpacing = it; onLineSpacingChange(it) },
                valueRange = 6f..30f,
                steps = 11
            )

            Spacer(Modifier.height(14.dp))

            // Padding
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Reading Padding", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${localPadding.toInt()} dp",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = primary
                    )
                )
            }
            Slider(
                value = localPadding,
                onValueChange = { localPadding = it; onPoemPaddingChange(it) },
                valueRange = 0f..48f,
                steps = 11
            )

            Spacer(Modifier.height(12.dp))

            // Center Align
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Center Align Text")
                Switch(
                    checked = localAlign,
                    onCheckedChange = { localAlign = it; onCenterAlignChange(it) }
                )
            }

            // Preview Hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Preview Hint")
                Switch(checked = showHint, onCheckedChange = { showHint = it })
            }

            Spacer(Modifier.height(18.dp))

            // Reset
            OutlinedButton(
                onClick = {
                    localFontSize    = 14f; onFontSizeChange(14f)
                    localLineSpacing = 14f; onLineSpacingChange(14f)
                    localPadding     = 21f; onPoemPaddingChange(21f)
                    localAlign       = false; onCenterAlignChange(false)
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                border = BorderStroke(1.dp, primary.copy(alpha = 0.6f))
            ) {
                Text("Reset to Default", fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = primary)
            }

            Spacer(Modifier.height(12.dp))

            // Done
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Done", fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            }
        }
    }
}