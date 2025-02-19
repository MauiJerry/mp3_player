package com.fallenstedt.mp3_player.screens.file_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fallenstedt.mp3_player.screens.list_screen.ListScreen
import com.fallenstedt.mp3_player.screens.list_screen.ListScreenViewModel
import com.fallenstedt.mp3_player.services.FileService

@Composable
fun FileScreen(navController: NavHostController) {
  val fileService = remember { FileService() }
  val backStackEntry by navController.currentBackStackEntryAsState()
  val query = backStackEntry?.arguments?.getString("query")

  val files = fileService.getMusicFiles(fileService.getRootMusicDirectory())

  ListScreen {
    listOf()
  }
}