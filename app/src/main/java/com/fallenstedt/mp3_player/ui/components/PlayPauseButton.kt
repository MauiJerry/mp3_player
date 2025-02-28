package com.fallenstedt.mp3_player.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.fallenstedt.mp3_player.R

@Composable
fun PlayPauseButton(
  isPlaying: Boolean,
  onPlay: () -> Unit,
  onPause: () -> Unit,
  containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
  contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
  elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation()
  ) {
  FloatingActionButton(
    containerColor = containerColor,
    contentColor = contentColor,
    elevation = elevation,
    onClick = { if (isPlaying) onPause() else { onPlay() } },
  ) {
    if (isPlaying) {
      Icon(Icons.Filled.Pause, stringResource(R.string.pause))
    } else {
      Icon(Icons.Filled.PlayArrow, stringResource(R.string.play))
    }
  }
}