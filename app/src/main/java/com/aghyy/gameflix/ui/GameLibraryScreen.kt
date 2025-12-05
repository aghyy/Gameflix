package com.aghyy.gameflix.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.aghyy.gameflix.R
import com.aghyy.gameflix.library.Game
import com.aghyy.gameflix.library.GameCategory
import com.aghyy.gameflix.library.GameSuggestion

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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF222222))
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF222222)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.no_image), color = Color.White)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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
            items(category.games, key = { it.id }) { game ->
                GameCard(game = game, onGameClick = onGameClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLibraryScreen(
    navController: NavController,
    viewModel: GameLibraryViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val searchError = uiState.searchError

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF141414))) {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            color = Color(0xFFE50914),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("favorites") }) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = stringResource(R.string.content_description_open_favorites),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                SearchBar(
                    query = uiState.searchQuery,
                    isLoading = uiState.isSearching,
                    onQueryChange = {
                        viewModel.onSearchQueryChange(it)
                        if (it.isBlank()) viewModel.clearSearchResults()
                    },
                    onClear = {
                        viewModel.onSearchQueryChange("")
                        viewModel.clearSearchResults()
                    },
                    onSearch = viewModel::submitSearch
                )
                SuggestionRow(
                    suggestions = uiState.suggestions,
                    isLoading = uiState.isSuggestionsLoading,
                    onSuggestionClick = viewModel::onSuggestionSelected
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
            if (uiState.isHomeLoading) {
                item {
                    InlineLoading(
                        message = stringResource(R.string.loading_curated_picks),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            }

            uiState.homeError?.let { error ->
                item {
                    ErrorMessage(
                        message = error,
                        onRetry = viewModel::refreshFeaturedContent
                    )
                }
            }

            when {
                uiState.isSearching -> {
                    item {
                        InlineLoading(
                            message = stringResource(R.string.searching_games),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                uiState.searchResults.isNotEmpty() -> {
                    item {
                        Text(
                            text = stringResource(R.string.search_results_header),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(uiState.searchResults, key = { it.id }) { game ->
                        SearchResultRow(
                            game = game,
                            isFavorite = uiState.favoriteIds.contains(game.id),
                            onClick = { navController.navigateToGame(game.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(game) }
                        )
                        Divider(color = Color(0x22FFFFFF))
                    }
                }

                searchError != null -> {
                    item {
                        ErrorMessage(
                            message = searchError,
                            onRetry = viewModel::submitSearch
                        )
                    }
                }
            }

            uiState.featuredGame?.let { game ->
                item {
                    FeaturedGameCard(
                        game = game,
                        onClick = { navController.navigateToGame(game.id) }
                    )
                }
            }

            if (uiState.similarGames.isNotEmpty()) {
                item {
                    GameCategoryRow(
                        category = GameCategory(
                            title = stringResource(R.string.category_similar_stronghold),
                            games = uiState.similarGames
                        ),
                        onGameClick = { navController.navigateToGame(it) }
                    )
                }
            }

            if (uiState.medievalStrategyGames.isNotEmpty()) {
                item {
                    GameCategoryRow(
                        category = GameCategory(
                            title = stringResource(R.string.category_medieval_strategy),
                            games = uiState.medievalStrategyGames
                        ),
                        onGameClick = { navController.navigateToGame(it) }
                    )
                }
            }

            if (!uiState.isHomeLoading &&
                uiState.featuredGame == null &&
                uiState.medievalStrategyGames.isEmpty() &&
                uiState.similarGames.isEmpty()
            ) {
                item {
                    EmptyState()
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: (String?) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_bar_placeholder), color = Color.Gray) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.content_description_search), tint = Color.White)
        },
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFFE50914)
                )
            } else if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.content_description_clear_search), tint = Color.White)
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2C2C2C),
            unfocusedContainerColor = Color(0xFF2C2C2C),
            disabledContainerColor = Color(0xFF2C2C2C),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFFE50914)
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch(query) }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
private fun SuggestionRow(
    suggestions: List<GameSuggestion>,
    isLoading: Boolean,
    onSuggestionClick: (GameSuggestion) -> Unit
) {
    if (isLoading) {
        LinearLoading(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp))
        return
    }

    if (suggestions.isEmpty()) return

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions, key = { it.id }) { suggestion ->
            AssistChip(
                onClick = { onSuggestionClick(suggestion) },
                label = { Text(suggestion.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF2F2F2F),
                    labelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun FeaturedGameCard(game: Game, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Column {
            SubcomposeAsyncImage(
                model = game.thumbnailUrl,
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.featured_game_by, game.developer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    game: Game,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
            modifier = Modifier.size(64.dp)
        ) {
            SubcomposeAsyncImage(
                model = game.thumbnailUrl,
                contentDescription = game.title,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = game.developer,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
            Text(
                text = game.releaseDate,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFBBBBBB)
            )
        }
        IconButton(onClick = onFavoriteToggle) {
            val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
            val contentDescription = if (isFavorite) {
                stringResource(R.string.content_description_delete_favorite)
            } else {
                stringResource(R.string.content_description_add_favorite)
            }
            val tint = if (isFavorite) Color(0xFFE50914) else Color.White
            Icon(icon, contentDescription = contentDescription, tint = tint)
        }
    }
}

@Composable
private fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0x33FF5555), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(text = message, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Button(onClick = onRetry) {
            Text(stringResource(R.string.button_retry))
        }
    }
}

@Composable
private fun InlineLoading(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = Color(0xFFE50914),
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = message, color = Color.White)
    }
}

@Composable
private fun LinearLoading(modifier: Modifier = Modifier) {
    androidx.compose.material3.LinearProgressIndicator(
        modifier = modifier,
        color = Color(0xFFE50914),
        trackColor = Color(0x33E50914)
    )
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.empty_state_message),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun NavController.navigateToGame(gameId: String) {
    val encodedId = Uri.encode(gameId)
    navigate("gameDetail/$encodedId")
}
