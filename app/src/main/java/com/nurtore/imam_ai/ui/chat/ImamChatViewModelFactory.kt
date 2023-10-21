package com.nurtore.imam_ai.ui.chat

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.messages.DbMessageWithImamDao

class ImamChatViewModelFactory(
    private val repo: Repo,
    private val dao: DbMessageWithImamDao,
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImamChatViewModel(repo, dao, application) as T
    }
}