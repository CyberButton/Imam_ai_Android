package com.nurtore.imam_ai.db.messages

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DbMessageWithImam::class, ChatId::class],
    version = 1, // possible to change schema in future, increment this value and add auto-migration
    //https://www.youtube.com/watch?v=hrJZIF7qSSw&t=1s&ab_channel=PhilippLackner
    exportSchema = true
    )
abstract class DbMessageWithImamDatabase:RoomDatabase() {

    abstract val dao: DbMessageWithImamDao
    companion object {
        @Volatile
        private var INSTANCE: DbMessageWithImamDatabase? = null

        fun getDatabase(context: Context): DbMessageWithImamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DbMessageWithImamDatabase::class.java,
                    "dbMessageWithImam_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}