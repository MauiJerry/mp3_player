package com.fallenstedt.mp3_player.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fallenstedt.mp3_player.Mp3PlayerScreens
import com.fallenstedt.mp3_player.R
import com.fallenstedt.mp3_player.ui.screens.file_screen.FileScreen
import com.fallenstedt.mp3_player.ui.screens.list_screen.ListScreen
import com.fallenstedt.mp3_player.ui.screens.list_screen.ListScreenListItem
import com.fallenstedt.mp3_player.ui.screens.list_screen.ListScreenViewModel
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mp3PlayerAppBar(
  currentScreen: Mp3PlayerScreens,
  canNavigateBack: Boolean,
  navigateUp: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    title = { Text(stringResource(currentScreen.title)) },
    colors = TopAppBarDefaults.mediumTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    modifier = modifier,
    navigationIcon = {
      if (canNavigateBack) {
        IconButton(onClick = navigateUp) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back_button)
          )
        }
      }
    }
  )
}

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

  Scaffold(
    topBar = {
      Mp3PlayerAppBar(
        currentScreen = currentScreen,
        canNavigateBack = navController.previousBackStackEntry != null,
        navigateUp = { navController.navigateUp() }
      )
    }
  ) { innerPadding ->

    NavHost(
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
        val query = backStackEntry.arguments?.getString("query")
        FileScreen(
          query = query,
          loadSongs = { files ->
            files
              .map { MediaItem.fromUri(it.path) }
              .forEach{ mediaControllerViewModel.addMediaItem(it) }
            mediaControllerViewModel.prepare()
            mediaControllerViewModel.play()
          },
          onItemClick = { navController.navigate("${Mp3PlayerScreens.Files.name}?query=$it") }
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
    }
  }
}

private fun getCurrentScreen(currentRoute: String): Mp3PlayerScreens {
  val currentScreen = when {
    currentRoute.startsWith(Mp3PlayerScreens.Files.name) -> Mp3PlayerScreens.Files
    currentRoute.startsWith(Mp3PlayerScreens.Albums.name) -> Mp3PlayerScreens.Albums
    currentRoute.startsWith(Mp3PlayerScreens.Artists.name) -> Mp3PlayerScreens.Artists
    currentRoute.startsWith(Mp3PlayerScreens.Songs.name) -> Mp3PlayerScreens.Songs
    currentRoute == Mp3PlayerScreens.Start.name -> Mp3PlayerScreens.Start
    else -> Mp3PlayerScreens.Start
  }
  return currentScreen
}