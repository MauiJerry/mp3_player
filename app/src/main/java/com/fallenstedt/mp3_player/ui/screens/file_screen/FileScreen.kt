package com.fallenstedt.mp3_player.ui.screens.file_screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.fallenstedt.mp3_player.ui.components.list.ListScreen
import com.fallenstedt.mp3_player.ui.components.list.ListScreenListItem
import com.fallenstedt.mp3_player.services.FileService
import java.io.File
import java.util.UUID

@Composable
fun FileScreen(
  query: String? = null,
  onItemClick: (id: String) -> Unit,
  onSongSelect: (directory: List<File>, index: Int) -> Unit
) {
  val fileService = remember { FileService() }

  Log.d("Mp3PlayerApp", "query: $query")

  val directory = query?.let { File(it) } ?: fileService.getRootMusicDirectory()
  val files = fileService.getFiles(
    directory = directory
  )
  val items =  files.map { file ->
    ListScreenListItem(
      key = UUID.randomUUID().toString(),
      text = file.name,
      onClick = {
        if (file.isDirectory()) {
          onItemClick(file.path)
        } else {
          Log.d("Mp3PlayerApp", "Clicked an audio file ${file.name}")
          onSongSelect(fileService.getMusicFilesInDirectory(directory), files.indexOf(file))
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