package com.nurtore.imam_ai.api

import com.nurtore.imam_ai.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ImamAIAPI by lazy {
        retrofit.create(ImamAIAPI::class.java)
    }

}