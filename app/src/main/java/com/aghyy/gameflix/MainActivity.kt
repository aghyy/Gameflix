package com.aghyy.gameflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aghyy.gameflix.library.InMemoryGamingLibrary
import com.aghyy.gameflix.ui.GameDetailScreen
import com.aghyy.gameflix.ui.GameLibraryScreen
import com.aghyy.gameflix.ui.theme.GameflixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val gamingLibrary = InMemoryGamingLibrary()
    NavHost(navController = navController, startDestination = "gameLibrary") {
        composable("gameLibrary") {
            GameLibraryScreen(navController = navController, library = gamingLibrary)
        }
        composable("gameDetail/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            val game = gamingLibrary.getGames().flatMap { it.games }.find { it.id == gameId }
            if (game != null) {
                GameDetailScreen(game = game, navController = navController)
            }
        }
    }
}
