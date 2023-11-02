package com.nurtore.imam_ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.nurtore.imam_ai.utils.permissions.LocationPermissionTextProvider
import com.nurtore.imam_ai.utils.permissions.PermissionsViewmodel
import com.nurtore.imam_ai.utils.permissions.PermissionDialog
import com.nurtore.imam_ai.utils.permissions.BatteryExceptionPermissionTextProvider
import com.nurtore.imam_ai.utils.permissions.NotificationPermissionTextProvider
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val permissionsToRequest = arrayOf<String>()

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

                val permissionsViewmodel = viewModel<PermissionsViewmodel>()
                val dialogQueue = permissionsViewmodel.visiblePermissionDialogQueue


                val repo = Repo()
                val viewModelFactoryImamChat =
                    ImamChatViewModelFactory(repo, messagesDb.dao, application)
                val viewmodelImamChat =
                    ViewModelProvider(
                        this,
                        viewModelFactoryImamChat
                    ).get(ImamChatViewModel::class.java)
//                runBlocking {
//                    db.dao.deleteAllMessages()
//                }

                val factory = KiblaSearchViewModelFactory(this)
                val kiblaSearchViewModel =
                    ViewModelProvider(this, factory).get(KiblaSearchViewModel::class.java)

                val targetRotation by kiblaSearchViewModel.rotationDegree.observeAsState(initial = 0f)

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                val geocoder = Geocoder(this, Locale.getDefault())
                val calendar = Calendar.getInstance()
                val homePageViewModelFactory = HomePageViewModelFactory(
                    fusedLocationClient,
                    geocoder,
                    repo,
                    calendar,
                    scheduleDb.dao,
                    prefs
                )
                val homePageViewModel: HomePageViewModel = ViewModelProvider(
                    this,
                    homePageViewModelFactory
                ).get(HomePageViewModel::class.java)

                val locationPermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        permissionsViewmodel.onPermissionResult(
                            permission = Manifest.permission.ACCESS_FINE_LOCATION,
                            isGranted = isGranted
                        )
                        kiblaSearchViewModel.initializeCompass(this.applicationContext)
                    }
                )

                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        permissionsToRequest.forEach { permission ->
                            permissionsViewmodel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
                            )
                        }
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(prefs.first_launch) {
                        InitialDialog(onOkClick = {
                            locationPermissionResultLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            prefs.first_launch = false
                        })
                    }
                    dialogQueue
                        .reversed()
                        .forEach { permission ->
                            PermissionDialog(
                                permissionTextProvider = when (permission) {
                                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                                        LocationPermissionTextProvider()
                                    }

                                    Manifest.permission.POST_NOTIFICATIONS -> {
                                        NotificationPermissionTextProvider()
                                    }

                                    "NURTORE_BATT" -> {
                                        BatteryExceptionPermissionTextProvider()
                                    }

                                    else -> return@forEach
                                },
                                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                    permission
                                ),
                                onDismiss = permissionsViewmodel::dismissDialog,
                                onOkClick = {
                                    permissionsViewmodel.dismissDialog()
                                    multiplePermissionResultLauncher.launch(
                                        arrayOf(permission)
                                    )
                                },
                                onGoToAppSettingsClick = when (permission) {
                                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                                        {openLocationSettings(this)}
                                    }
                                    Manifest.permission.POST_NOTIFICATIONS -> {
                                        { openNotificationsSettings(this) }
                                    }
                                    "NURTORE_BATT" -> {
                                        { openBatterySaverSettings(this) }
                                    }
                                    else -> return@forEach
                                }
                            )
                        }
                    MainScreen(
                        imamChatViewModel = viewmodelImamChat,
                        homePageViewModel = homePageViewModel,
                        kiblaSearchViewModel = kiblaSearchViewModel
                    ) {
                        locationPermissionResultLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                }
            }
        }
    }


//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            KiblaSearchViewModel.LOCATION_PERMISSION_REQUEST_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted. Now you can access the location.
//                    // TODO: Refresh or update location-based functionality.
//
//                } else {
//                    // Permission denied. Inform the user that the feature requires this permission.
//                    Toast.makeText(this, "Location permission is required for this feature.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    //    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            PERMISSION_REQUEST_CODE -> {
//                // If request is cancelled, the result arrays are empty.
//                if ((grantResults.isNotEmpty() &&
//                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // Permission is granted. Continue the action or workflow
//                    // in your app.
//                } else {
//                    // Explain to the user that the feature is unavailable because
//                    // the feature requires a permission that the user has denied.
//                    // At the same time, respect the user's decision. Don't link to
//                    // system settings in an effort to convince the user to change
//                    // their decision.
//                }
//                return
//            }
//
//            // Add other 'when' lines to check for other
//            // permissions this app might request.
//            else -> {
//                // Ignore all other requests.
//            }
//        }
//    }
}
//works
fun openLocationSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", activity.packageName, null))
    activity.startActivity(intent)
}

fun openBatterySaverSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            //Uri.fromParts("package", activity.packageName, null)
    intent.data = Uri.parse("package:" + activity.packageName)
//    intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
    activity.startActivity(intent)
}

//works
fun openNotificationsSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
    activity.startActivity(intent)
}



    @Composable
    fun InitialDialog(
        onOkClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        AlertDialog(
            onDismissRequest = {

            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider()
                    Text(
                        "OK",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOkClick()
                            }
                            .padding(16.dp)
                    )
                }
            },
            title = {
                Text(text = "Permissions required")
            },
            text = {
                Text(
                    "Imam AI uses Location services to accurately detect your city"
                )
            },
            modifier = modifier
        )

    }
