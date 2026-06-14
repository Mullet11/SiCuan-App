package com.example.sicuan.data.local

import android.content.Context
import com.example.sicuan.domain.repository.PinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PinRepositoryImpl(private val context: Context) : PinRepository {

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override suspend fun savePin(pin: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString(KEY_PIN, pin).apply()
        }
    }

    override suspend fun getPin(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_PIN, null)
        }
    }

    override suspend fun clearPin() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().remove(KEY_PIN).apply()
        }
    }

    override suspend fun isPinSet(): Boolean {
        return withContext(Dispatchers.IO) {
            sharedPreferences.contains(KEY_PIN)
        }
    }

    companion object {
        private const val PREF_NAME = "sicuan_security_prefs"
        private const val KEY_PIN = "sicuan_user_pin"
    }
}
