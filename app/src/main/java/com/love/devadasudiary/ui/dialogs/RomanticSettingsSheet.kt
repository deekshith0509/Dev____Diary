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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.love.devadasudiary.core.DiaryDimens
import com.love.devadasudiary.core.DiaryRanges
import com.love.devadasudiary.ui.state.ReadingSettings
import com.love.devadasudiary.ui.theme.RomanticFontFamily

private const val PREVIEW_TEXT =
    "ఇది నా మనసు రాసిన ప్రేమ...\nనా మాటల్లో నిన్ను దాచుకున్నాను..."

/**
 * Reading-experience settings.
 *
 * The sheet now consumes the immutable [ReadingSettings] value rather than
 * five disjoint Float/Boolean pairs, and writes back through callbacks
 * directly to the ViewModel — no local mirror state, no slider/VM drift.
 * Slider ranges come from [DiaryRanges] so they can never disagree with
 * the validation in the ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomanticSettingsSheet(
    settings: ReadingSettings,
    hapticsEnabled: Boolean,
    dynamicColor: Boolean,
    onFontSizeChange: (Float) -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onPaddingChange: (Float) -> Unit,
    onCenterAlignChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val previewText = remember { PREVIEW_TEXT }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = DiaryDimens.SettingsSheetCorner,
            topEnd = DiaryDimens.SettingsSheetCorner
        ),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp))

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

            // Live preview ----------------------------------------------------
            Card(
                shape = RoundedCornerShape(DiaryDimens.CardCornerMedium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            primary.copy(alpha = 0.45f),
                            secondary.copy(alpha = 0.35f),
                            Color.Transparent
                        )
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
                            fontSize = settings.fontSize.sp,
                            fontFamily = RomanticFontFamily,
                            fontWeight = FontWeight.Light,
                            color = onSurface.copy(alpha = 0.92f),
                            lineHeight = (settings.fontSize + settings.lineSpacing).sp,
                            textAlign = if (settings.centerAlign) TextAlign.Center else TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = settings.padding.dp)
                    )
                    Text(
                        text = "Preview",
                        style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                        color = onSurface.copy(alpha = 0.55f),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            SliderRow(
                label = "Font Size",
                value = settings.fontSize,
                range = DiaryRanges.FONT_SIZE,
                steps = DiaryRanges.FONT_SIZE_STEPS,
                valueLabel = "${settings.fontSize.toInt()} sp",
                accent = primary,
                onChange = onFontSizeChange
            )

            Spacer(Modifier.height(14.dp))

            SliderRow(
                label = "Line Spacing",
                value = settings.lineSpacing,
                range = DiaryRanges.LINE_SPACING,
                steps = DiaryRanges.LINE_SPACING_STEPS,
                valueLabel = "${settings.lineSpacing.toInt()}",
                accent = secondary,
                onChange = onLineSpacingChange
            )

            Spacer(Modifier.height(14.dp))

            SliderRow(
                label = "Reading Padding",
                value = settings.padding,
                range = DiaryRanges.PADDING,
                steps = DiaryRanges.PADDING_STEPS,
                valueLabel = "${settings.padding.toInt()} dp",
                accent = primary,
                onChange = onPaddingChange
            )

            Spacer(Modifier.height(12.dp))

            ToggleRow(
                label = "Center Align Text",
                checked = settings.centerAlign,
                onChange = onCenterAlignChange
            )

            ToggleRow(
                label = "Haptic Feedback",
                checked = hapticsEnabled,
                onChange = onHapticsChange
            )

            ToggleRow(
                label = "Dynamic Theme Colors",
                checked = dynamicColor,
                onChange = onDynamicColorChange
            )

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = onSurface.copy(alpha = 0.12f))
            Spacer(Modifier.height(14.dp))

            OutlinedButton(
                onClick = onReset,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(1.dp, primary.copy(alpha = 0.6f))
            ) {
                Text(
                    "Reset to Default",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = primary
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Done", fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun SliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueLabel: String,
    accent: Color,
    onChange: (Float) -> Unit
) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            valueLabel,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = accent
            )
        )
    }
    Slider(
        value = value,
        onValueChange = onChange,
        valueRange = range,
        steps = steps
    )
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
