package com.nammavastra.repository

import android.content.Context
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.nammavastra.model.StorySubmission
import com.nammavastra.model.WeaverProfile
import com.nammavastra.model.WeaverStory
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
import java.io.ByteArrayOutputStream
import java.util.UUID

class StoryRepository(
    context: Context
) {
    private val appContext = context.applicationContext

    suspend fun fetchStories(): Result<List<WeaverStory>> = runCatching {
        if (!SupabaseProvider.isConfigured()) {
            return@runCatching emptyList()
        }

        val remote: List<WeaverStory> = SupabaseProvider.client.get("${SupabaseProvider.baseUrl()}/rest/v1/stories") {
            parameter("select", "*")
            parameter("order", "id.desc")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }.body()
        remote
    }.recoverCatching { emptyList() }

    suspend fun fetchWeavers(): Result<List<WeaverProfile>> = runCatching {
        if (!SupabaseProvider.isConfigured()) {
            return@runCatching emptyList()
        }

        val remote: List<WeaverProfile> = SupabaseProvider.client.get("${SupabaseProvider.baseUrl()}/rest/v1/weavers") {
            parameter("select", "*")
            parameter("order", "id.desc")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }.body()
        remote
    }.recoverCatching { emptyList() }

    suspend fun submitStorySubmission(submission: StorySubmission) {
        val payload = submission.copy(
            id = if (submission.id.isBlank()) UUID.randomUUID().toString() else submission.id,
            status = "pending"
        )
        val response = SupabaseProvider.client.post("${SupabaseProvider.baseUrl()}/rest/v1/story_submissions") {
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
            header("Prefer", "return=minimal")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }
        if (!response.status.isSuccess()) {
            error("Submission failed: ${response.status.value} ${response.bodyAsText()}")
        }
    }

    suspend fun submitStorySubmission(
        submission: StorySubmission,
        imageUri: Uri,
        contentResolver: ContentResolver
    ) {
        if (!SupabaseProvider.isConfigured()) error("Supabase is not configured yet.")
        val objectId = if (submission.id.isBlank()) UUID.randomUUID().toString() else submission.id
        val imagePath = "submissions/${System.currentTimeMillis()}-$objectId.jpg"
        val bytes = compressImage(contentResolver, imageUri)

        val uploadResponse = SupabaseProvider.client.post("${SupabaseProvider.baseUrl()}/storage/v1/object/sarees/$imagePath") {
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
            header("x-upsert", "false")
            header(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
            header(HttpHeaders.ContentDisposition, "filename=\"$objectId.jpg\"")
            setBody(bytes)
        }
        if (!uploadResponse.status.isSuccess()) {
            error("Story image upload failed: ${uploadResponse.status.value} ${uploadResponse.bodyAsText()}")
        }

        submitStorySubmission(
            submission.copy(
                id = objectId,
                imageUrl = SupabaseProvider.publicUrl("sarees", imagePath)
            )
        )
    }

    suspend fun fetchPendingSubmissions(): Result<List<StorySubmission>> = runCatching {
        if (!SupabaseProvider.isConfigured()) return@runCatching emptyList<StorySubmission>()
        val remote: List<StorySubmission> = SupabaseProvider.client.get("${SupabaseProvider.baseUrl()}/rest/v1/story_submissions") {
            parameter("select", "*")
            parameter("status", "eq.pending")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }.body()
        remote
    }.recoverCatching { emptyList<StorySubmission>() }

    suspend fun approveSubmission(submission: StorySubmission) {
        val story = WeaverStory(
            id = if (submission.id.isBlank()) UUID.randomUUID().toString() else submission.id,
            title = submission.title,
            content = submission.content,
            imageUrl = submission.imageUrl,
            section = submission.section,
            weaverName = submission.weaverName,
            village = submission.village
        )
        val weaver = WeaverProfile(
            id = story.id,
            name = submission.weaverName,
            village = submission.village,
            imageUrl = submission.imageUrl,
            subtitle = submission.subtitle
        )

        val storyResponse = SupabaseProvider.client.post("${SupabaseProvider.baseUrl()}/rest/v1/stories") {
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
            header("Prefer", "resolution=merge-duplicates,return=minimal")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(story)
        }
        if (!storyResponse.status.isSuccess()) {
            error("Story publish failed: ${storyResponse.status.value} ${storyResponse.bodyAsText()}")
        }
        val weaverResponse = SupabaseProvider.client.post("${SupabaseProvider.baseUrl()}/rest/v1/weavers") {
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
            header("Prefer", "resolution=merge-duplicates,return=minimal")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(weaver)
        }
        if (!weaverResponse.status.isSuccess()) {
            error("Weaver publish failed: ${weaverResponse.status.value} ${weaverResponse.bodyAsText()}")
        }
        rejectSubmission(submission.id)
    }

    suspend fun rejectSubmission(id: String) {
        val response = SupabaseProvider.client.delete("${SupabaseProvider.baseUrl()}/rest/v1/story_submissions") {
            parameter("id", "eq.$id")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }
        if (!response.status.isSuccess()) {
            error("Unable to remove submission: ${response.status.value} ${response.bodyAsText()}")
        }
    }

    suspend fun deleteStory(id: String) {
        if (!SupabaseProvider.isConfigured()) return
        val response = SupabaseProvider.client.delete("${SupabaseProvider.baseUrl()}/rest/v1/stories") {
            parameter("id", "eq.$id")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }
        if (!response.status.isSuccess()) {
            error("Unable to delete story: ${response.status.value} ${response.bodyAsText()}")
        }
    }

    suspend fun deleteWeaver(id: String) {
        if (!SupabaseProvider.isConfigured()) return
        val response = SupabaseProvider.client.delete("${SupabaseProvider.baseUrl()}/rest/v1/weavers") {
            parameter("id", "eq.$id")
            header("apikey", SupabaseProvider.apiKey())
            header("Authorization", "Bearer ${SupabaseProvider.apiKey()}")
        }
        if (!response.status.isSuccess()) {
            error("Unable to delete weaver: ${response.status.value} ${response.bodyAsText()}")
        }
    }

    fun defaultWeavers(): List<WeaverProfile> = emptyList()

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
