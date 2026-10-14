package com.example.data.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageBase64: String? = null,
    val mode: AIMode = AIMode.SUPER_GULABO,
    val isSpeaking: Boolean = false,
    val isError: Boolean = false
)
