package com.nurtore.imam_ai.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private val save_data = "data"
    private val preferences: SharedPreferences =
        context.getSharedPreferences(save_data, Context.MODE_PRIVATE)

    private val CAlCULATION_METHOD: String = "method"
    var calculationType: Int?
        get() = preferences.getInt(CAlCULATION_METHOD, 2)
        set(value) = preferences.edit().putInt(CAlCULATION_METHOD, value ?: 2).apply()

    private val LOCATION_CITY: String = "city"
    var locationCity: String?
        get() = preferences.getString(LOCATION_CITY, null)
        set(value) = preferences.edit().putString(LOCATION_CITY, value).apply()

    private val LOCATION_COUNTRY: String = "country"
    var locationCountry: String?
        get() = preferences.getString(LOCATION_COUNTRY, null)
        set(value) = preferences.edit().putString(LOCATION_COUNTRY, value).apply()

}