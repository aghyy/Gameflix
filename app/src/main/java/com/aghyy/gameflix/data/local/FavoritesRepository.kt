package com.aghyy.gameflix.data.local

import com.aghyy.gameflix.GameflixApplication
import com.aghyy.gameflix.library.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class FavoriteGame(
    val game: Game,
    val savedAt: Long
)

class FavoritesRepository(
    private val dao: FavoriteGameDao = GameflixDatabase.getInstance(GameflixApplication.appContext).favoriteGameDao()
) {

    fun observeFavorites(): Flow<List<FavoriteGame>> =
        dao.observeFavorites().map { list -> list.map { it.toFavoriteGame() } }

    fun observeFavorite(gameId: String): Flow<Boolean> =
        dao.observeFavorite(gameId).map { it != null }

    suspend fun saveFavorite(game: Game) = withContext(Dispatchers.IO) {
        val entity = game.toEntity()
        dao.insertFavorite(entity)
    }

    suspend fun removeFavorite(gameId: String) = withContext(Dispatchers.IO) {
        dao.deleteFavorite(gameId)
    }

    suspend fun clearFavorites() = withContext(Dispatchers.IO) {
        dao.deleteAllFavorites()
    }

    private fun FavoriteGameEntity.toFavoriteGame(): FavoriteGame =
        FavoriteGame(
            game = Game(
                id = id,
                title = title,
                thumbnailUrl = thumbnailUrl,
                description = description,
                developer = developer,
                releaseDate = releaseDate,
                screenshots = screenshots
            ),
            savedAt = savedAt
        )

    private fun Game.toEntity(): FavoriteGameEntity =
        FavoriteGameEntity(
            id = id,
            title = title,
            thumbnailUrl = thumbnailUrl,
            description = description,
            developer = developer,
            releaseDate = releaseDate,
            screenshots = screenshots,
            savedAt = System.currentTimeMillis()
        )
}

