package com.aghyy.gameflix.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.StringRes
import com.aghyy.gameflix.GameflixApplication
import com.aghyy.gameflix.R
import com.aghyy.gameflix.data.GameRepository
import com.aghyy.gameflix.data.local.FavoritesRepository
import com.aghyy.gameflix.library.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameDetailViewModel @JvmOverloads constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: GameRepository = GameRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository()
) : ViewModel() {

    private val gameId: String = savedStateHandle.get<String>("gameId").orEmpty()

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    init {
        observeFavorite()
        loadDetails()
    }

    private fun observeFavorite() {
        if (gameId.isBlank()) return
        viewModelScope.launch {
            favoritesRepository.observeFavorite(gameId).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    fun retry() {
        loadDetails()
    }

    fun toggleFavorite() {
        val currentGame = _uiState.value.game ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoritesRepository.removeFavorite(currentGame.id)
            } else {
                favoritesRepository.saveFavorite(currentGame)
            }
        }
    }

    fun onScreenshotSelected(url: String) {
        _uiState.update { it.copy(expandedScreenshotUrl = url) }
    }

    fun dismissScreenshotPreview() {
        _uiState.update { it.copy(expandedScreenshotUrl = null) }
    }

    private fun loadDetails() {
        if (gameId.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = getString(R.string.error_missing_game_id), expandedScreenshotUrl = null)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.fetchGameDetail(gameId)
                .onSuccess { game ->
                    if (game == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = getString(R.string.error_no_game_details),
                                expandedScreenshotUrl = null
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, game = game, expandedScreenshotUrl = null) }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: getString(R.string.error_unable_load_details),
                            expandedScreenshotUrl = null
                        )
                    }
                }
        }
    }

    private fun getString(@StringRes resId: Int): String =
        GameflixApplication.appContext.getString(resId)
}

data class GameDetailUiState(
    val isLoading: Boolean = true,
    val game: Game? = null,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false,
    val expandedScreenshotUrl: String? = null
)

