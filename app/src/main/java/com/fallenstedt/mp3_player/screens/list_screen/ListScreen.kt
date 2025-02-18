package com.fallenstedt.mp3_player.screens.list_screen


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ListScreen(
  modifier:Modifier = Modifier,

  fetchItems: () -> List<ListScreenListItem>,
) {
  val items = fetchItems()
  LazyColumn(modifier = modifier) {
    items(items, key = { item -> item.text }) {
      item -> Item(text = item.text, onClick = item.onClick, icon = item.icon)
    }
  }
}
