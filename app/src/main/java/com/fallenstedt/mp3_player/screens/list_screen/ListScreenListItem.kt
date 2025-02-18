package com.fallenstedt.mp3_player.screens.list_screen

import androidx.compose.ui.graphics.vector.ImageVector

data class ListScreenListItem(
  val text: String,
  val onClick: (id: String) -> Unit,
  val icon: ImageVector? = null
)