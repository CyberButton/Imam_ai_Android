package com.nurtore.imam_ai.model.prayerApiResponse


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("date")
    val date: Date,
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("timings")
    val timings: Timings
)