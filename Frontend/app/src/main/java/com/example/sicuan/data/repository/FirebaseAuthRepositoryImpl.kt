package com.example.sicuan.data.repository

import com.example.sicuan.domain.model.FirebaseUserInfo
import com.example.sicuan.domain.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthRepository {

    override fun getCurrentUser(): FirebaseUserInfo? {
        return firebaseAuth.currentUser?.toDomain()
    }

    override suspend fun signInAnonymously(): FirebaseUserInfo {
        val existingUser = firebaseAuth.currentUser

        if (existingUser != null) {
            return existingUser.toDomain()
        }

        val result = firebaseAuth.signInAnonymously().await()

        val user = result.user
            ?: throw IllegalStateException("Firebase user tidak ditemukan setelah anonymous login")

        return user.toDomain()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toDomain(): FirebaseUserInfo {
        return FirebaseUserInfo(
            uid = uid,
            isAnonymous = isAnonymous,
            email = email
        )
    }
}