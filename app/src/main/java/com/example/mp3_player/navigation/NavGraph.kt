package com.example.mp3_player.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mp3_player.screens.ListScreen
import com.example.mp3_player.screens.PlayerScreen

@Composable
fun AppNavigation() {
  val context = LocalContext.current
  val navController: NavHostController = rememberNavController() // Explicit type declaration
  NavHost(navController = navController, startDestination = "listScreen") {
    composable("listScreen") { ListScreen(navController, context) }
    composable("playerScreen") { PlayerScreen(navController) }
  }
}
