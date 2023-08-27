package com.nurtore.imam_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nurtore.imam_ai.repo.Repo
import com.nurtore.imam_ai.ui.theme.Imam_aiTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.nurtore.imam_ai.model.MessageWithImam

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Imam_aiTheme {
                // A surface container using the 'background' color from the theme

                val repo = Repo()
                val viewModelFactory = MainViewModelFactory(repo)
                val viewmodel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

                val messagesList = viewmodel.messagesList
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Button(onClick = {viewmodel.getMessagesList()}) {
//                        Column {
//                            messagesList.forEach {
//                                Greeting(name = it)
//                            }
//                        }
//                    }
                    ChatScreen(onSendMessage = viewmodel::messageImam, messageList = messagesList)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messageList: List<MessageWithImam>,
    onSendMessage: (String) -> Unit
) {
    val message = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Messages",
                modifier = Modifier.weight(1f)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messageList) { message ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChatMessage(
                        message = message,
                        modifier = Modifier
                            .align(
                                if(message.sentByImam()) Alignment.Start else Alignment.End
                            )
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Message")
                }
            )
            IconButton(onClick = {
                onSendMessage(message.value)
                message.value = ""
                keyboardController?.hide()
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send message"
                )
            }
        }
    }
}

@Composable
fun ChatMessage(
    message: MessageWithImam,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.sentByImam()) 0.dp else 15.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = if (message.sentByImam()) 15.dp else 0.dp
                )
            )
            .background(
                if (message.sentByImam()) Color.LightGray else Color.Cyan
            )
            .padding(16.dp)
    ) {
        Text(
            text = message.content,
            color = Color.Black,
            modifier = Modifier.widthIn(max = 250.dp)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Imam_aiTheme {
//        Greeting("Android")
//    }
//}