package com.fallenstedt.mp3_player.ui.screens.list_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class ListScreenListItem(
  val text: String,
  val onClick: (id: String) -> Unit,
  val icon: ImageVector? = null
)