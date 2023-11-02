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

    private val LATITUDE: String = "latitude"
    var latitude: String?
        get() = preferences.getString(LATITUDE, null)
        set(value) = preferences.edit().putString(LATITUDE, value).apply()

    private val LONGITUDE: String = "latitude"
    var longitude: String?
        get() = preferences.getString(LONGITUDE, null)
        set(value) = preferences.edit().putString(LONGITUDE, value).apply()

    private val FIRST_LAUCNH: String = "first_launch"
    var first_launch: Boolean
        get() = preferences.getBoolean(FIRST_LAUCNH, true)
        set(value) = preferences.edit().putBoolean(FIRST_LAUCNH, value).apply()

}