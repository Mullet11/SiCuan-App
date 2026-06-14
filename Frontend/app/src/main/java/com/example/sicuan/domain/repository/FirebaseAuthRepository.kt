package com.example.sicuan.domain.repository

import com.example.sicuan.domain.model.FirebaseUserInfo

interface FirebaseAuthRepository {

    fun getCurrentUser(): FirebaseUserInfo?

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUserInfo

    suspend fun signUpWithEmailAndPassword(email: String, password: String): FirebaseUserInfo

    suspend fun updateProfile(displayName: String?, photoUrl: String?): FirebaseUserInfo

    fun signOut()
}
