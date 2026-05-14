package com.nammavastra.service

import com.nammavastra.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object GeminiService {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    fun isConfigured(): Boolean = BuildConfig.GEMINI_API_KEY.isNotBlank()

    suspend fun generate(prompt: String): String {
        if (!isConfigured()) {
            return ""
        }
        return runCatching {
            val response = client.post(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${BuildConfig.GEMINI_API_KEY}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                        )
                    )
                )
            }
            json.decodeFromString(GeminiResponse.serializer(), response.bodyAsText())
                .candidates.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
                .orEmpty()
                .trim()
        }.getOrDefault("").trim()
    }
}

@Serializable
private data class GeminiRequest(val contents: List<GeminiContent>)

@Serializable
private data class GeminiContent(val parts: List<GeminiPart>)

@Serializable
private data class GeminiPart(val text: String)

@Serializable
private data class GeminiResponse(val candidates: List<GeminiCandidate> = emptyList())

@Serializable
private data class GeminiCandidate(val content: GeminiGeneratedContent)

@Serializable
private data class GeminiGeneratedContent(val parts: List<GeminiPart>)
