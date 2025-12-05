package com.aghyy.gameflix.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aghyy.gameflix.data.local.FavoriteGame
import com.aghyy.gameflix.data.local.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FavoritesRepository = FavoritesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.observeFavorites().collect { favorites ->
                _uiState.update {
                    it.copy(
                        favorites = favorites,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteFavorite(gameId: String) {
        viewModelScope.launch {
            repository.removeFavorite(gameId)
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            repository.clearFavorites()
        }
    }
}

data class FavoritesUiState(
    val favorites: List<FavoriteGame> = emptyList(),
    val isLoading: Boolean = true
)

