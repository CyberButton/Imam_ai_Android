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
import com.nurtore.imam_ai.ui.ChatScreen
import com.nurtore.imam_ai.ui.MainActivityViewModel
import com.nurtore.imam_ai.ui.MainViewModelFactory
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
                val viewModelFactory = MainViewModelFactory(repo, db.dao, application)
                val viewmodel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
                val messagesList = viewmodel.messagesList
                val isOnline = viewmodel.isConnected

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(
                        onSendMessage = viewmodel::messageImam,
                        messageList = messagesList,
                        deleteMessages = viewmodel::deleteAllMessages,
                        getNewMessageId = viewmodel::getNewChatId,
                        isOnline = isOnline
                        )
                }
            }
        }
    }
}