package com.example.sicuan.domain.usecase.auth

import com.example.sicuan.domain.repository.FirebaseAuthRepository

class SignOutUseCase(
    private val repository: FirebaseAuthRepository
) {
    operator fun invoke() {
        repository.signOut()
    }
}
