package com.aghyy.gameflix.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FavoriteGameEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(GameflixConverters::class)
abstract class GameflixDatabase : RoomDatabase() {
    abstract fun favoriteGameDao(): FavoriteGameDao

    companion object {
        @Volatile
        private var INSTANCE: GameflixDatabase? = null

        fun getInstance(context: Context): GameflixDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GameflixDatabase::class.java,
                    "gameflix.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

