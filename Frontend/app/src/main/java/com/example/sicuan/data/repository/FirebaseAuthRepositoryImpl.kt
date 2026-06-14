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

    override suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUserInfo {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Firebase user tidak ditemukan setelah login")
        return user.toDomain()
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): FirebaseUserInfo {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Firebase user tidak ditemukan setelah registrasi")
        return user.toDomain()
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): FirebaseUserInfo {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("Firebase user tidak ditemukan")
        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .apply {
                if (displayName != null) setDisplayName(displayName)
                if (photoUrl != null) setPhotoUri(android.net.Uri.parse(photoUrl))
            }
            .build()
        
        user.updateProfile(profileUpdates).await()
        return user.toDomain()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toDomain(): FirebaseUserInfo {
        return FirebaseUserInfo(
            uid = uid,
            isAnonymous = isAnonymous,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }
}
