package com.fallenstedt.mp3_player.receivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player


class BluetoothReceiver(private val player: Player) :
  BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action

    when (action) {
      BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
        if (player.isPlaying) {
          player.pause()
        }
      }
      else -> {}
    }
  }
}