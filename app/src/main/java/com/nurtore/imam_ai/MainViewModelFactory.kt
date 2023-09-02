package com.nurtore.imam_ai

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurtore.imam_ai.repo.Repo

class MainViewModelFactory(
    private val repo: Repo,
    private val dao: DbMessageWithImamDao,
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(repo, dao, application) as T
    }

}