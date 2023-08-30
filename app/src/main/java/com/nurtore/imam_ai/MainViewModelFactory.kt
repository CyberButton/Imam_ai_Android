package com.nurtore.imam_ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurtore.imam_ai.repo.Repo

class MainViewModelFactory(private val repo: Repo, private val dao: DbMessageWithImamDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(repo, dao) as T
    }

}