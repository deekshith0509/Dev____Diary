package com.love.devadasudiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.love.devadasudiary.ui.PoetryViewModel
import com.love.devadasudiary.ui.screens.LoveDiaryScreen
import com.love.devadasudiary.ui.theme.DevadasuDiaryTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PoetryViewModel by viewModels {
        PoetryViewModel.factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge so our `RomanticBackground` paints behind the
        // status & navigation bars. Inset padding is consumed by Scaffold.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val appState by viewModel.appState.collectAsStateWithLifecycle()
            val isDark = appState.isDarkTheme

            // Keep system bar icons readable in both themes.
            LaunchedEffect(isDark) {
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }

            DevadasuDiaryTheme(
                isDark = isDark,
                useDynamicColor = appState.dynamicColor
            ) {
                LoveDiaryScreen(viewModel = viewModel)
            }
        }
    }
}
