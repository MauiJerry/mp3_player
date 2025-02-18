package com.fallenstedt.mp3_player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fallenstedt.mp3_player.screens.list_screen.ListScreen
import com.fallenstedt.mp3_player.screens.list_screen.ListScreenListItem


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
fun Mp3PlayerApp(navController: NavHostController = rememberNavController()
) {
  // Get current back stack entry
  val backStackEntry by navController.currentBackStackEntryAsState()
  // Get the name of the current screen
  val currentScreen = Mp3PlayerScreens.valueOf(
    backStackEntry?.destination?.route ?: Mp3PlayerScreens.Start.name
  )

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
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)
    ) {
      composable(route = Mp3PlayerScreens.Start.name) {
        ListScreen {
          listOf(
            Mp3PlayerScreens.Files,
            Mp3PlayerScreens.Albums,
            Mp3PlayerScreens.Artists,
            Mp3PlayerScreens.Songs).map { item ->
              ListScreenListItem(
                text = stringResource(item.title),
                onClick = { navController.navigate(item.name) },
                icon = item.icon
            )}.sortedBy { it.text }
        }
      }
      composable(route = Mp3PlayerScreens.Files.name) {
       ListScreen {
         listOf()
       }
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