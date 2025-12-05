package com.aghyy.gameflix.data.remote

import android.util.Log
import com.aghyy.gameflix.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private const val GAMEBRAIN_HOST = "api.gamebrain.co"

class GamebrainApi internal constructor(
    private val client: HttpClient = GamebrainHttpClient.client
) {

    suspend fun getGames(query: String): JsonElement = client.get {
        configureBaseRequest()
        url {
            path("v1", "games")
        }
        parameter("query", query)
    }.body()

    suspend fun getSuggestions(query: String): JsonElement = client.get {
        configureBaseRequest()
        url {
            path("v1", "games", "suggestions")
        }
        parameter("query", query)
    }.body()

    suspend fun getGameDetail(gameId: Long): JsonElement = client.get {
        configureBaseRequest()
        url {
            path("v1", "games", gameId.toString())
        }
    }.body()

    suspend fun getSimilarGames(gameId: Long): JsonElement = client.get {
        configureBaseRequest()
        url {
            path("v1", "games", gameId.toString(), "similar")
        }
    }.body()

    private fun HttpRequestBuilder.configureBaseRequest() {
        url {
            protocol = URLProtocol.HTTPS
            host = GAMEBRAIN_HOST
        }
        contentType(ContentType.Application.Json)
    }
}

private object GamebrainHttpClient {
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    val client: HttpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(jsonConfig)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
                connectTimeoutMillis = 30_000
            }
            install(Logging) {
                level = LogLevel.NONE
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = GAMEBRAIN_HOST
                }
                contentType(ContentType.Application.Json)
                val apiKey = BuildConfig.GAMEBRAIN_API_KEY
                if (apiKey.isBlank()) {
                    Log.w("GamebrainApi", "Missing Gamebrain API key")
                } else {
                    header(HttpHeaders.Authorization, "Bearer $apiKey")
                }
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }
        }
    }
}

