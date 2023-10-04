package com.nurtore.imam_ai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBar(
    val route: String,
    val title: String,
    val icon: ImageVector
//    val selectedIcon: ImageVector,
//    val unselectedIcon: ImageVector
) {
    object Chat : BottomBar(
        route = "chat",
        title = "Chat",
        icon = Icons.Default.Email
//        selectedIcon = Icons.Filled.Email,
//        unselectedIcon = Icons.Outlined.Email
    )
    object Home : BottomBar(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
//        selectedIcon = Icons.Filled.Home,
//        unselectedIcon = Icons.Outlined.Home
    )
    object Kibla : BottomBar(
        route = "kibla",
        title = "Kibla",
        icon = Icons.Default.LocationOn
//        selectedIcon = Icons.Filled.LocationOn,
//        unselectedIcon = Icons.Outlined.LocationOn
    )
}
