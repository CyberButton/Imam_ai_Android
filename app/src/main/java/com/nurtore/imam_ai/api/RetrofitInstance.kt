package com.nurtore.imam_ai.api

import com.nurtore.imam_ai.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val oKHttpClient = OkHttpClient.Builder()
        .connectTimeout(35, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(35, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(oKHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ImamAIAPI by lazy {
        retrofit.create(ImamAIAPI::class.java)
    }

}