package com.fallenstedt.mp3_player.ui.components.list


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListScreen(
  modifier:Modifier = Modifier,
  title: String? = null,
  fetchItems: () -> List<ListScreenListItem>,
) {
  val items = fetchItems()

  Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
    if (title != null) {
      Row(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)) {
        Text(text = title,
          fontWeight = FontWeight.Bold,
          fontSize=18.sp,
          modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp))
      }
    }
    LazyColumn {
      items(items, key = { item -> item.key }) {
          item -> Item(
          text = item.text,
          subtext = item.subtext,
          onClick = item.onClick,
          icon = item.icon,
          emphasize = item.emphasize)
      }
    }
  }

}
