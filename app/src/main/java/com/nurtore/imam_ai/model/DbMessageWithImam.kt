package com.nurtore.imam_ai.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbMessageWithImam(
    val role: String,
    val content: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
    )
