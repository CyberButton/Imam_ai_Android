package com.nurtore.imam_ai

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nurtore.imam_ai.model.DbMessageWithImam

@Database(
    entities = [DbMessageWithImam::class],
    version = 1
)
abstract class DbMessageWithImamDatabase:RoomDatabase() {

    abstract val dao: DbMessageWithImamDao
}