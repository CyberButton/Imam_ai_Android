package com.nurtore.imam_ai.db.schedule

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nurtore.imam_ai.db.messages.ChatId
import com.nurtore.imam_ai.db.messages.DbMessageWithImam

@Database(
    entities = [PrayerTime::class],
    version = 1 // possible to change schema in future, increment this value and add auto-migration
    //https://www.youtube.com/watch?v=hrJZIF7qSSw&t=1s&ab_channel=PhilippLackner
    ,exportSchema = true
)
abstract class ScheduleDatabase:RoomDatabase() {

    abstract val dao: ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getDatabase(context: Context): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}