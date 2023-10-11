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
}