package com.nurtore.imam_ai

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import com.nurtore.imam_ai.api.Repo
import com.nurtore.imam_ai.db.messages.DbMessageWithImamDatabase
import com.nurtore.imam_ai.db.schedule.ScheduleDatabase
import com.nurtore.imam_ai.ui.chat.ImamChatViewModel
import com.nurtore.imam_ai.ui.chat.ImamChatViewModelFactory
import com.nurtore.imam_ai.ui.homepage.HomePageViewModel
import com.nurtore.imam_ai.ui.homepage.HomePageViewModelFactory
import com.nurtore.imam_ai.ui.kibla.KiblaSearchViewModel
import com.nurtore.imam_ai.ui.kibla.KiblaSearchViewModelFactory
import com.nurtore.imam_ai.ui.navigation.MainScreen
import com.nurtore.imam_ai.ui.theme.Imam_aiTheme
import com.nurtore.imam_ai.utils.SharedPrefs
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val messagesDb by lazy {
        DbMessageWithImamDatabase.getDatabase(this)
    }

    private val scheduleDb by lazy {
        ScheduleDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Imam_aiTheme {
                // A surface container using the 'background' color from the theme

                val prefs = SharedPrefs(this)

                val repo = Repo()
                val viewModelFactoryImamChat = ImamChatViewModelFactory(repo, messagesDb.dao, application)
                val viewmodelImamChat =
                    ViewModelProvider(this, viewModelFactoryImamChat).get(ImamChatViewModel::class.java)
//                runBlocking {
//                    db.dao.deleteAllMessages()
//                }

                val factory = KiblaSearchViewModelFactory(this)
                val viewModel = ViewModelProvider(this, factory).get(KiblaSearchViewModel::class.java)
                val rotation by viewModel.rotationFlow.collectAsState(initial = 0f)

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                val geocoder = Geocoder(this, Locale.getDefault())
                val calendar = Calendar.getInstance()
                val homePageViewModelFactory = HomePageViewModelFactory(fusedLocationClient, geocoder, repo, calendar, scheduleDb.dao, prefs)
                val homePageViewModel: HomePageViewModel = ViewModelProvider(this, homePageViewModelFactory).get(HomePageViewModel::class.java)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        imamChatViewModel = viewmodelImamChat,
                        compassDegree = rotation,
                        homePageViewModel = homePageViewModel
                    )
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            KiblaSearchViewModel.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted. Now you can access the location.
                    // TODO: Refresh or update location-based functionality.
                } else {
                    // Permission denied. Inform the user that the feature requires this permission.
                    Toast.makeText(this, "Location permission is required for this feature.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}