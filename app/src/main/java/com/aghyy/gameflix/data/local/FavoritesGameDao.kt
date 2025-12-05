package com.aghyy.gameflix.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteGameDao {
    @Query("SELECT * FROM favorite_games ORDER BY savedAt DESC")
    fun observeFavorites(): Flow<List<FavoriteGameEntity>>

    @Query("SELECT * FROM favorite_games WHERE id = :gameId LIMIT 1")
    fun observeFavorite(gameId: String): Flow<FavoriteGameEntity?>

    @Query("SELECT * FROM favorite_games WHERE id = :gameId LIMIT 1")
    suspend fun getFavorite(gameId: String): FavoriteGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(entity: FavoriteGameEntity)

    @Query("DELETE FROM favorite_games WHERE id = :gameId")
    suspend fun deleteFavorite(gameId: String)

    @Query("DELETE FROM favorite_games")
    suspend fun deleteAllFavorites()
}

