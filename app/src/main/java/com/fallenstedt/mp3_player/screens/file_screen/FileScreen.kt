package com.fallenstedt.mp3_player.screens.file_screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fallenstedt.mp3_player.screens.list_screen.ListScreen
import com.fallenstedt.mp3_player.screens.list_screen.ListScreenListItem
import com.fallenstedt.mp3_player.services.FileService

@Composable
fun FileScreen(query: String? = null, onItemClick: (id: String) -> Unit) {
  val fileService = remember { FileService() }

  val files = fileService.getMusicFiles(fileService.getRootMusicDirectory())
  val items = files.map { file ->
    ListScreenListItem(text = file.name, onClick = {
    Log.d("Mp3PlayerApp", "File clicked: ${file.path}")
    onItemClick(file.path)
  }) }
  ListScreen {
    items
  }
}