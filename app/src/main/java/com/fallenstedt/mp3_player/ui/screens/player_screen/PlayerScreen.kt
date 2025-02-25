package com.fallenstedt.mp3_player.ui.screens.player_screen

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.ui.PlayerView
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.media3.common.util.RepeatModeUtil
import androidx.media3.common.util.UnstableApi
import com.fallenstedt.mp3_player.R
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


  Column (modifier = modifier){
    AndroidView(
      modifier = Modifier.fillMaxHeight(0.5f),
      factory = {
        PlayerView(context).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )

          useController = true
          controllerAutoShow = true
          defaultArtwork = defaultArtworkDrawable
          setBackgroundColor(Color.Black.toArgb())
          setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
          setControllerShowTimeoutMs(0)
          setShowFastForwardButton(false)
          setShowRewindButton(false)
          setShowShuffleButton(true)
          setRepeatToggleModes(RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL)
          showController()
          setKeepContentOnPlayerReset(true)
        }
      },
      update = { playerView ->
        playerView.player = mediaController
      }
    )
    // TODO something is wrong with the key. Sometimes a key is null.
//    ListScreen { playerUiState.playlist }
  }

}