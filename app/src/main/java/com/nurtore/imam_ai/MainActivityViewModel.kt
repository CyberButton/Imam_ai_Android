package com.nurtore.imam_ai

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.repo.Repo
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repo: Repo): ViewModel() {

    // private mutable list so that it cant be modified from other places
    @SuppressLint("MutableCollectionMutableState")
    private var _messagesList = mutableStateListOf<String>("niga")

    val messagesList: List<String> = _messagesList

    private var x = 0;
    fun sendMessage() {
        println("pressed")
        _messagesList.add("button pressed bro" + x)
        x++;
        println(messagesList)
    }

    //val myChatId: MutableLiveData<Chat_id> = MutableLiveData()

    fun getChatId() {
        viewModelScope.launch {
            val response:String = repo.getChatId()
//            myChatId.value = response
            _messagesList.add(response)
        }
    }

    fun messageImam() {
        viewModelScope.launch {
            val response: String = repo.messageImam("64e90de128bce87b8d7a86dc")
            _messagesList.add(response)
        }
    }

    fun getMessagesList() {
        viewModelScope.launch {
            val response: List<MessageWithImam> = repo.getMessagesList("64e90de128bce87b8d7a86dc")
            //_messagesList.clear()  // Clear the existing list if needed
            _messagesList.addAll(response.map { it -> it.content })
        }
    }

}