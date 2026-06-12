package com.example.sicuan.domain.usecase.auth

import com.example.sicuan.domain.model.FirebaseUserInfo
import com.example.sicuan.domain.repository.FirebaseAuthRepository

class SignInAnonymouslyUseCase(
    private val repository: FirebaseAuthRepository
) {
    suspend operator fun invoke(): FirebaseUserInfo {
        return repository.signInAnonymously()
    }
}