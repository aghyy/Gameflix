package com.aghyy.gameflix.library

data class Game(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val description: String,
    val developer: String,
    val releaseDate: String,
    val screenshots: List<String> = emptyList()
)

data class GameCategory(val title: String, val games: List<Game>)
