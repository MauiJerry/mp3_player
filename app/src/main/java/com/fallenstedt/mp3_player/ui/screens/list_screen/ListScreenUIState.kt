package com.fallenstedt.mp3_player.ui.screens.list_screen

import com.fallenstedt.mp3_player.Mp3PlayerScreens

data class ListScreenUIState(
  val listItems: List<ListScreenListItem> = listOf()
)