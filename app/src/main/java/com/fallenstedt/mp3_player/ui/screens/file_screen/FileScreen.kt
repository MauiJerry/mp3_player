package com.fallenstedt.mp3_player.ui.screens.file_screen

import android.content.Intent
import android.drm.DrmStore.Playback
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fallenstedt.mp3_player.ui.screens.list_screen.ListScreen
import com.fallenstedt.mp3_player.ui.screens.list_screen.ListScreenListItem
import com.fallenstedt.mp3_player.services.FileService
import com.fallenstedt.mp3_player.services.PlaybackService
import java.io.File

@Composable
fun FileScreen(
  query: String? = null,
  onItemClick: (id: String) -> Unit,
  loadSongs: (directory: List<File>, index: Int) -> Unit
) {
  val fileService = remember { FileService() }

  Log.d("Mp3PlayerApp", "query: $query")

  val directory = query?.let { File(it) } ?: fileService.getRootMusicDirectory()
  val files = fileService.getMusicFiles(
    directory = directory
  )
  val items =  files.map { file ->
    ListScreenListItem(
      text = file.name,
      onClick = {
        if (file.isDirectory()) {
          onItemClick(file.path)
        } else {
          loadSongs(fileService.getMusicFiles(directory), files.indexOf(file))
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