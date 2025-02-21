package com.fallenstedt.mp3_player.services

import android.content.Intent
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService: MediaSessionService() {
  private var mediaSession: MediaSession? = null


  override fun onCreate() {
    super.onCreate()
    val player = ExoPlayer.Builder(this).build()
    mediaSession = MediaSession.Builder(this, player).build()
    Log.d("Mp3PlayerApp", "Created PlaybackService")
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    val player = mediaSession?.player!!
    if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
      // Stop the service if not playing, continue playing in the background otherwise
      stopSelf()
    }
  }

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
    return mediaSession
  }

  override fun onDestroy() {
    mediaSession?.run {
      player.release()
      release()
      mediaSession = null
    }
    super.onDestroy()
  }

}