package com.fallenstedt.mp3_player.screens.file_screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fallenstedt.mp3_player.screens.list_screen.ListScreen
import com.fallenstedt.mp3_player.screens.list_screen.ListScreenListItem
import com.fallenstedt.mp3_player.services.FileService
import java.io.File

@Composable
fun FileScreen(query: String? = null, onItemClick: (id: String) -> Unit) {
  val fileService = remember { FileService() }

  Log.d("Mp3PlayerApp", "query: $query")

  val items =  fileService.getMusicFiles(
    directory = query?.let { File(it) } ?: fileService.getRootMusicDirectory()
  ).map { file ->
    ListScreenListItem(
      text = file.name,
      onClick = {
        if (file.isDirectory()) {
          onItemClick(file.path)
        } else {
          Log.d("Mp3PlayerApp", "Clicked an audio file ${file.name}")
        }
      }
    )
  }

  ListScreen(
    title = query?.substringAfter("Music/", missingDelimiterValue = "Music")
  ) {
    items
  }
}