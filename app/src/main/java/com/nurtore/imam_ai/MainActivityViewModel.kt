package com.nurtore.imam_ai

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurtore.imam_ai.model.DbMessageWithImam
import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.model.Question
import com.nurtore.imam_ai.repo.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(
    private val repo: Repo,
    private val dao: DbMessageWithImamDao
): ViewModel() {

    // private mutable list so that it cant be modified from other places
    @SuppressLint("MutableCollectionMutableState")
    private var _messagesList = mutableStateListOf<MessageWithImam>()
    val messagesList: List<MessageWithImam> = _messagesList

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

    private var x = 0;
//    fun sendMessage() {
//        println("pressed")
//        _messagesList.add("button pressed bro" + x)
//        x++;
//        println(messagesList)
//    }

    //val myChatId: MutableLiveData<Chat_id> = MutableLiveData()

//    fun getChatId() {
//        viewModelScope.launch {
//            val response:String = repo.getChatId()
////            myChatId.value = response
//            _messagesList.add(response)
//        }
//    }
    fun deleteAllMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAllMessages()
        }
    }

    fun messageImam(question: String) {
        viewModelScope.launch {
            println(question)
            val response: String = repo.messageImam("64e90de128bce87b8d7a86dc", Question(question))
            println(response)
            _messagesList.add(MessageWithImam("user", question))
            dao.addMessage(DbMessageWithImam(++x,"user", question))

            _messagesList.add(MessageWithImam("assistant", response))
            dao.addMessage(DbMessageWithImam(++x,"assistant", response))
            println("call successful")
        }
    }

    fun getMessagesList() {
        viewModelScope.launch {
            val response: List<MessageWithImam> = repo.getMessagesList("64e90de128bce87b8d7a86dc")
            //_messagesList.clear()  // Clear the existing list if needed
            _messagesList.addAll(response)
        }
    }

}