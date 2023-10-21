package com.nurtore.imam_ai.model.prayerApiResponse


import com.google.gson.annotations.SerializedName

data class Month(
    @SerializedName("en")
    val en: String,
    @SerializedName("number")
    val number: Int
)