package com.fallenstedt.mp3_player.ui.viewmodel

import com.fallenstedt.mp3_player.ui.components.list.ListScreenListItem

data class MediaUIState(
  val isLoadingPlaylist: Boolean = false,
  val currentTitle: String = "",
  val currentArtist: String = "",
  val currentAlbum: String = "",
  val playlist: List<ListScreenListItem> = listOf()
)
