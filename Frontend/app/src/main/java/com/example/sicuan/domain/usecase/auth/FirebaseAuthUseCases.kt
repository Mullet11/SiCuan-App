package com.example.sicuan.domain.usecase.auth

data class FirebaseAuthUseCases(
    val signInWithEmailAndPassword: SignInWithEmailAndPasswordUseCase,
    val signUpWithEmailAndPassword: SignUpWithEmailAndPasswordUseCase,
    val signOut: SignOutUseCase,
    val getCurrentFirebaseUser: GetCurrentFirebaseUserUseCase,
    val updateProfile: UpdateProfileUseCase
)
