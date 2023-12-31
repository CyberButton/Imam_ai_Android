package com.nurtore.imam_ai.db.messages

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DbMessageWithImamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(dbMessageWithImam: DbMessageWithImam)

    @Query("DELETE FROM dbmessagewithimam")
    suspend fun deleteAllMessages()

    //can return Flow<> or LiveData<> to observe changes in db
    // ascending order
    // it may be difficult process the large number of messages,
    // future goal to optimize this part
    @Query("SELECT * FROM dbmessagewithimam ORDER BY id ASC")
    fun getMessagesOrderedBySequence(): List<DbMessageWithImam>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatId(chatId: ChatId)

    @Query("SELECT chatId FROM chatid LIMIT 1")
    fun getChatId(): String

    @Query("SELECT COUNT(*) FROM chatid")
    fun numberOfChatId(): Int

    @Query("SELECT COUNT(*) FROM dbmessagewithimam")
    fun numberOfMessages(): Int
}