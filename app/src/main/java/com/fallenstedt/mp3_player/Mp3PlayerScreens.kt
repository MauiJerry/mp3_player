package com.fallenstedt.mp3_player

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector


enum class Mp3PlayerScreens(@StringRes val title: Int, val icon: ImageVector) {
  Start(title = R.string.app_name, icon = Icons.Filled.Home),
  Files(title = R.string.files, icon = Icons.Filled.Folder),
  Albums(title = R.string.albums, icon = Icons.Filled.Album),
  Artists(title = R.string.artists, icon = Icons.Filled.Person),
  Songs(title = R.string.songs, icon = Icons.Filled.MusicNote),
  Player(title = R.string.player, icon = Icons.Filled.MusicNote),

}

