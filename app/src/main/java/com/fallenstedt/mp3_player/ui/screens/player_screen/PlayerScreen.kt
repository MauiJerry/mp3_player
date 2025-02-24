package com.fallenstedt.mp3_player.ui.screens.player_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.ui.PlayerView
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerControlView

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
  modifier: Modifier = Modifier,
  mediaControllerViewModel: MediaControllerViewModel
) {
  val context = LocalContext.current
  val mediaController = mediaControllerViewModel.mediaController

  Column (modifier = modifier){
    AndroidView(
      modifier = Modifier.fillMaxHeight(0.5f),
      factory = {
        PlayerView(context).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
//          setBackgroundColor(Color.Black.toArgb())
//          setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
//          player = mediaController
//          useController = false
          useController = true
          controllerAutoShow = true
          setBackgroundColor(Color.Black.toArgb())
          setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
          setControllerShowTimeoutMs(0)
          showController()
        }
      },
      update = { playerView ->
        playerView.player = mediaController
      }
    )
    AndroidView(
      factory = {
        PlayerControlView(context).apply {
          player = mediaController
          showTimeoutMs = 0
          setShowNextButton(true)
          setShowPreviousButton(true)
          setBackgroundColor(Color.Black.toArgb())
        }
      },
      modifier = Modifier
    )
  }

}