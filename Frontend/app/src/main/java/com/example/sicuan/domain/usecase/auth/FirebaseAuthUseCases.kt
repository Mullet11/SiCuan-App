package com.example.sicuan.domain.usecase.auth

data class FirebaseAuthUseCases(
    val signInAnonymously: SignInAnonymouslyUseCase,
    val getCurrentFirebaseUser: GetCurrentFirebaseUserUseCase
)