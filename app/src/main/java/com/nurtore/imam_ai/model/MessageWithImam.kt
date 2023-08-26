package com.nurtore.imam_ai.model

data class MessageWithImam(
    val role: String,
    val content: String
) {
    fun sentByImam() : Boolean {
        return role == "assistant"
    }
}
