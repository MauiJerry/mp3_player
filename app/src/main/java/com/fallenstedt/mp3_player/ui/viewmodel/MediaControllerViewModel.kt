package com.fallenstedt.mp3_player.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController


class MediaControllerViewModel : ViewModel() {
  private lateinit var _mediaController: MediaController
  private val mediaController: MediaController
    get() = _mediaController


  fun setMediaController(mediaController: MediaController) {
    _mediaController = mediaController
  }

  fun startPlaylist(mediaItems: List<MediaItem>, startIndex: Int = 0) {
    Log.d("Mp3PlayerApp.MediaControllerVM", "starting playlist at index $startIndex with ${mediaItems.count()} items")
    mediaController.clearMediaItems()
    mediaController.addMediaItems(mediaItems)
    mediaController.prepare()
    mediaController.seekToDefaultPosition(startIndex)
    mediaController.play()
  }
}