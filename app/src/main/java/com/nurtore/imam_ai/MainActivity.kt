package com.nurtore.imam_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.DbMessageWithImamDatabase
import com.nurtore.imam_ai.ui.chat.ImamChatViewModel
import com.nurtore.imam_ai.ui.chat.ImamChatViewModelFactory
import com.nurtore.imam_ai.ui.navigation.MainScreen
import com.nurtore.imam_ai.ui.theme.Imam_aiTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            DbMessageWithImamDatabase::class.java,
            "messages.db"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Imam_aiTheme {
                // A surface container using the 'background' color from the theme

                val repo = Repo()
                val viewModelFactoryImamChat = ImamChatViewModelFactory(repo, db.dao, application)
                val viewmodelImamChat =
                    ViewModelProvider(this, viewModelFactoryImamChat).get(ImamChatViewModel::class.java)
//                runBlocking {
//                    db.dao.deleteAllMessages()
//                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    ImamChatScreen(
//                        onSendMessage = viewmodel::messageImam,
//                        messageList = messagesList,
//                        deleteMessages = viewmodel::deleteAllMessages,
//                        getNewMessageId = viewmodel::getNewChatId,
//                        isOnline = isOnline,
//                        uiState = uiState,
//                        scrollState = scrollState,
//                        typing = typing
//                    )
//                }
                    // ?
                    MainScreen(
                        imamChatViewModel = viewmodelImamChat
                    )
                }
            }
        }
    }
}