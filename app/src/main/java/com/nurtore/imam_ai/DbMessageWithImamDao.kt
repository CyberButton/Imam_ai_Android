package com.nurtore.imam_ai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nurtore.imam_ai.model.DbMessageWithImam

@Dao
interface DbMessageWithImamDao {

    @Insert
    suspend fun addMessage(dbMessageWithImam: DbMessageWithImam)

    @Query("DELETE FROM dbmessagewithimam")
    suspend fun deleteAllMessages()

    //can return Flow<> or LiveData<> to observe changes in db
    // ascending order
    @Query("SELECT * FROM dbmessagewithimam ORDER BY sequence ASC")
    fun getMessagesOrderedBySequence(): List<DbMessageWithImam>
}