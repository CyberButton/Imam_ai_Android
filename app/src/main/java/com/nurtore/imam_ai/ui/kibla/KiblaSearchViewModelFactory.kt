package com.nurtore.imam_ai.ui.kibla

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class KiblaSearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return KiblaSearchViewModel(context) as T
    }
}
