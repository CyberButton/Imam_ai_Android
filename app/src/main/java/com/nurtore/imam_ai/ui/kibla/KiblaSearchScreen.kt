package com.nurtore.imam_ai.ui.kibla

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import com.nurtore.imam_ai.R
import com.nurtore.imam_ai.ui.chat.LoadingAnimation


@Composable
fun KiblaSearchScreen( askForLocationPermission: () -> Unit,
                       kiblaSearchViewModel: KiblaSearchViewModel
                       ) {
    val targetRotation by kiblaSearchViewModel.rotationDegree.observeAsState(initial = 0f)
    val enabledLocation by kiblaSearchViewModel.hasLocation.observeAsState(null)
    val loadingSate by kiblaSearchViewModel.isLoading.observeAsState(null)

    var lastRotation by remember { mutableFloatStateOf(targetRotation) }
    val diff = shortestAngleDifference(lastRotation, targetRotation)
    val animatedRotation by animateFloatAsState(
        targetValue = lastRotation + diff,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing), label = "compass"
    )

    val context = LocalContext.current
    // Update lastRotation after the animation
    LaunchedEffect(animatedRotation) {
        lastRotation = animatedRotation
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        loadingSate?.let {
            if(it) {
                LoadingAnimation()
            }
        }

        if(ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enabledLocation?.let {
                if(it) {
                    Image(
                        painter = painterResource(id = R.drawable.compass),
                        contentDescription = "Compass",
                        modifier = Modifier.rotate(animatedRotation)
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "This feature requires enabled location services, turn on location and try again.")
                        Button(onClick = { kiblaSearchViewModel.initializeCompass(context) }) {
                            Text(text = "Try again")
                        }
                    }
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

private fun shortestAngleDifference(current: Float, target: Float): Float {
    val difference = ((target - current) + 180f) % 360f - 180f
    return if (difference <= -180f) difference + 360f else difference
}


