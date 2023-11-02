package com.nurtore.imam_ai.ui.homepage

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.nurtore.imam_ai.R
import java.time.LocalTime


//Welcome! I am your personal Imam. I can answer any questions about Islam, Quran, Hadith, Islamic culture and history. What question interests you today?
//Ask your question!
//Personal Imam
@Composable
fun HomePageScreen(
    navController: NavHostController,
    homePageViewModel: HomePageViewModel,
    askForLocationPermission: () -> Unit
) {

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
            Text(text = "Welcome! I am your personal Imam. I can answer any questions about Islam, Quran, Hadith, Islamic culture and history. What question interests you today?",
                //fontSize = 12.sp,
                style = MaterialTheme.typography.bodyMedium)
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

        Spacer(modifier = Modifier.height(10.dp))

        locationData?.let {
            Row {
                Icon(imageVector = Icons.TwoTone.Place, contentDescription = "location icon")
                Text(" ${it.city}, ${it.country}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val context = LocalContext.current

//        Button(onClick = onclicktest) {
//            Text(text = "test")
//        }

        val prayerTimes by homePageViewModel.currentPrayerTimes.observeAsState(null)


        if(ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            prayerTimes?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Blue.copy(alpha = 0.1f)), // Adding background to the table
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Prayer",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Time",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    // Fajr
                    PrayerRow("Fajr", it.fajr, false)
                    // Sunrise
                    PrayerRow(prayerName = "Sunrise", prayerTime = it.sunrise, current = false)
                    // Dhuhr
                    PrayerRow("Dhuhr", it.dhuhr, false)
                    // Asr
                    PrayerRow("Asr", it.asr, false)
                    // Maghrib
                    PrayerRow("Maghrib", it.maghrib, false)
                    // Isha
                    PrayerRow("Isha", it.isha, false)
                    // add view by days of month for furtre updates
                    // << 21            22 nov              23>>
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "This feature requires location permission")
                Button(onClick =  askForLocationPermission) {
                    Text(text = "Grant Permission")
                }
            }
        }
    }
}

@Composable
fun PrayerRow(prayerName: String, prayerTime: String, current: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (current) Color.Green.copy(alpha = 0.2f) else Color.Transparent) // Highlighting the entire row
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = prayerName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = prayerTime,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
