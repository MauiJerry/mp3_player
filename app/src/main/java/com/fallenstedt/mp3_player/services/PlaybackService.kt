package com.fallenstedt.mp3_player.services

import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.app.Notification
import androidx.core.app.NotificationCompat
import android.content.Context
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

private val NOTIFICATION_CHANNEL_ID = "Mp3PlayerChannel"
private val NOTIFICATION_ID = 101
class PlaybackService: MediaSessionService() {
  private lateinit var bluetoothReceiver: BluetoothReceiver
  private var mediaSession: MediaSession? = null
  private var isReceiverRegistered = false

  override fun onCreate() {
    super.onCreate()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        "MP3 Player Playback",
        NotificationManager.IMPORTANCE_LOW
      )
      val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)

    }
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

    bluetoothReceiver = BluetoothReceiver(player)

    registerReceiverIfNeeded()
    startForeground(NOTIFICATION_ID, createNotification())
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
    unregisterReceiverIfNeeded()
    super.onDestroy()
  }


  private fun unregisterReceiverIfNeeded() {
    if (isReceiverRegistered) {
      unregisterReceiver(bluetoothReceiver)
      isReceiverRegistered = false
    }
  }

  private fun registerReceiverIfNeeded() {
    if (!isReceiverRegistered) {
      val filter = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
      }
        registerReceiver(bluetoothReceiver, filter)
      isReceiverRegistered = true
    }
  }


  private fun createNotification(): Notification {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
      .setContentTitle("Playing MP3")
      .setContentText("Playback is running")
      .setSmallIcon(android.R.drawable.ic_media_play)
      .setContentIntent(pendingIntent)
      .setOngoing(true)
      .build()
  }
}
