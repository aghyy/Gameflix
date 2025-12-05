package com.aghyy.gameflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aghyy.gameflix.ui.FavoritesScreen
import com.aghyy.gameflix.ui.FavoritesViewModel
import com.aghyy.gameflix.ui.GameDetailScreen
import com.aghyy.gameflix.ui.GameDetailViewModel
import com.aghyy.gameflix.ui.GameLibraryScreen
import com.aghyy.gameflix.ui.GameLibraryViewModel
import com.aghyy.gameflix.ui.theme.GameflixTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        setContent {
            GameflixTheme {
                GameflixApp()
            }
        }
    }
}

@Composable
fun GameflixApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "gameLibrary") {
        composable("gameLibrary") {
            val viewModel: GameLibraryViewModel = viewModel()
            GameLibraryScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("favorites") {
            val viewModel: FavoritesViewModel = viewModel()
            FavoritesScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("gameDetail/{gameId}") { backStackEntry ->
            val viewModel: GameDetailViewModel = viewModel(backStackEntry)
            val uiState by viewModel.uiState.collectAsState()
            GameDetailScreen(
                navController = navController,
                uiState = uiState,
                onRetry = viewModel::retry,
                onToggleFavorite = viewModel::toggleFavorite,
                onScreenshotSelected = viewModel::onScreenshotSelected,
                onScreenshotDismissed = viewModel::dismissScreenshotPreview
            )
        }
    }
}
