package com.nurtore.imam_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nurtore.imam_ai.repo.Repo
import com.nurtore.imam_ai.ui.theme.Imam_aiTheme

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
                    Button(onClick = {viewmodel.getMessagesList()}) {
                        Column {
                            messagesList.forEach {
                                Greeting(name = it)
                            }
                        }
                    }
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

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Imam_aiTheme {
//        Greeting("Android")
//    }
//}