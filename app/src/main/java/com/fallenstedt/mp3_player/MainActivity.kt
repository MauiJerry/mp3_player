package com.fallenstedt.mp3_player

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.fallenstedt.mp3_player.services.PlaybackService
import com.fallenstedt.mp3_player.ui.Mp3PlayerApp
import com.fallenstedt.mp3_player.ui.theme.AppTheme
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.CompletableFuture

class MainActivity : ComponentActivity() {
  private var mediaControllerViewModel: MediaControllerViewModel? = null
  private val mediaControllerFuture = CompletableFuture<MediaController>()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    if (hasStoragePermission()) {
      startApp()
    } else {
      requestStoragePermission()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (mediaControllerViewModel != null) {
      if (mediaControllerViewModel!!.mediaController != null) {
        mediaControllerViewModel!!.mediaController.release()

      }
    }
  }

  private fun startService() {
    val sessionToken =
      SessionToken(this, ComponentName(this, PlaybackService::class.java))
    val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
    controllerFuture.addListener({
      mediaControllerFuture.complete(controllerFuture.get())
      Log.d("Mp3PlayerApp.Mp3PlayerApplication", "Created Media Controller")
    }, MoreExecutors.directExecutor())
  }


  private fun startApp() {
    startService()
    mediaControllerFuture.thenAccept{ mediaController ->
      mediaControllerViewModel = ViewModelProvider(this)[MediaControllerViewModel::class.java]
      mediaControllerViewModel!!.setMediaController(mediaController)

      runOnUiThread {
        setContent {
          AppTheme(dynamicColor = false) {
            Mp3PlayerApp(mediaControllerViewModel = mediaControllerViewModel!!)
          }
        }
      }
    }
  }

  private fun hasStoragePermission(): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Manifest.permission.READ_MEDIA_AUDIO
    } else {
      Manifest.permission.READ_EXTERNAL_STORAGE
    }
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
  }


  private fun requestStoragePermission() {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Manifest.permission.READ_MEDIA_AUDIO
    } else {
      Manifest.permission.READ_EXTERNAL_STORAGE
    }

    when {
      ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
        return
      }
      ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
        showPermissionRationaleDialog()
      }
      else -> {
        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE)
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    deviceId: Int
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    if (requestCode == REQUEST_CODE) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        startApp()
      } else {
        // Permission denied, check if user selected "Don't ask again"
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
          showSettingsDialog()
        } else {
          showPermissionRationaleDialog()
        }
      }
    }
  }
  private fun showPermissionRationaleDialog() {
    AlertDialog.Builder(this)
      .setTitle("Storage Permission Required")
      .setMessage("This app needs access to your music directory to play songs.")
      .setPositiveButton("OK") { _, _ ->
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          Manifest.permission.READ_MEDIA_AUDIO
        } else {
          Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE)
      }
      .setNegativeButton("Cancel", null)
      .show()
  }
  private fun showSettingsDialog() {
    AlertDialog.Builder(this)
      .setTitle("Permission Denied")
      .setMessage("To enable music playback, allow access in Settings.")
      .setPositiveButton("Go to Settings") { _, _ ->
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
      }
      .setNegativeButton("Cancel", null)
      .show()
  }



  companion object {
    private const val REQUEST_CODE = 1001
  }
}
