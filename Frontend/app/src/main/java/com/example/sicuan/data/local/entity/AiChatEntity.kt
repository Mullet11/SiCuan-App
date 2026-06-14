package com.example.sicuan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sicuan.presentation.viewmodel.ChatMessage

@Entity(tableName = "ai_chats")
data class AiChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sessionId: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long
) {
    fun toDomainModel(): ChatMessage {
        return ChatMessage(
            text = text,
            isFromUser = isFromUser
        )
    }

    companion object {
        fun fromDomainModel(sessionId: String, chatMessage: ChatMessage): AiChatEntity {
            return AiChatEntity(
                sessionId = sessionId,
                text = chatMessage.text,
                isFromUser = chatMessage.isFromUser,
                timestamp = System.currentTimeMillis()
            )
        }
    }
}
