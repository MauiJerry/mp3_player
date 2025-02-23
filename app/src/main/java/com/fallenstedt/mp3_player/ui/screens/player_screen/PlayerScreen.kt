package com.fallenstedt.mp3_player.ui.screens.player_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.ui.PlayerView
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView.ARTWORK_DISPLAY_MODE_FILL
import androidx.media3.ui.PlayerView.ArtworkDisplayMode


@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
  modifier: Modifier = Modifier,
  mediaControllerViewModel: MediaControllerViewModel
) {
  val context = LocalContext.current
  val mediaController = mediaControllerViewModel.mediaController

  DisposableEffect(key1 = mediaController) {
    onDispose {
      Log.d("Mp3PlayerApp.PlayerScreen", "onDispose")
//      mediaController?.release()
    }
  }

  AndroidView(
    modifier = modifier,
    factory = {
      PlayerView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
        useController = true

      }
    },
    update = { playerView ->
      playerView.player = mediaController
    }
  )
}