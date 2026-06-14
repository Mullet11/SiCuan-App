package com.example.sicuan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicuan.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val isLoading: Boolean = false
)

class AiAssistantViewModel : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private var generativeModel: GenerativeModel? = null

    init {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotEmpty()) {
            generativeModel = GenerativeModel(
                modelName = "gemini-flash-latest",
                apiKey = apiKey,
                systemInstruction = content {
                    text("Anda adalah SiCuan AI, asisten keuangan pribadi cerdas berbahasa Indonesia. Anda ahli dalam memberikan saran manajemen keuangan mahasiswa secara ringkas, ramah, dan solutif. Jangan memberikan jawaban yang terlalu panjang, dan gunakan emoji sesekali.")
                }
            )
            _chatHistory.value = listOf(
                ChatMessage(
                    text = "Halo! Saya SiCuan AI. Ada yang bisa saya bantu terkait pengelolaan keuangan Anda hari ini?",
                    isFromUser = false
                )
            )
        } else {
            _chatHistory.value = listOf(
                ChatMessage(
                    text = "⚠️ Gemini API Key belum diatur. Mohon tambahkan GEMINI_API_KEY di file local.properties Anda agar saya bisa membantu.",
                    isFromUser = false
                )
            )
        }
    }

    fun sendMessage(userMessage: String, financialContext: String = "") {
        if (userMessage.isBlank()) return

        val currentHistory = _chatHistory.value.toMutableList()
        currentHistory.add(ChatMessage(text = userMessage, isFromUser = true))
        _chatHistory.value = currentHistory

        if (generativeModel == null) {
            currentHistory.add(ChatMessage(text = "Sistem AI tidak dapat merespons karena API Key belum dikonfigurasi.", isFromUser = false))
            _chatHistory.value = currentHistory
            return
        }

        _isTyping.value = true

        viewModelScope.launch {
            try {
                val prompt = if (financialContext.isNotBlank()) {
                    "Konteks keuangan pengguna saat ini: $financialContext\n\nPertanyaan pengguna: $userMessage"
                } else {
                    userMessage
                }

                val response = generativeModel?.generateContent(prompt)

                val responseText = response?.text ?: "Maaf, saya tidak mengerti. Bisa ulangi?"

                val newHistory = _chatHistory.value.toMutableList()
                newHistory.add(ChatMessage(text = responseText, isFromUser = false))
                _chatHistory.value = newHistory
            } catch (e: Exception) {
                val errorHistory = _chatHistory.value.toMutableList()
                errorHistory.add(ChatMessage(text = "Kesalahan: ${e.message}", isFromUser = false))
                _chatHistory.value = errorHistory
            } finally {
                _isTyping.value = false
            }
        }
    }
}
