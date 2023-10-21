package com.nurtore.imam_ai.ui.chat

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.messages.DbMessageWithImamDao
import com.nurtore.imam_ai.db.messages.ChatId
import com.nurtore.imam_ai.db.messages.DbMessageWithImam
import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.model.Question
import com.nurtore.imam_ai.utils.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ImamChatViewModel(
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

    // 0 - no chatId (show retry button)
    // 1 - loading
    // 3 - success
    val chatIdState = mutableStateOf(1)

    private val _scroll = mutableStateOf(0)
    val scroll = _scroll

    val typing = mutableStateOf(false)

    private val _listState = mutableStateOf(LazyListState())
    val listState: State<LazyListState> get() = _listState

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

            _scroll.value++
            println("messages loaded from db")
        }
    }

    fun getNewChatId() {
        viewModelScope.launch {
            var attempt = 0
            val maxAttempts = 3
            chatIdState.value = 1
            while (attempt < maxAttempts) {
                try {
                    val response = viewModelScope.async(Dispatchers.IO) {
                        repo.getChatId()
                    }
                    response.await()
                    if (response.getCompleted().isSuccessful && !response.getCompleted().body()
                            .isNullOrEmpty()
                    ) {
                        chatId = response.getCompleted().body()!!
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
                        chatIdState.value = 3
                        break
                    }
                } catch (e: IOException) {
                    chatIdState.value = 1
                    println("Network error: $e")
                } catch (e: HttpException) {
                    chatIdState.value = 1
                    println("HTTP error: $e")
                } catch (e: Exception) {
                    chatIdState.value = 1
                    println("An unexpected error occurred: $e")
                }
                attempt++
                if (attempt < maxAttempts) {
                    delay(1000) // Delay 1 seconds before the next attempt
                }
            }
            if (attempt == maxAttempts) {
                println("Max retry attempts reached.")
                chatIdState.value = 0
            }
        }
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

            typing.value = true

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
            typing.value = false
            _scroll.value++
        }
    }

    private fun getMessagesList() {

        _messagesList.add(
            MessageWithImam("assistant", "Assalamu Alaikum!" +
                    " How may I help you?")
        )
        var loaded = false
        viewModelScope.launch {
            try {
                val response: List<MessageWithImam> = repo.getMessagesList(chatId).body()!!
                _messagesList.clear()
                _messagesList.addAll(response)
                for(message in response) {
                    dao.addMessage(DbMessageWithImam(message.role, message.content))
                }
                loaded = true
                println("messages loaded from API")
            } catch (e: IOException) {
                println("Network error: $e")
            } catch (e: HttpException) {
                println("HTTP error: $e")
            } catch (e: Exception) {
                println("An unexpected error occurred: $e")
            }
            if (!loaded) {
                dao.addMessage(
                    DbMessageWithImam("assistant", "Assalamu Alaikum!" +
                            " How may I help you?")
                )
                println("api failed, empty start")
            }
            _scroll.value++
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
                var attempt = 0
                val maxAttempts = 3

                while (attempt < maxAttempts) {
                    try {
                        val response = viewModelScope.async(Dispatchers.IO) {
                            repo.getChatId()
                        }
                        response.await()
                        if(response.getCompleted().isSuccessful && !response.getCompleted().body().isNullOrEmpty()) {
                            chatId = response.getCompleted().body()!!
                            println("new chat id is $chatId")
                            withContext(Dispatchers.IO) {
                                dao.insertChatId(ChatId(chatId))
                            }
                            chatIdState.value = 3
                            break
                        }
                    } catch (e: IOException) {
                        chatIdState.value = 1
                        println("Network error: $e")
                    } catch (e: HttpException) {
                        chatIdState.value = 1
                        println("HTTP error: $e")
                    } catch (e: Exception) {
                        chatIdState.value = 1
                        println("An unexpected error occurred: $e")
                    }
                    attempt++
                    if (attempt < maxAttempts) {
                        delay(1000) // Delay 1 seconds before the next attempt
                    }
                }
                if (attempt == maxAttempts) {
                    println("Max retry attempts reached.")
                    chatIdState.value = 0
                }
            } else {
                println("chat id was found in db")
                chatIdState.value = 3
                loadChatIdFromDb()
            }
            if (chatIdState.value == 3) {
                if (noMessages()) {
                    println("no local messages were found")
                    getMessagesList()
                } else {
                    initializeMessagesList()
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}

