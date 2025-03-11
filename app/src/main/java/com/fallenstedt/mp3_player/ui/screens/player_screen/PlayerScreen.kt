package com.fallenstedt.mp3_player.ui.screens.player_screen

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel
import androidx.annotation.OptIn
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.media3.common.util.UnstableApi
import com.fallenstedt.mp3_player.R
import com.fallenstedt.mp3_player.ui.components.LoadingScreen
import com.fallenstedt.mp3_player.ui.components.MySlider
import com.fallenstedt.mp3_player.ui.components.PlayPauseButton
import com.fallenstedt.mp3_player.ui.components.list.ListScreen

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
  modifier: Modifier = Modifier,
  mediaControllerViewModel: MediaControllerViewModel
) {
  val context = LocalContext.current
  val mediaController = mediaControllerViewModel.mediaController
  val playerUiState by mediaControllerViewModel.uiState.collectAsState()

  val defaultArtworkDrawable: Drawable? = remember {
    ResourcesCompat.getDrawable(context.resources, R.drawable.default_artwork, null)
  }

  if (playerUiState.isLoadingPlaylist) {
    LoadingScreen()
    return
  }

  Column(modifier = modifier) {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
        .fillMaxWidth()
    ) {
        Text(
          text = playerUiState.currentAlbum,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier
            .weight(1f)
            .basicMarquee()
        )
      PlayPauseButton(
        containerColor = MaterialTheme.colorScheme.onSurface,
        contentColor = MaterialTheme.colorScheme.surface,
        onPlay = { mediaController.play() },
        onPause = { mediaController.pause() },
        isPlaying = mediaControllerViewModel.isPlaying,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
      )
    }
    Row() {
      MySlider(mediaControllerViewModel)
    }
    ListScreen { playerUiState.playlist }
  }

}