package com.aghyy.gameflix.data.remote

import com.aghyy.gameflix.library.Game
import com.aghyy.gameflix.library.GameSuggestion
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object GamebrainJsonMapper {

    fun toGames(element: JsonElement?): List<Game> {
        if (element == null || element is JsonNull) return emptyList()
        return extractObjects(element).mapNotNull { it.toGame() }
    }

    fun toGame(element: JsonElement?): Game? {
        if (element == null || element is JsonNull) return null
        val target = when (element) {
            is JsonObject -> unwrapSingleObject(element)
            is JsonArray -> element.firstOrNull()?.jsonObject
            else -> null
        } ?: return null
        return target.toGame()
    }

    fun toSuggestions(element: JsonElement?): List<GameSuggestion> {
        if (element == null || element is JsonNull) return emptyList()
        return extractObjects(element, extraKeys = listOf("suggestions")).mapNotNull { obj ->
            val id = obj.readString("id") ?: return@mapNotNull null
            val title = obj.readString("name", "title") ?: return@mapNotNull null
            GameSuggestion(id = id, title = title)
        }
    }

    private fun JsonObject.toGame(): Game? {
        val id = readString("id") ?: return null
        val title = readString("name", "title") ?: "Untitled"
        val description = readString("description", "summary", "storyline", "deck")
        val developer = readString("developer", "studio", "company", "publisher")
            ?: readFirstArrayString("developers", "companies", "involved_companies")
            ?: "Unknown studio"
        val releaseDate = readString("release_date", "released", "releaseDate", "first_release_date")
            ?: firstFromArray("release_dates")?.readString("human", "date")
            ?: "TBD"
        val coverUrl = readCoverUrl()
        val screenshots = readScreenshots()

        return Game(
            id = id,
            title = title,
            thumbnailUrl = coverUrl ?: DEFAULT_IMAGE_PLACEHOLDER,
            description = description ?: "Description not available.",
            developer = developer,
            releaseDate = releaseDate,
            screenshots = screenshots
        )
    }

    private fun JsonObject.readCoverUrl(): String? {
        val directUrl = readString(
            "image",
            "imageUrl",
            "coverUrl",
            "cover_url",
            "thumbnail",
            "artwork",
            "headerImage"
        )
        if (!directUrl.isNullOrBlank()) return normalizeImageUrl(directUrl)

        readImageId()?.let { return buildImageUrl(it) }

        val coverObject = this["cover"]?.jsonObjectOrNull()
        coverObject?.let { obj ->
            obj.readString("url", "image_url")?.let { return normalizeImageUrl(it) }
            obj.readImageId()?.let { return buildImageUrl(it) }
        }

        val mediaObject = this["media"]?.jsonObjectOrNull()
        mediaObject?.readString("cover", "portrait")?.let { return normalizeImageUrl(it) }

        val assetObject = arrayFirstObject("artworks", "screenshots", "images", "assets")
        assetObject?.let { obj ->
            obj.readString("url", "image_url")?.let { return normalizeImageUrl(it) }
            obj.readImageId()?.let { return buildImageUrl(it, size = "t_screenshot_big") }
        }

        return null
    }

    private fun normalizeImageUrl(raw: String): String =
        when {
            raw.startsWith("http") -> raw
            raw.startsWith("//") -> "https:$raw"
            raw.startsWith("/") -> "https://$GAMEBRAIN_IMAGE_HOST$raw"
            else -> raw
        }

    private fun buildImageUrl(imageId: String, size: String = IGDB_DEFAULT_SIZE): String =
        "https://$GAMEBRAIN_IMAGE_HOST/igdb/image/upload/$size/${imageId.trim()}.jpg"

    private fun extractObjects(
        element: JsonElement,
        extraKeys: List<String> = emptyList()
    ): List<JsonObject> {
        return when (element) {
            is JsonArray -> element.mapNotNull { it.jsonObjectOrNull() }
            is JsonObject -> {
                val keys = DEFAULT_COLLECTION_KEYS + extraKeys
                keys.forEach { key ->
                    val nested = element[key]
                    if (nested is JsonArray) {
                        return nested.mapNotNull { it.jsonObjectOrNull() }
                    } else if (nested is JsonObject && nested.values.all { it is JsonArray }) {
                        val flat = nested.values
                            .filterIsInstance<JsonArray>()
                            .firstOrNull()
                            ?.mapNotNull { it.jsonObjectOrNull() }
                        if (!flat.isNullOrEmpty()) return flat
                    }
                }
                listOf(element)
            }
            else -> emptyList()
        }
    }

    private fun unwrapSingleObject(objectElement: JsonObject): JsonObject? {
        val keys = DEFAULT_OBJECT_KEYS
        keys.forEach { key ->
            val nested = objectElement[key]
            if (nested is JsonObject) {
                return nested
            }
            if (nested is JsonArray) {
                return nested.firstOrNull()?.jsonObject
            }
        }
        return objectElement
    }

    private fun JsonObject.readFirstArrayString(vararg keys: String): String? {
        keys.forEach { key ->
            val array = this[key] as? JsonArray ?: return@forEach
            array.forEach { element ->
                val primitive = element as? JsonPrimitive
                val content = primitive?.contentOrNull
                if (!content.isNullOrBlank()) return content
                val obj = element as? JsonObject
                val nestedName = obj?.readString("name", "company", "title")
                if (!nestedName.isNullOrBlank()) return nestedName
            }
        }
        return null
    }

    private fun JsonObject.readScreenshots(): List<String> {
        val urls = mutableSetOf<String>()
        readStringArray("screenshots", "images").forEach { urls.add(normalizeImageUrl(it)) }

        listOf("screenshots", "images", "artworks").forEach { key ->
            val array = this[key] as? JsonArray ?: return@forEach
            array.forEach { element ->
                when (element) {
                    is JsonPrimitive -> element.contentOrNull?.let { urls.add(normalizeImageUrl(it)) }
                    is JsonObject -> {
                        element.readString("url", "image_url")?.let { urls.add(normalizeImageUrl(it)) }
                        element.readImageId()?.let { urls.add(buildImageUrl(it, size = "t_screenshot_big")) }
                    }
                    is JsonArray -> {
                        element.forEach { nested ->
                            if (nested is JsonPrimitive) {
                                nested.contentOrNull?.let { urls.add(normalizeImageUrl(it)) }
                            }
                        }
                    }
                }
            }
        }

        return urls.toList()
    }

    private fun JsonObject.arrayFirstObject(vararg keys: String): JsonObject? {
        keys.forEach { key ->
            val array = this[key] as? JsonArray ?: return@forEach
            val candidate = array.firstOrNull { it is JsonObject } as? JsonObject
            if (candidate != null) return candidate
        }
        return null
    }

    private fun JsonObject.firstFromArray(key: String): JsonObject? =
        (this[key] as? JsonArray)?.firstOrNull()?.jsonObjectOrNull()

    private fun JsonObject.readString(vararg keys: String): String? {
        keys.forEach { key ->
            val element = this[key] ?: return@forEach
            val result = when (element) {
                is JsonPrimitive -> element.contentOrNull
                is JsonObject -> element.readString("name", "title", "value")
                is JsonArray -> element.firstOrNull()?.jsonPrimitive?.contentOrNull
                else -> null
            }
            if (!result.isNullOrBlank()) return result
        }
        return null
    }

    private fun JsonObject.readImageId(vararg keys: String): String? {
        val keySet = if (keys.isEmpty()) DEFAULT_IMAGE_ID_KEYS else keys.toList()
        keySet.forEach { key ->
            val element = this[key] ?: return@forEach
            val value = when (element) {
                is JsonPrimitive -> element.contentOrNull
                is JsonObject -> element.readString("id", "code")
                else -> null
            }
            if (!value.isNullOrBlank()) return value
        }
        return null
    }

    private fun JsonObject.readStringArray(vararg keys: String): List<String> {
        val results = mutableListOf<String>()
        keys.forEach { key ->
            val array = this[key] as? JsonArray ?: return@forEach
            array.forEach { element ->
                val value = when (element) {
                    is JsonPrimitive -> element.contentOrNull
                    else -> null
                }
                if (!value.isNullOrBlank()) results.add(value)
            }
        }
        return results
    }

    private fun JsonElement.jsonObjectOrNull(): JsonObject? = this as? JsonObject

    private const val GAMEBRAIN_IMAGE_HOST = "images.igdb.com"
    private const val IGDB_DEFAULT_SIZE = "t_cover_big"
    private const val DEFAULT_IMAGE_PLACEHOLDER = "https://placehold.co/600x800/111111/FFFFFF?text=Gameflix"

    private val DEFAULT_COLLECTION_KEYS = listOf("games", "results", "data", "items")
    private val DEFAULT_OBJECT_KEYS = listOf("game", "data", "result", "item")
    private val DEFAULT_IMAGE_ID_KEYS = listOf("image_id", "imageId", "cloudinary_id")
}

