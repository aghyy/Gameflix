package com.aghyy.gameflix.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.aghyy.gameflix.library.Game
import com.aghyy.gameflix.library.GameCategory
import com.aghyy.gameflix.library.GamingLibrary
import com.aghyy.gameflix.library.InMemoryGamingLibrary

@Composable
fun GameCard(game: Game, modifier: Modifier = Modifier, onGameClick: (String) -> Unit) {
    Card(
        modifier = modifier
            .size(width = 150.dp, height = 230.dp)
            .clickable { onGameClick(game.id) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(170.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = game.thumbnailUrl,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF222222)))
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0xFF222222)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image", color = Color.White)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun GameCategoryRow(category: GameCategory, onGameClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = category.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(category.games) { game ->
                GameCard(game = game, onGameClick = onGameClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLibraryScreen(
    modifier: Modifier = Modifier,
    library: GamingLibrary = InMemoryGamingLibrary(),
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = library.getGames()
    val filteredCategories = if (searchQuery.isBlank()) {
        categories
    } else {
        categories.mapNotNull { category ->
            val filteredGames = category.games.filter { it.title.contains(searchQuery, ignoreCase = true) }
            if (filteredGames.isNotEmpty()) {
                category.copy(games = filteredGames)
            } else {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF141414))) {
                TopAppBar(
                    title = { Text("Gameflix", color = Color(0xFFE50914), fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2C2C2C),
                        unfocusedContainerColor = Color(0xFF2C2C2C),
                        disabledContainerColor = Color(0xFF2C2C2C),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFE50914)
                    )
                )
            }
        },
        containerColor = Color(0xFF141414)
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (filteredCategories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No games found.", color = Color.White)
                    }
                }
            } else {
                items(filteredCategories) { category ->
                    GameCategoryRow(
                        category = category,
                        onGameClick = { gameId ->
                            navController.navigate("gameDetail/$gameId")
                        }
                    )
                }
            }
        }
    }
}
