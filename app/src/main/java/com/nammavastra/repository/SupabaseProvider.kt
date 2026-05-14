package com.nammavastra.repository

import com.nammavastra.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupabaseProvider {
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(SupabaseProvider.json)
        }
    }

    fun isConfigured(): Boolean {
        return BuildConfig.SUPABASE_URL.isNotBlank() &&
            BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()
    }

    fun baseUrl(): String = BuildConfig.SUPABASE_URL.trimEnd('/')

    fun apiKey(): String = BuildConfig.SUPABASE_PUBLISHABLE_KEY

    fun publicUrl(bucket: String, path: String): String {
        return "${baseUrl()}/storage/v1/object/public/$bucket/$path"
    }
}
