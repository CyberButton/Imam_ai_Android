package com.nurtore.imam_ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nurtore.imam_ai.ui.chat.ImamChatScreen
import com.nurtore.imam_ai.ui.chat.ImamChatViewModel
import com.nurtore.imam_ai.ui.homepage.HomePageScreen
import com.nurtore.imam_ai.ui.kibla.KiblaSearchScreen


@Composable
fun BottomNavGraph(
    navController: NavHostController,
    imamChatViewModel: ImamChatViewModel,
    compassDegree: Float
    ) {
    NavHost(
        navController = navController,
        startDestination = BottomBar.Home.route
    ) {
        composable(route = BottomBar.Home.route) {
            HomePageScreen()
        }
        composable(route = BottomBar.Chat.route) {
            ImamChatScreen(
                imamChatViewModel
            )
        }
        composable(route = BottomBar.Kibla.route) {
            KiblaSearchScreen(
                compassDegree
            )
        }
    }
}