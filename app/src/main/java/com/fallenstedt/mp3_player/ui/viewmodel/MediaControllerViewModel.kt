package com.fallenstedt.mp3_player.ui.viewmodel

import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class MediaControllerViewModel : ViewModel() {
  private var _mediaController: MediaController? = null
  val mediaController: MediaController?
    get() = _mediaController

  private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
  val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

  fun setMediaController(mediaController: MediaController) {
    _mediaController = mediaController
  }

  @OptIn(UnstableApi::class)
  fun addMediaItem(mediaItem: MediaItem) {
    Log.d("MediaControllerVM", "addMediaItem: $mediaItem")
    _mediaItems.value += mediaItem
    mediaController?.addMediaItem(mediaItem)
  }

  fun prepare() {
    Log.d("MediaControllerVM", "prepare")
    mediaController?.prepare()
  }

  fun play() {
    if (mediaController != null) {
      Log.d("MediaControllerVM", "play")
      mediaController?.play()
    }

  }

  fun pause() {
    Log.d("MediaControllerVM", "pause")
    mediaController?.pause()
  }

  fun stop() {
    Log.d("MediaControllerVM", "stop")
    mediaController?.stop()
  }

  fun next() {
    Log.d("MediaControllerVM", "next")
    mediaController?.seekToNextMediaItem()
  }

  fun previous() {
    Log.d("MediaControllerVM", "previous")
    mediaController?.seekToPreviousMediaItem()
  }
}