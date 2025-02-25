package com.fallenstedt.mp3_player.ui.components.list

import androidx.compose.ui.graphics.vector.ImageVector

data class ListScreenListItem(
  val key: String,
  val text: String,
  val onClick: (id: String) -> Unit,
  val icon: ImageVector? = null
)