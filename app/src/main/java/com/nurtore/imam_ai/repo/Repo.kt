package com.nurtore.imam_ai.repo

import com.nurtore.imam_ai.api.RetrofitInstance
import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.model.Question
import retrofit2.Response

class Repo {

    suspend fun getChatId(): String {
        return RetrofitInstance.api.getChatId()
    }

    suspend fun messageImam(chatId: String, question: Question): String {
        return RetrofitInstance.api.messageImam(chatId, question)
    }

    suspend fun getMessagesList(chatId: String): List<MessageWithImam> {
        return RetrofitInstance.api.getMessagesList(chatId)
    }

}