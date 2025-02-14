package com.fallenstedt.mp3_player.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fallenstedt.mp3_player.screens.ListScreen
import com.fallenstedt.mp3_player.screens.PlayerScreen

@Composable
fun AppNavigation() {
  val context = LocalContext.current
  val navController: NavHostController = rememberNavController()
  NavHost(navController = navController, startDestination = "listScreen") {
    composable("listScreen") { ListScreen(navController, context) }
    composable(route = "playerScreen?filePath={filePath}",
      arguments= listOf(
        navArgument("filePath") {
          type = NavType.StringType
          nullable = false
        }
      )) { navBackStackEntry ->
        val filePath = navBackStackEntry.arguments?.getString("filePath")
        PlayerScreen(navController, context, filePath)
    }
  }
}
