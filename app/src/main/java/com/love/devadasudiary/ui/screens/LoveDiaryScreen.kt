package com.love.devadasudiary.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.love.devadasudiary.PoetryUiState
import com.love.devadasudiary.PoetryViewModel
import com.love.devadasudiary.ui.components.RomanticBackground
import com.love.devadasudiary.ui.components.RomanticBottomNavigation
import com.love.devadasudiary.ui.components.RomanticHeader
import com.love.devadasudiary.ui.components.RomanticLoadingCard
import com.love.devadasudiary.ui.components.RomanticErrorCard
import com.love.devadasudiary.ui.components.RomanticPoemCard
import com.love.devadasudiary.ui.components.RomanticTopBar
import com.love.devadasudiary.ui.dialogs.RomanticSettingsSheet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoveDiaryScreen(vm: PoetryViewModel) {
    val uiState       by vm.uiState.collectAsState()
    val fontSize      by vm.fontSize.collectAsState()
    val lineSpacing   by vm.lineSpacing.collectAsState()
    val poemPadding   by vm.poemPadding.collectAsState()
    val centerAlign   by vm.centerAlign.collectAsState()
    val isDark        by vm.isDarkTheme.collectAsState()
    val favoriteIds   by vm.favoriteIds.collectAsState()
    val currentPoemId by vm.currentPoemId.collectAsState()
    val haptic        = LocalHapticFeedback.current
    val poems         = vm.poems

    val currentPoem = remember(currentPoemId, poems) {
        poems.firstOrNull { it.id == currentPoemId } ?: poems.first()
    }

    val showSettings = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        RomanticBackground(isDark = isDark)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                RomanticTopBar(
                    title = currentPoem.title,
                    subtitle = currentPoem.subtitle,
                    isDark = isDark,
                    isFavorite = favoriteIds.contains(currentPoemId),
                    onToggleTheme = { vm.toggleTheme() },
                    onToggleFavorite = { vm.toggleFavorite(currentPoemId) },
                    onRefresh = { vm.refreshPoem() },
                    onSettings = { showSettings.value = true }
                )
            },
            bottomBar = {
                RomanticBottomNavigation(
                    poems = poems,
                    currentPoemId = currentPoemId,
                    favoriteIds = favoriteIds,
                    onPoemSelected = { id ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        vm.selectPoemById(id)
                    },
                    onToggleFavorite = { id ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        vm.toggleFavorite(id)
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                RomanticHeader(title = "Made with 💖 by Devadasu...👀✨")

                Spacer(Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {

                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            (fadeIn(tween(500)) + slideInVertically(
                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                            ) { it / 8 }).togetherWith(
                                fadeOut(tween(300)) + slideOutVertically(
                                    animationSpec = tween(300)
                                ) { -it / 8 }
                            )
                        },
                        label = "poem_transition"
                    ) { state ->

                        when (state) {
                            is PoetryUiState.Loading -> RomanticLoadingCard()

                            is PoetryUiState.Error -> RomanticErrorCard(
                                title = state.title,
                                message = state.message,
                                onRetry = { vm.refreshPoem() }
                            )

                            is PoetryUiState.Success -> RomanticPoemCard(
                                poem = state.poem.content,
                                fontSize = fontSize,
                                lineSpacing = lineSpacing,
                                horizontalPadding = poemPadding,
                                centerAlign = centerAlign
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettings.value) {
        RomanticSettingsSheet(
            fontSize = fontSize,
            onFontSizeChange = { vm.setFontSize(it) },
            lineSpacing = lineSpacing,
            onLineSpacingChange = { vm.setLineSpacing(it) },
            poemPadding = poemPadding,
            onPoemPaddingChange = { vm.setPoemPadding(it) },
            centerAlign = centerAlign,
            onCenterAlignChange = { vm.setCenterAlign(it) },
            onDismiss = { showSettings.value = false }
        )
    }
}