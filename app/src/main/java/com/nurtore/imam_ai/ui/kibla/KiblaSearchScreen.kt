package com.nurtore.imam_ai.ui.kibla

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.nurtore.imam_ai.R


@Composable
fun KiblaSearchScreen(targetRotation: Float) {
    var lastRotation by remember { mutableStateOf(targetRotation) }
    val diff = shortestAngleDifference(lastRotation, targetRotation)
    val animatedRotation by animateFloatAsState(
        targetValue = lastRotation + diff,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing), label = "compass"
    )


    // Update lastRotation after the animation
    LaunchedEffect(animatedRotation) {
        lastRotation = animatedRotation
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.compass),
            contentDescription = "Compass",
            modifier = Modifier.rotate(animatedRotation)
        )
    }
}

private fun shortestAngleDifference(current: Float, target: Float): Float {
    val difference = ((target - current) + 180f) % 360f - 180f
    return if (difference <= -180f) difference + 360f else difference
}



