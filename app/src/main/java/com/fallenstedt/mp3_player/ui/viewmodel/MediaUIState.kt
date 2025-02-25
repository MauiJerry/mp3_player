package com.fallenstedt.mp3_player.ui.viewmodel

data class MediaUIState(
  val currentTitle: String = "",
  val currentArtist: String = "",
  val currentAlbum: String = "",
  val hasPlaylistLoaded: Boolean = false,
)
