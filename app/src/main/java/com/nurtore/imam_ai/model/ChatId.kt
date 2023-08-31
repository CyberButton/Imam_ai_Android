package com.nurtore.imam_ai.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatId (
    val chatId: String,

    @PrimaryKey(autoGenerate = false)
    val id: Int = 0
    )