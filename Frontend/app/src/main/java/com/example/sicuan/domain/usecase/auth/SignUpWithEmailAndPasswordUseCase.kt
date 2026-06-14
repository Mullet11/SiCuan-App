package com.example.sicuan.domain.usecase.auth

import com.example.sicuan.domain.model.FirebaseUserInfo
import com.example.sicuan.domain.repository.FirebaseAuthRepository

class SignUpWithEmailAndPasswordUseCase(
    private val repository: FirebaseAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): FirebaseUserInfo {
        return repository.signUpWithEmailAndPassword(email, password)
    }
}
