package com.fallenstedt.mp3_player.ui.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Item(text: String, onClick: (id: String) -> Unit, icon: ImageVector? = null) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().clickable { onClick(text) }) {
      if (icon != null) {
        Icon(icon, contentDescription = text, modifier = Modifier.padding(start = 16.dp))
      }
      Text(text = text, modifier = Modifier.padding(16.dp))
    }
}
