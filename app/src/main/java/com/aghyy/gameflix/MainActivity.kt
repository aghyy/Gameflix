package com.aghyy.gameflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.aghyy.gameflix.ui.GameLibraryScreen
import com.aghyy.gameflix.ui.theme.GameflixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameflixTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    GameLibraryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
