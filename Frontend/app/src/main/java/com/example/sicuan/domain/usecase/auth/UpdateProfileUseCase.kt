package com.example.sicuan.domain.usecase.auth

import com.example.sicuan.domain.model.FirebaseUserInfo
import com.example.sicuan.domain.repository.FirebaseAuthRepository

class UpdateProfileUseCase(
    private val repository: FirebaseAuthRepository
) {
    suspend operator fun invoke(displayName: String?, photoUrl: String?): FirebaseUserInfo {
        return repository.updateProfile(displayName, photoUrl)
    }
}
