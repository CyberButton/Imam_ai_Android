package com.nurtore.imam_ai

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurtore.imam_ai.model.ChatId
import com.nurtore.imam_ai.model.DbMessageWithImam
import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.model.Question
import com.nurtore.imam_ai.repo.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

class MainActivityViewModel(
    private val repo: Repo,
    private val dao: DbMessageWithImamDao
): ViewModel() {

    // private mutable list so that it cant be modified from other places
    @SuppressLint("MutableCollectionMutableState")
    private var _messagesList = mutableStateListOf<MessageWithImam>()
    val messagesList: List<MessageWithImam> = _messagesList

    private var chatId = "64e90de128bce87b8d7a86dc"


    fun initializeMessagesList() {
        CoroutineScope(Dispatchers.IO).launch {
            val messages = dao.getMessagesOrderedBySequence().map { it ->
                MessageWithImam(it.role, it.content)
            }

            withContext(Dispatchers.Main) {
                _messagesList.clear() // Clear the existing list
                _messagesList.addAll(messages) // Add the mapped messages
            }
        }
    }

    fun getNewChatId() {
        viewModelScope.launch {
            val response:String = repo.getChatId()
            chatId = response
            withContext(Dispatchers.IO) {
                dao.insertChatId(ChatId(chatId))
            }
        }
    }

    fun noChatId(): Boolean {
        val dBResponse = viewModelScope.async(Dispatchers.IO) {
            dao.numberOfChatId()
        }
        dBResponse.invokeOnCompletion {
            //left here ===========================================================================
        }
        return  dBResponse.getCompleted() == 0
    }

    fun deleteAllMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAllMessages()
        }
    }

    fun messageImam(question: String) {
        viewModelScope.launch {

            _messagesList.add(MessageWithImam("user", question))
            dao.addMessage(DbMessageWithImam("user", question))
            println(question)

            val response: String = repo.messageImam(chatId, Question(question))
            println(response)

            _messagesList.add(MessageWithImam("assistant", response))
            dao.addMessage(DbMessageWithImam("assistant", response))

            println("call successful")
        }
    }

    fun getMessagesList() {
        viewModelScope.launch {
            val response: List<MessageWithImam> = repo.getMessagesList(chatId)
            //_messagesList.clear()  // Clear the existing list if needed
            _messagesList.addAll(response)
        }
    }

    init {

    }

}