package com.aghyy.gameflix

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.aghyy.gameflix.ui.theme.GameflixTheme

fun ComposeContentTestRule.launchGameflixApp() {
    setContent {
        GameflixTheme {
            GameflixApp()
        }
    }
}