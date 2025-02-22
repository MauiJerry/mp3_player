package com.fallenstedt.mp3_player

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.fallenstedt.mp3_player.services.PlaybackService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class Mp3PlayerApplication : Application() {

  private var mediaController: MediaController? = null
  private val applicationJob = Job()
  private val applicationScope = CoroutineScope(Dispatchers.Main + applicationJob)

  override fun onCreate() {
    super.onCreate()
    startService()
  }

  private fun startService() {
    val sessionToken =
      SessionToken(this, ComponentName(this, PlaybackService::class.java))
    val controllerFuture =
      MediaController.Builder(this, sessionToken).buildAsync()
    controllerFuture.addListener({

      mediaController = controllerFuture.get()
      Log.d("Mp3PlayerApp", "Created Media Controller")

    },
    MoreExecutors.directExecutor())

  }

  fun getMediaController(): MediaController? {
    return mediaController
  }

  fun setMediaController(mediaController: MediaController) {
    this.mediaController = mediaController
  }

  override fun onTerminate() {
    super.onTerminate()
    applicationJob.cancel()
  }
}