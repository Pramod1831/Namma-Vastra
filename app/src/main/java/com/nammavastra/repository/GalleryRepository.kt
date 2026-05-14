package com.nammavastra.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.nammavastra.model.Saree
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.builtins.ListSerializer
import java.io.ByteArrayOutputStream
import java.util.UUID

class GalleryRepository(
    context: Context
) {
    private val appContext = context.applicationContext

    suspend fun fetchSarees(): Result<List<Saree>> = runCatching {
        if (!SupabaseProvider.isConfigured()) {
            return@runCatching emptyList()
        }

        val remote: List<Saree> = SupabaseProvider.client.get("${SupabaseProvider.baseUrl()}/rest/v1/sarees") {
            parameter("select", "*")
            parameter("order", "id.desc")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }.body()
        remote
    }.recoverCatching { emptyList() }

    suspend fun getSareeById(id: String): Saree? = runCatching {
        if (!SupabaseProvider.isConfigured()) {
            return@runCatching null
        }

        val remote: List<Saree> = SupabaseProvider.client.get("${SupabaseProvider.baseUrl()}/rest/v1/sarees") {
            parameter("select", "*")
            parameter("id", "eq.$id")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }.body()

        remote.firstOrNull()
    }.getOrNull()

    suspend fun uploadSaree(
        saree: Saree,
        imageUri: Uri,
        contentResolver: ContentResolver
    ) {
        if (!SupabaseProvider.isConfigured()) {
            error("Supabase is not configured yet.")
        }

        val bytes = compressImage(contentResolver, imageUri)
        val objectId = if (saree.id.isBlank()) UUID.randomUUID().toString() else saree.id
        val imagePath = "uploads/${System.currentTimeMillis()}-$objectId.jpg"

        val uploadResponse = SupabaseProvider.client.post("${SupabaseProvider.baseUrl()}/storage/v1/object/sarees/$imagePath") {
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
            header("x-upsert", "false")
            header(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
            header(HttpHeaders.ContentDisposition, "filename=\"$objectId.jpg\"")
            setBody(bytes)
        }
        if (!uploadResponse.status.isSuccess()) {
            error("Image upload failed: ${uploadResponse.status.value} ${uploadResponse.bodyAsText()}")
        }

        val payload = saree.copy(
            id = objectId,
            imageUrl = SupabaseProvider.publicUrl("sarees", imagePath)
        )

        val insertResponse = SupabaseProvider.client.post("${SupabaseProvider.baseUrl()}/rest/v1/sarees") {
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
            header("Prefer", "return=minimal")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }
        if (!insertResponse.status.isSuccess()) {
            error("Saree save failed: ${insertResponse.status.value} ${insertResponse.bodyAsText()}")
        }
    }

    suspend fun deleteSaree(saree: Saree) {
        if (!SupabaseProvider.isConfigured()) return
        val deletedById = if (saree.id.isNotBlank()) {
            val response = SupabaseProvider.client.delete("${SupabaseProvider.baseUrl()}/rest/v1/sarees") {
                parameter("id", "eq.${saree.id}")
                header("Prefer", "return=representation")
                header("apikey", SupabaseProvider.apiKey())
                header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
            }
            if (!response.status.isSuccess()) {
                error("Unable to delete saree: ${response.status.value} ${response.bodyAsText()}")
            }
            runCatching {
                SupabaseProvider.json.decodeFromString(
                    ListSerializer(Saree.serializer()),
                    response.bodyAsText()
                )
            }.getOrDefault(emptyList())
        } else {
            emptyList()
        }

        if (deletedById.isNotEmpty()) return

        val fallbackResponse = SupabaseProvider.client.delete("${SupabaseProvider.baseUrl()}/rest/v1/sarees") {
            parameter("name", "eq.${saree.name}")
            parameter("weaver_name", "eq.${saree.weaverName}")
            parameter("location", "eq.${saree.location}")
            header("Prefer", "return=representation")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }
        if (!fallbackResponse.status.isSuccess()) {
            error("Fallback delete failed: ${fallbackResponse.status.value} ${fallbackResponse.bodyAsText()}")
        }
        val deletedFallback = runCatching {
            SupabaseProvider.json.decodeFromString(
                ListSerializer(Saree.serializer()),
                fallbackResponse.bodyAsText()
            )
        }.getOrDefault(emptyList())
        if (deletedFallback.isEmpty()) {
            error(
                "No loom gallery row was deleted for id: ${saree.id.ifBlank { "missing-id" }}. " +
                    "This usually means the public.sarees delete policy is missing or the row data no longer matches."
            )
        }
    }

    private fun compressImage(contentResolver: ContentResolver, imageUri: Uri): ByteArray {
        val source = contentResolver.openInputStream(imageUri)
            ?: error("Unable to read selected image.")
        val bitmap = BitmapFactory.decodeStream(source)
        source.close()

        val maxWidth = 800
        val scale = maxWidth.toFloat() / bitmap.width.toFloat()
        val scaled = if (bitmap.width > maxWidth) {
            Bitmap.createScaledBitmap(
                bitmap,
                maxWidth,
                (bitmap.height * scale).toInt(),
                true
            )
        } else {
            bitmap
        }

        return ByteArrayOutputStream().use { output ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, output)
            output.toByteArray()
        }
    }
}
