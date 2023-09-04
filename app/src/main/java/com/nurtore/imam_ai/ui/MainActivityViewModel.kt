package com.nurtore.imam_ai.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.DbMessageWithImamDao
import com.nurtore.imam_ai.model.ChatId
import com.nurtore.imam_ai.model.DbMessageWithImam
import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.model.Question
import com.nurtore.imam_ai.utils.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModel(
    private val repo: Repo,
    private val dao: DbMessageWithImamDao,
    application: Application
) : AndroidViewModel(application) {

    // private mutable list so that it cant be modified from other places
    @SuppressLint("MutableCollectionMutableState")
    private var _messagesList = mutableStateListOf<MessageWithImam>()
    val messagesList: List<MessageWithImam> = _messagesList

    private var chatId = ""

    val isConnected = mutableStateOf(isOnline(getApplication()))

    // only for SDK 24+ (inclusive)
    private val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var networkCallback = object : ConnectivityManager.NetworkCallback() {}



    private fun initializeMessagesList() {
        CoroutineScope(Dispatchers.IO).launch {
            val messages = dao.getMessagesOrderedBySequence().map { it ->
                MessageWithImam(it.role, it.content)
            }

            withContext(Dispatchers.Main) {
                _messagesList.clear() // Clear the existing list
                _messagesList.addAll(messages) // Add the mapped messages
            }
            println("messages loaded from db")
        }
    }

    fun getNewChatId() {
        viewModelScope.launch {
            val response: String = repo.getChatId().body()!!
            chatId = response
            println("new chat id is $chatId")
            runBlocking(Dispatchers.IO) {
                _messagesList.add(
                    MessageWithImam("assistant", "Assalamu Alaikum!" +
                            " How may I help you?"))
                dao.insertChatId(ChatId(chatId))
                dao.addMessage(
                    DbMessageWithImam("assistant", "Assalamu Alaikum!" +
                            " How may I help you?")
                )
            }
        }
    }

    fun getNewChatId_Next_Ver() {
//        viewModelScope.launch {
//            var attempt = 0
//            val maxAttempts = 3
//
//            while (attempt < maxAttempts) {
//                try {
//                    val response: Response<String> = repo.getChatId()
//                    if (response.isSuccessful) {
//                        val newChatId = response.body()
//                        if (newChatId != null) {
//                            chatId = newChatId
//                            println("new chat id is $chatId")
//                            withContext(Dispatchers.IO) {
//                                dao.insertChatId(ChatId(chatId))
//                                dao.addMessage(
//                                    DbMessageWithImam("assistant", "Assalamu Alaikum!" +
//                                            " How may I help you?")
//                                )
//                                _messagesList.add(
//                                    MessageWithImam("assistant", "Assalamu Alaikum!" +
//                                            " How may I help you?")
//                                )
//                            }
//                            break // Exit while loop if the operation is successful
//                        } else {
//                            println("Received null chat ID from the server.")
//                        }
//                    } else {
//                        println("Failed to get a new chat ID: ${response.errorBody()?.string()}")
//                    }
//                } catch (e: IOException) {
//                    println("Network error: $e")
//                } catch (e: HttpException) {
//                    println("HTTP error: $e")
//                } catch (e: Exception) {
//                    println("An unexpected error occurred: $e")
//                }
//
//                attempt++
//                if (attempt < maxAttempts) {
//                    delay(1000) // Delay 1 seconds before the next attempt
//                }
//            }
//
//            if (attempt == maxAttempts) {
//                println("Max retry attempts reached.")
//                // Handle this case as you need
//            }
//        }
    }


    private fun loadChatIdFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            chatId = dao.getChatId()
        }
    }

    private suspend fun noChatId(): Boolean {
        val dBResponse = viewModelScope.async(Dispatchers.IO) {
            dao.numberOfChatId()
        }
        dBResponse.await()
        return  dBResponse.getCompleted() == 0
    }

    private suspend fun noMessages(): Boolean {
        val dBResponse = viewModelScope.async(Dispatchers.IO) {
            dao.numberOfMessages()
        }
        dBResponse.await()
        return  dBResponse.getCompleted() == 0
    }

    fun deleteAllMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            _messagesList.clear()
            dao.deleteAllMessages()
        }
    }

    fun messageImam(question: String) {
        viewModelScope.launch {

            _messagesList.add(MessageWithImam("user", question))
            println(question)

            try {
                val response = repo.messageImam(chatId, Question(question))
                if(response.isSuccessful && !response.body().isNullOrEmpty()) {
                    dao.addMessage(DbMessageWithImam("user", question))
                    _messagesList.add(MessageWithImam("assistant", response.body()!!))
                    dao.addMessage(DbMessageWithImam("assistant", response.body()!!))
                    println("call successful")
                }
            } catch (e: IOException) {
                _messagesList.add(MessageWithImam("assistant", "sorry there was error"))
                println("Network error: $e")
            } catch (e: HttpException) {
                _messagesList.add(MessageWithImam("assistant", "sorry there was error"))
                println("HTTP error: $e")
            } catch (e: Exception) {
                _messagesList.add(MessageWithImam("assistant", "sorry there was error"))
                println("An unexpected error occurred: $e")
            }
        }
    }

    private fun getMessagesList() {

        _messagesList.add(
            MessageWithImam("assistant", "Assalamu Alaikum!" +
                    " How may I help you?")
        )

        viewModelScope.launch {
            val response: List<MessageWithImam> = repo.getMessagesList(chatId).body()!!
            _messagesList.clear()  // Clear the existing list if needed
            _messagesList.addAll(response)
            for(message in response) {
                dao.addMessage(DbMessageWithImam(message.role, message.content))
            }
            println("messages loaded from API")
        }
    }

    init {

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModelScope.launch(Dispatchers.Main) {
                    isConnected.value = true
                }
                println("yes internet")
            }
            override fun onLost(network: Network) {
                viewModelScope.launch(Dispatchers.Main) {
                    isConnected.value = false
                }
                println("no internet")
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)


        viewModelScope.launch {
            val dBResponse = viewModelScope.async(Dispatchers.IO) {
                dao.numberOfChatId()
            }
            dBResponse.await()
            if (dBResponse.getCompleted() == 0) {
                println("no chat id was found")
                val response = viewModelScope.async(Dispatchers.IO){
                    repo.getChatId()
                }
                response.await()
                chatId = response.getCompleted().body()!!
                println("new chat id is $chatId")
                withContext(Dispatchers.IO) {
                    dao.insertChatId(ChatId(chatId))
                }
            } else {
                println("chat id was found in db")
                loadChatIdFromDb()
            }

            if (noMessages()) {
                println("no local messages were found")
                getMessagesList()
            } else {
                initializeMessagesList()
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}

