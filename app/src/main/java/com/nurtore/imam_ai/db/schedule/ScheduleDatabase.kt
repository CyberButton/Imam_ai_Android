package com.nurtore.imam_ai.db.schedule

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nurtore.imam_ai.db.messages.ChatId
import com.nurtore.imam_ai.db.messages.DbMessageWithImam

@Database(
    entities = [PrayerTime::class],
    version = 1 // possible to change schema in future, increment this value and add auto-migration
    //https://www.youtube.com/watch?v=hrJZIF7qSSw&t=1s&ab_channel=PhilippLackner
)
abstract class ScheduleDatabase:RoomDatabase() {

    abstract val dao: ScheduleDao
}