package com.nurtore.imam_ai.model.prayerApiResponse


import com.google.gson.annotations.SerializedName

data class Date(
    @SerializedName("gregorian")
    val gregorian: Gregorian,
    @SerializedName("hijri")
    val hijri: Hijri,
    @SerializedName("readable")
    val readable: String,
    @SerializedName("timestamp")
    val timestamp: String
)