package com.fallenstedt.mp3_player.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fallenstedt.mp3_player.Mp3PlayerScreens
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel

@Composable
fun Mp3PlayerBottomAppBar(
  mediaControllerViewModel: MediaControllerViewModel,
  currentScreen: Mp3PlayerScreens, modifier: Modifier = Modifier,
  onNavigateToPlayer: () -> Unit
) {
  val mediaController = mediaControllerViewModel.mediaController
  val playerUiState by mediaControllerViewModel.uiState.collectAsState()

  AnimatedVisibility(visible = mediaControllerViewModel.mediaController.mediaItemCount > 0 && currentScreen != Mp3PlayerScreens.Player) {
    BottomAppBar(
      modifier = modifier.clickable { onNavigateToPlayer() },
      actions = {
        Column(modifier = Modifier.padding(start = 16.dp)) {
          Text(
            text = playerUiState.currentTitle,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
          )
          Text(text = playerUiState.currentArtist)
        }
      },
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
      floatingActionButton = {
        PlayPauseButton(
          containerColor = MaterialTheme.colorScheme.onSurface,
          contentColor = MaterialTheme.colorScheme.surface,
          onPlay = { mediaController.play() },
          onPause = { mediaController.pause() },
          isPlaying = mediaControllerViewModel.isPlaying,
          elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        )
      }
    )
  }
}
