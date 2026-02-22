package com.love.devadasudiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.love.devadasudiary.ui.theme.DevadasuDiaryTheme
import com.love.devadasudiary.ui.screens.LoveDiaryScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val vm: PoetryViewModel = viewModel<PoetryViewModel>()
            val isDark by vm.isDarkTheme.collectAsState()

            DevadasuDiaryTheme(isDark = isDark) {
                LoveDiaryScreen(vm)
            }
        }
    }
}
