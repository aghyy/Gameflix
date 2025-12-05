package com.aghyy.gameflix.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_games")
data class FavoriteGameEntity(
    @PrimaryKey val id: String,
    val title: String,
    val thumbnailUrl: String,
    val description: String,
    val developer: String,
    val releaseDate: String,
    val screenshots: List<String>,
    val savedAt: Long
)

