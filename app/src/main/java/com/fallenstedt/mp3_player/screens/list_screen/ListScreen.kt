package com.fallenstedt.mp3_player.screens.list_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.fallenstedt.mp3_player.components.Item
import androidx.compose.ui.Modifier


@Composable
fun ListScreen(
  modifier:Modifier = Modifier,
  fetchItems: @Composable () -> List<ListScreenListItem>,
) {

  Column(modifier = modifier) {
    fetchItems().forEach { item ->
      Item(text = item.text, onClick = item.onClick, icon = item.icon)
    }
  }
}