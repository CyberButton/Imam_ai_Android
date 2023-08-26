package com.nurtore.imam_ai.repo

import com.nurtore.imam_ai.api.RetrofitInstance
import com.nurtore.imam_ai.model.MessageWithImam

class Repo {

    suspend fun getChatId(): String {
        return RetrofitInstance.api.getChatId()
    }

    suspend fun messageImam(chatId: String): String {
        return RetrofitInstance.api.messageImam(chatId)
    }

    suspend fun getMessagesList(chatId: String): List<MessageWithImam> {
        return RetrofitInstance.api.getMessagesList(chatId)
    }

}