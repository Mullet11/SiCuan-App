package com.example.sicuan.data.repository

import com.example.sicuan.domain.repository.SupabaseStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class SupabaseStorageRepositoryImpl(
    private val client: OkHttpClient
) : SupabaseStorageRepository {

    // Kunci API yang diberikan pengguna
    private val supabaseUrl = "https://dfwsscbofavswxtbtpwb.supabase.co"
    private val supabaseKey = "sb_publishable_OQ62BI_eOLZyuZrHpSKXgA_FXBVj5cU"
    private val bucketName = "profile_pictures"

    override suspend fun uploadProfilePicture(uid: String, file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${uid}_${System.currentTimeMillis()}.jpg"
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("$supabaseUrl/storage/v1/object/$bucketName/$fileName")
                .addHeader("Authorization", "Bearer $supabaseKey")
                .addHeader("apikey", supabaseKey)
                .post(requestBody) // Use POST to upload
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // If successful, return the public URL
                val publicUrl = "$supabaseUrl/storage/v1/object/public/$bucketName/$fileName"
                Result.success(publicUrl)
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Result.failure(Exception("Upload failed: ${response.code} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
