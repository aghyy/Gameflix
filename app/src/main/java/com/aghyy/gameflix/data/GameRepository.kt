package com.aghyy.gameflix.data

import com.aghyy.gameflix.data.remote.GamebrainApi
import com.aghyy.gameflix.data.remote.GamebrainJsonMapper
import com.aghyy.gameflix.library.Game
import com.aghyy.gameflix.library.GameSuggestion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

class GameRepository(
    private val api: GamebrainApi = GameRepositoryProvider.api
) {

    suspend fun fetchFeaturedGame(): Result<Game?> =
        request(block = { api.getGameDetail(FEATURED_GAME_ID) }) { response ->
            GamebrainJsonMapper.toGame(response)
        }

    suspend fun fetchMedievalStrategyGames(): Result<List<Game>> =
        request(block = { api.getGames(MEDIEVAL_QUERY) }) { response ->
            GamebrainJsonMapper.toGames(response)
        }

    suspend fun fetchSimilarStrongholdGames(): Result<List<Game>> =
        request(block = { api.getSimilarGames(SIMILAR_BASE_ID) }) { response ->
            GamebrainJsonMapper.toGames(response)
        }

    suspend fun fetchDefaultSuggestions(): Result<List<GameSuggestion>> =
        request(block = { api.getSuggestions(DEFAULT_SUGGESTION_QUERY) }) { response ->
            GamebrainJsonMapper.toSuggestions(response)
        }

    suspend fun searchGames(query: String): Result<List<Game>> =
        request(block = { api.getGames(query) }) { response ->
            GamebrainJsonMapper.toGames(response)
        }

    suspend fun fetchSuggestions(query: String): Result<List<GameSuggestion>> =
        request(block = { api.getSuggestions(query) }) { response ->
            GamebrainJsonMapper.toSuggestions(response)
        }

    suspend fun fetchGameDetail(gameId: String): Result<Game?> {
        val numericId = gameId.toLongOrNull()
        return if (numericId != null) {
            request(block = { api.getGameDetail(numericId) }) { response ->
                GamebrainJsonMapper.toGame(response)
            }
        } else {
            Result.failure(IllegalArgumentException("Invalid game id"))
        }
    }

    private suspend fun <T> request(
        block: suspend () -> JsonElement,
        mapper: (JsonElement) -> T
    ): Result<T> {
        return try {
            val result = withContext(Dispatchers.IO) {
                val response = block()
                mapper(response)
            }
            Result.success(result)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    companion object {
        private const val FEATURED_GAME_ID = 1_273_796L
        private const val SIMILAR_BASE_ID = 33_313L
        private const val MEDIEVAL_QUERY = "medieval strategy games"
        private const val DEFAULT_SUGGESTION_QUERY = "kingdom co"
    }
}

private object GameRepositoryProvider {
    val api by lazy { GamebrainApi() }
}

