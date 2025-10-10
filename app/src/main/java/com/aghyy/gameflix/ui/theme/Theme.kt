package com.aghyy.gameflix.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NetflixRed,
    secondary = NetflixDarkGray,
    background = NetflixDark,
    surface = NetflixDark,
    onPrimary = NetflixLightGray,
    onSecondary = NetflixLightGray,
    onBackground = NetflixLightGray,
    onSurface = NetflixLightGray
)

@Composable
fun GameflixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
