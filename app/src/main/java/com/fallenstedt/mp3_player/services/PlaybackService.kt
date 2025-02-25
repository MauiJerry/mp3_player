package com.fallenstedt.mp3_player.services

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.bluetooth.BluetoothDevice
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.fallenstedt.mp3_player.receivers.BluetoothReceiver

class PlaybackService: MediaSessionService() {
  private lateinit var bluetoothReceiver: BluetoothReceiver
  private var mediaSession: MediaSession? = null


  override fun onCreate() {
    super.onCreate()
    val player = ExoPlayer.Builder(this).build().apply {
      setAudioAttributes(
        AudioAttributes.Builder()
          .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
          .setUsage(C.USAGE_MEDIA)
          .build(),
        true
      )
    }

    val sessionActivityPendingIntent =
      packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
        PendingIntent.getActivity(
          this,
          0,
          sessionIntent,
          PendingIntent.FLAG_IMMUTABLE
        )
      }

    mediaSession = MediaSession.Builder(this, player)
      .setSessionActivity(sessionActivityPendingIntent!!)
      .build()

//    bluetoothReceiver = BluetoothReceiver(player)
//    val filter = IntentFilter().apply {
//      addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
//    }
//    registerReceiver(bluetoothReceiver, filter)
    Log.d("Mp3PlayerApp.PlaybackService", "Created PlaybackService")
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    val player = mediaSession?.player!!
    if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
      // Stop the service if not playing, continue playing in the background otherwise
      Log.d("Mp3PlayerApp.PlaybackService", "service stopped")

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
      Log.d("Mp3PlayerApp.PlaybackService", "player released")

    }
    super.onDestroy()
  }

}