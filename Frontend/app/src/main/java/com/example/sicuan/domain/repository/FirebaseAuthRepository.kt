package com.example.sicuan.domain.repository

import com.example.sicuan.domain.model.FirebaseUserInfo

interface FirebaseAuthRepository {

    fun getCurrentUser(): FirebaseUserInfo?

    suspend fun signInAnonymously(): FirebaseUserInfo

    fun signOut()
}