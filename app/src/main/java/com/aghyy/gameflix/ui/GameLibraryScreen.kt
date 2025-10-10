package com.aghyy.gameflix.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.aghyy.gameflix.library.Game
import com.aghyy.gameflix.library.GamingLibrary
import com.aghyy.gameflix.library.InMemoryGamingLibrary

@Composable
fun GameCard(game: Game, modifier: Modifier = Modifier) {
    Card(modifier = modifier.size(width = 140.dp, height = 220.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF222222)), // Placeholder background
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = game.thumbnailUrl,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    loading = {
                        Text("Loading...", color = Color.LightGray)
                    },
                    error = {
                        Text("No Image", color = Color.White)
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun GameLibraryScreen(modifier: Modifier = Modifier, library: GamingLibrary = InMemoryGamingLibrary()) {
    val games = library.getGames()
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Game Library", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games) { game ->
                GameCard(game)
            }
        }
    }
}
