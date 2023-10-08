package com.nurtore.imam_ai.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nurtore.imam_ai.model.MessageWithImam
import com.nurtore.imam_ai.ui.chat.ImamChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    imamChatViewModel: ImamChatViewModel,
    compassDegree: Float
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBarContents(navHostController = navController)
        }
    ) {
        BottomNavGraph(
            navController = navController,
            imamChatViewModel = imamChatViewModel,
            compassDegree = compassDegree
            )
    }
}

@Composable
fun BottomBarContents(navHostController: NavHostController) {
    val screens = listOf(
        BottomBar.Home,
        BottomBar.Chat,
        BottomBar.Kibla
    )
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier.height(40.dp)
    ) {
        screens.forEach {screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    it.route == screen.route
                } == true,
                onClick = {
                    navHostController.navigate(screen.route) {
                        popUpTo(navHostController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                icon = {
                       Image(imageVector = screen.icon, contentDescription = "icon")
                },
//                label = {
//                    Text(text = screen.title)
//                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color.Green,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray
                )
            )
        }
    }
}

