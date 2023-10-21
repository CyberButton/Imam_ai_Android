package com.nurtore.imam_ai.api

import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.model.Question
import com.nurtore.imam_ai.model.prayerApiResponse.PrayerTimeResponse
import retrofit2.Response

class Repo {

    suspend fun getChatId(): Response<String> {
        return RetrofitInstance.api.getChatId()
    }

    suspend fun messageImam(chatId: String, question: Question): Response<String> {
        return RetrofitInstance.api.messageImam(chatId, question)
    }

    suspend fun getMessagesList(chatId: String): Response<List<MessageWithImam>> {
        return RetrofitInstance.api.getMessagesList(chatId)
    }

    suspend fun getPrayerSchedule(url: String): Response<PrayerTimeResponse> {
        return RetrofitInstance.api.getPrayerSchedule(url)
    }
}