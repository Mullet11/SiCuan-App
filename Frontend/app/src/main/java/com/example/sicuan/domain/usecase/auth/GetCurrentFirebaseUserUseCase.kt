package com.example.sicuan.domain.usecase.auth

import com.example.sicuan.domain.model.FirebaseUserInfo
import com.example.sicuan.domain.repository.FirebaseAuthRepository

class GetCurrentFirebaseUserUseCase(
    private val repository: FirebaseAuthRepository
) {
    operator fun invoke(): FirebaseUserInfo? {
        return repository.getCurrentUser()
    }
}
