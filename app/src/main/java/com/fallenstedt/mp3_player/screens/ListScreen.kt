package com.fallenstedt.mp3_player.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.fallenstedt.mp3_player.components.ListItem
import com.fallenstedt.mp3_player.services.MusicManager
import java.io.File

@Composable
fun ListScreen(navController: NavController, context: Context) {
  val musicManager = remember { MusicManager() }
  val currentDirectory = remember { mutableStateOf<File?>(null) }
  val musicFiles = remember { mutableStateOf(emptyList<File>())}

  fun loadFiles(directory: File) {
    musicFiles.value = musicManager.getMusicFiles(directory)
  }

  LaunchedEffect(currentDirectory.value) {
    val directory = currentDirectory.value ?: musicManager.getRootMusicDirectory()
    loadFiles(directory)
  }

  Column {
    if (currentDirectory.value != null && currentDirectory.value != musicManager.getRootMusicDirectory()) {
      Button(onClick = {
        currentDirectory.value = currentDirectory.value?.parentFile
      }) {
        Text("Back")
      }
    }
    LazyColumn {
      items(musicFiles.value) { file ->
        ListItem(file) { selectedFile ->
          if (selectedFile.isDirectory) {
            currentDirectory.value = selectedFile
          } else {
            navController.navigate("playerScreen?filePath=${selectedFile.path}")
          }
        }
      }
    }
  }

}

