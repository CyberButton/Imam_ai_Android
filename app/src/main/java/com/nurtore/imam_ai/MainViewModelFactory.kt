package com.nurtore.imam_ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurtore.imam_ai.repo.Repo

class MainViewModelFactory(private val repo: Repo): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(repo) as T
    }

}