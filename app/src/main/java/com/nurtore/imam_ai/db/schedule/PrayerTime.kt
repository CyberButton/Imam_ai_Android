package com.nurtore.imam_ai.db.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerTime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)
