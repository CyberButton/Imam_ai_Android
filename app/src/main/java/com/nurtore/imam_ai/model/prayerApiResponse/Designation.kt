package com.nurtore.imam_ai.model.prayerApiResponse


import com.google.gson.annotations.SerializedName

data class Designation(
    @SerializedName("abbreviated")
    val abbreviated: String,
    @SerializedName("expanded")
    val expanded: String
)