package com.love.devadasudiary.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.love.devadasudiary.core.DiaryDimens
import com.love.devadasudiary.ui.PoetryViewModel
import com.love.devadasudiary.ui.components.RomanticBackground
import com.love.devadasudiary.ui.components.RomanticBottomNavigation
import com.love.devadasudiary.ui.components.RomanticErrorCard
import com.love.devadasudiary.ui.components.RomanticHeader
import com.love.devadasudiary.ui.components.RomanticLoadingCard
import com.love.devadasudiary.ui.components.RomanticPoemCard
import com.love.devadasudiary.ui.components.RomanticTopBar
import com.love.devadasudiary.ui.dialogs.RomanticSettingsSheet
import com.love.devadasudiary.ui.state.PoetryUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveDiaryScreen(viewModel: PoetryViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val readingSettings by viewModel.readingSettings.collectAsStateWithLifecycle()
    val appState by viewModel.appState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val poems = viewModel.poems

    // Avoid recomputing currentPoem on every recomposition.
    val currentPoem by remember(poems) {
        derivedStateOf {
            poems.firstOrNull { it.id == appState.currentPoemId } ?: poems.first()
        }
    }

    var showSettings by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        RomanticBackground(isDark = appState.isDarkTheme)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                RomanticTopBar(
                    title = currentPoem.title,
                    subtitle = currentPoem.subtitle,
                    isDark = appState.isDarkTheme,
                    isFavorite = appState.currentPoemId in appState.favoriteIds,
                    onToggleTheme = viewModel::toggleTheme,
                    onToggleFavorite = { viewModel.toggleFavorite(appState.currentPoemId) },
                    onRefresh = viewModel::refreshPoem,
                    onSettings = { showSettings = true }
                )
            },
            bottomBar = {
                RomanticBottomNavigation(
                    poems = poems,
                    isDark = appState.isDarkTheme,
                    currentPoemId = appState.currentPoemId,
                    favoriteIds = appState.favoriteIds,
                    onPoemSelected = { id ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.selectPoemById(id)
                    },
                    onToggleFavorite = { id ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.toggleFavorite(id)
                    }
                )
            }
        ) { contentPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(
                        horizontal = DiaryDimens.ScreenHorizontalPadding,
                        vertical = DiaryDimens.ScreenVerticalPadding
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                RomanticHeader(title = "Made with 💖 by Devadasu...👀✨")

                Spacer(Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {

                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            (fadeIn(tween(500)) +
                                slideInVertically(tween(500, easing = FastOutSlowInEasing)) { it / 8 })
                                .togetherWith(
                                    fadeOut(tween(300)) +
                                        slideOutVertically(tween(300)) { -it / 8 }
                                )
                        },
                        label = "poem_transition",
                        contentKey = { state ->
                            // Distinguish state transitions for AnimatedContent.
                            when (state) {
                                is PoetryUiState.Loading -> "loading"
                                is PoetryUiState.Error   -> "error:${state.title}"
                                is PoetryUiState.Success -> "success:${state.poem.id}"
                            }
                        }
                    ) { state ->

                        when (state) {
                            is PoetryUiState.Loading ->
                                RomanticLoadingCard()

                            is PoetryUiState.Error ->
                                RomanticErrorCard(
                                    title = state.title,
                                    message = state.message,
                                    onRetry = viewModel::refreshPoem
                                )

                            is PoetryUiState.Success ->
                                RomanticPoemCard(
                                    poem = state.poem.content,
                                    fontSize = readingSettings.fontSize,
                                    lineSpacing = readingSettings.lineSpacing,
                                    horizontalPadding = readingSettings.padding,
                                    centerAlign = readingSettings.centerAlign
                                )
                        }
                    }
                }
            }
        }
    }

    if (showSettings) {
        RomanticSettingsSheet(
            settings = readingSettings,
            hapticsEnabled = appState.hapticsEnabled,
            dynamicColor = appState.dynamicColor,
            onFontSizeChange = viewModel::setFontSize,
            onLineSpacingChange = viewModel::setLineSpacing,
            onPaddingChange = viewModel::setPoemPadding,
            onCenterAlignChange = viewModel::setCenterAlign,
            onHapticsChange = viewModel::setHapticsEnabled,
            onDynamicColorChange = viewModel::setDynamicColor,
            onReset = viewModel::resetReadingSettings,
            onDismiss = { showSettings = false }
        )
    }
}
