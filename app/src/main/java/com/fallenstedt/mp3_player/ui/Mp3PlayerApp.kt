package com.fallenstedt.mp3_player.ui

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fallenstedt.mp3_player.Mp3PlayerScreens
import com.fallenstedt.mp3_player.ui.components.Mp3PlayerAppBar
import com.fallenstedt.mp3_player.ui.components.Mp3PlayerBottomAppBar
import com.fallenstedt.mp3_player.ui.screens.file_screen.FileScreen
import com.fallenstedt.mp3_player.ui.components.list.ListScreen
import com.fallenstedt.mp3_player.ui.components.list.ListScreenListItem
import com.fallenstedt.mp3_player.ui.screens.player_screen.PlayerScreen
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun Mp3PlayerApp(
  navController: NavHostController = rememberNavController(),
  mediaControllerViewModel: MediaControllerViewModel
) {
  // Get current back stack entry
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route ?: Mp3PlayerScreens.Start.name
  // Get the name of the current screen
  val currentScreen = getCurrentScreen(currentRoute)
  Log.d("Mp3PlayerApp", "Current screen: $currentScreen")
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  Scaffold(
    topBar = {
      Mp3PlayerAppBar(
        currentScreen = currentScreen,
        canNavigateBack = navController.previousBackStackEntry != null,
        navigateUp = { navController.navigateUp() }
      )
    },
    bottomBar = {
      Mp3PlayerBottomAppBar(
        mediaControllerViewModel = mediaControllerViewModel,
        currentScreen = currentScreen,
        onNavigateToPlayer = { navController.navigate(Mp3PlayerScreens.Player.name)}
      )
    }
  ) { innerPadding ->
    NavHost(
      enterTransition = { slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(
          150
        )
      ) },
      exitTransition = { slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.End, tween(
          150
        )
      ) },
      navController = navController,
      startDestination = Mp3PlayerScreens.Start.name,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      composable(route = Mp3PlayerScreens.Start.name) {
        ListScreen {
          listOf(
            Mp3PlayerScreens.Files,
            Mp3PlayerScreens.Albums,
            Mp3PlayerScreens.Artists,
            Mp3PlayerScreens.Songs
          ).map { item ->
            ListScreenListItem(
              key = item.name,
              text = item.name,
              onClick = { navController.navigate(it) },
              icon = item.icon
            )
          }.sortedBy { it.text }
        }
      }
      composable(
        route = "${Mp3PlayerScreens.Files.name}?query={query}",
        arguments = listOf(
          navArgument("query") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
         },
        )
      ) { backStackEntry ->
        val encodedQuery = backStackEntry.arguments?.getString("query")
        val decodedQuery = if (encodedQuery != null) {
          URLDecoder.decode(encodedQuery, StandardCharsets.UTF_8.toString())
        } else {
          null
        }
        FileScreen(
          query = decodedQuery,
          onSongSelect = { files, startIndex ->
            navController.navigate(Mp3PlayerScreens.Player.name)
            mediaControllerViewModel.startPlaylist(
              context,
              files,
              startIndex
            )
          },
          onItemClick = {
            navController.navigate("${Mp3PlayerScreens.Files.name}?query=${Uri.encode(it)}")
          }
        )
      }
      composable(route = Mp3PlayerScreens.Albums.name) {
        ListScreen {
          listOf()
        }
      }
      composable(route = Mp3PlayerScreens.Artists.name) {
        ListScreen {
          listOf()
        }
      }
      composable(route = Mp3PlayerScreens.Songs.name) {
        ListScreen {
          listOf()
        }
      }
      composable(route = Mp3PlayerScreens.Player.name) {
        PlayerScreen(mediaControllerViewModel = mediaControllerViewModel)
      }
    }
  }
}

private fun getCurrentScreen(currentRoute: String): Mp3PlayerScreens {
  val currentScreen = when {
    currentRoute.startsWith(Mp3PlayerScreens.Files.name) -> Mp3PlayerScreens.Files
    currentRoute.startsWith(Mp3PlayerScreens.Albums.name) -> Mp3PlayerScreens.Albums
    currentRoute.startsWith(Mp3PlayerScreens.Artists.name) -> Mp3PlayerScreens.Artists
    currentRoute.startsWith(Mp3PlayerScreens.Songs.name) -> Mp3PlayerScreens.Songs
    currentRoute.startsWith(Mp3PlayerScreens.Player.name) -> Mp3PlayerScreens.Player
    currentRoute == Mp3PlayerScreens.Start.name -> Mp3PlayerScreens.Start
    else -> Mp3PlayerScreens.Start
  }
  return currentScreen
}