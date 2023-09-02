package com.nurtore.imam_ai.api

import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.model.Question
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ImamAIAPI {
    // wrap with response to hande exceptions : https://www.youtube.com/watch?v=sBCE_hOFnQU&list=PLSrm9z4zp4mF1Ssdfuocy2XH5Bw4wLLNw&index=1&ab_channel=Stevdza-San
    @GET("messages/en/new")
    suspend fun getChatId(): String

    @POST("messages/{chat_id}")
    suspend fun messageImam(
        @Path("chat_id") chatId: String,
        @Body question: Question
    ): String

    @GET("messages/{chat_id}")
    suspend fun getMessagesList(
        @Path("chat_id") chatId: String
    ): List<MessageWithImam>
}