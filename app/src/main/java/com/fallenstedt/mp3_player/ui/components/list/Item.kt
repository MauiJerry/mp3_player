package com.fallenstedt.mp3_player.ui.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Item(
  text: String,
  onClick: (id: String) -> Unit,
  emphasize: Boolean? = false,
  icon: ImageVector? = null,
  subtext: String? = null,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
        .background(
          if (emphasize == true) MaterialTheme.colorScheme.primaryContainer else { Color.Unspecified })
        .clickable { onClick(text) }) {
      if (icon != null) {
        Icon(icon, contentDescription = text, modifier = Modifier.padding(start = 16.dp))
      }
      Column (modifier = Modifier.padding(12.dp)) {
        Text(text = text, fontWeight = FontWeight.Bold)
        if (subtext != null) {
          Text(text = subtext)
        }
      }
    }
}
