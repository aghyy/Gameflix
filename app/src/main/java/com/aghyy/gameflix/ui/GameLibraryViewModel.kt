package com.aghyy.gameflix.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aghyy.gameflix.data.GameRepository
import com.aghyy.gameflix.data.local.FavoritesRepository
import com.aghyy.gameflix.library.Game
import com.aghyy.gameflix.library.GameSuggestion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameLibraryViewModel @JvmOverloads constructor(
    private val repository: GameRepository = GameRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameLibraryUiState())
    val uiState: StateFlow<GameLibraryUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var suggestionJob: Job? = null

    init {
        refreshFeaturedContent()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.observeFavorites().collect { favorites ->
                val ids = favorites.map { it.game.id }.toSet()
                _uiState.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    fun refreshFeaturedContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHomeLoading = true, homeError = null) }
            val featuredDeferred = async { repository.fetchFeaturedGame() }
            val medievalDeferred = async { repository.fetchMedievalStrategyGames() }
            val similarDeferred = async { repository.fetchSimilarStrongholdGames() }
            val suggestionsDeferred = async { repository.fetchDefaultSuggestions() }

            val featuredResult = featuredDeferred.await()
            val medievalResult = medievalDeferred.await()
            val similarResult = similarDeferred.await()
            val suggestionsResult = suggestionsDeferred.await()

            val homeError = listOf(featuredResult, medievalResult, similarResult)
                .firstOrNull { it.isFailure }
                ?.exceptionOrNull()
                ?.message

            _uiState.update { state ->
                state.copy(
                    featuredGame = featuredResult.getOrNull(),
                    medievalStrategyGames = medievalResult.getOrDefault(emptyList()),
                    similarGames = similarResult.getOrDefault(emptyList()),
                    defaultSuggestions = suggestionsResult.getOrDefault(emptyList()),
                    suggestions = if (state.searchQuery.isBlank()) {
                        suggestionsResult.getOrDefault(emptyList())
                    } else {
                        state.suggestions
                    },
                    isHomeLoading = false,
                    homeError = homeError
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        suggestionJob?.cancel()
        if (query.length < 3) {
            _uiState.update { state ->
                state.copy(
                    suggestions = if (query.isBlank()) state.defaultSuggestions else emptyList()
                )
            }
            return
        }

        suggestionJob = viewModelScope.launch {
            _uiState.update { it.copy(isSuggestionsLoading = true) }
            repository.fetchSuggestions(query)
                .onSuccess { suggestions ->
                    _uiState.update {
                        it.copy(
                            suggestions = suggestions,
                            isSuggestionsLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) return@launch
                    _uiState.update {
                        it.copy(
                            suggestions = emptyList(),
                            isSuggestionsLoading = false,
                            searchError = error.message
                        )
                    }
                }
        }
    }

    fun submitSearch(queryOverride: String? = null) {
        val query = (queryOverride ?: _uiState.value.searchQuery).trim()
        if (query.isBlank()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchError = null) }
            repository.searchGames(query)
                .onSuccess { games ->
                    _uiState.update { it.copy(searchResults = games, isSearching = false) }
                }
                .onFailure { error ->
                    if (error is CancellationException) return@launch
                    _uiState.update {
                        it.copy(
                            searchResults = emptyList(),
                            isSearching = false,
                            searchError = error.message ?: "Search failed"
                        )
                    }
                }
        }
    }

    fun onSuggestionSelected(suggestion: GameSuggestion) {
        _uiState.update { it.copy(searchQuery = suggestion.title) }
        submitSearch(suggestion.title)
    }

    fun clearSearchResults() {
        searchJob?.cancel()
        _uiState.update { it.copy(searchResults = emptyList(), searchError = null) }
    }

    fun toggleFavorite(game: Game) {
        viewModelScope.launch {
            val isFavorite = _uiState.value.favoriteIds.contains(game.id)
            if (isFavorite) {
                favoritesRepository.removeFavorite(game.id)
            } else {
                favoritesRepository.saveFavorite(game)
            }
        }
    }
}

data class GameLibraryUiState(
    val featuredGame: Game? = null,
    val medievalStrategyGames: List<Game> = emptyList(),
    val similarGames: List<Game> = emptyList(),
    val defaultSuggestions: List<GameSuggestion> = emptyList(),
    val suggestions: List<GameSuggestion> = emptyList(),
    val searchResults: List<Game> = emptyList(),
    val searchQuery: String = "",
    val isHomeLoading: Boolean = true,
    val isSearching: Boolean = false,
    val isSuggestionsLoading: Boolean = false,
    val homeError: String? = null,
    val searchError: String? = null,
    val favoriteIds: Set<String> = emptySet()
)

