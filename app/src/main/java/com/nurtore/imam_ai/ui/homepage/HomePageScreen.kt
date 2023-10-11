package com.nurtore.imam_ai.ui.homepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.nurtore.imam_ai.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState


//Welcome! I am your personal Imam. I can answer any questions about Islam, Quran, Hadith, Islamic culture and history. What question interests you today?
//Ask your question!
//Personal Imam
@Composable
fun HomePageScreen(navController: NavHostController, homePageViewModel: HomePageViewModel) {

    val locationData by homePageViewModel.locationData.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Personal Imam",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Image(painter = painterResource(id = R.drawable.imam_love), contentDescription = "imam love")
            Text(text = "Welcome! I am your personal Imam. I can answer any questions about Islam, Quran, Hadith, Islamic culture and history. What question interests you today?")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
                         navController.navigate("chat"){
                             popUpTo(navController.graph.findStartDestination().id)
                             launchSingleTop = true
                         }
        },
            modifier = Modifier.fillMaxWidth()) {
            Text(text = "Ask your question!")
        }

        Spacer(modifier = Modifier.height(12.dp))

        locationData?.let {
            Text("City: ${it.city}, Country: ${it.country}")
        }

    }
}