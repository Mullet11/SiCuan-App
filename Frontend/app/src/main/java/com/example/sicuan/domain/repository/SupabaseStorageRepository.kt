package com.example.sicuan.domain.repository

import java.io.File

interface SupabaseStorageRepository {
    suspend fun uploadProfilePicture(uid: String, file: File): Result<String>
}
