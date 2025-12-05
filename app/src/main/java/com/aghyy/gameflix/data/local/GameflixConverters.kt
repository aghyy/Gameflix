package com.aghyy.gameflix.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class GameflixConverters {
    @TypeConverter
    fun fromScreenshots(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toScreenshots(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return try {
            Json.parseToJsonElement(value).jsonArray.mapNotNull { it.jsonPrimitive.contentOrNull }
        } catch (ex: Exception) {
            emptyList()
        }
    }
}

