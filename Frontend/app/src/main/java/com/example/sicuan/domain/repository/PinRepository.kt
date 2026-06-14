package com.example.sicuan.domain.repository

interface PinRepository {
    suspend fun savePin(pin: String)
    suspend fun getPin(): String?
    suspend fun clearPin()
    suspend fun isPinSet(): Boolean
}
