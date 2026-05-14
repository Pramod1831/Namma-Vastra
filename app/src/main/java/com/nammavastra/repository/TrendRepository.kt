package com.nammavastra.repository

import android.content.Context
import android.content.SharedPreferences
import com.nammavastra.model.Trend
import com.nammavastra.service.GeminiService
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

class TrendRepository(
    context: Context
) {
    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("trend_cache", Context.MODE_PRIVATE)

    suspend fun fetchTrends(): Result<List<Trend>> = runCatching {
        if (!SupabaseProvider.isConfigured()) {
            return@runCatching emptyList()
        }

        val remote: List<Trend> = SupabaseProvider.client.get("${SupabaseProvider.baseUrl()}/rest/v1/trends") {
            parameter("select", "*")
            parameter("order", "month.desc")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }.body()
        remote
    }.recoverCatching { emptyList() }

    suspend fun enrichTrendDescription(trend: Trend): String {
        val cacheKey = "trend_${trend.id}"
        val cached = prefs.getString(cacheKey, null)
        if (!cached.isNullOrBlank() &&
            (!GeminiService.isConfigured() || cached != trend.description)
        ) {
            return cached
        }

        val prompt = """
            You are a handloom fashion advisor. In 2 sentences, describe why the trend "${trend.name}"
            with colors ${trend.colors} is relevant for Ilkal/Molakalmuru weavers targeting urban boutiques
            in India this season.
        """.trimIndent()

        val generated = GeminiService.generate(prompt)
        if (generated.isNotBlank()) {
            prefs.edit().putString(cacheKey, generated).apply()
            return generated
        }
        return cached ?: trend.description
    }
}
