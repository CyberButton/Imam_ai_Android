package com.nurtore.imam_ai.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nurtore.imam_ai.model.ChatId
import com.nurtore.imam_ai.model.DbMessageWithImam

@Database(
    entities = [DbMessageWithImam::class, ChatId::class],
    version = 1 // possible to change schema in future, increment this value and add auto-migration
    //https://www.youtube.com/watch?v=hrJZIF7qSSw&t=1s&ab_channel=PhilippLackner
)
abstract class DbMessageWithImamDatabase:RoomDatabase() {

    abstract val dao: DbMessageWithImamDao
}