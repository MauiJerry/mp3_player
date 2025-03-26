package com.fallenstedt.mp3_player

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import android.app.AlertDialog as AlertDialogAndroid
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
  private var showPermissionDialog by mutableStateOf(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      AppTheme(dynamicColor = false) {
        PermissionRequestScreen {
          startApp()
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaControllerViewModel?.mediaController?.release()
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
    mediaControllerFuture.thenAccept { mediaController ->
      mediaControllerViewModel = ViewModelProvider(this)[MediaControllerViewModel::class.java]
      mediaControllerViewModel!!.setMediaController(mediaController)

      setContent {
        mediaControllerViewModel?.let { viewModel ->
          viewModel.restorePlaybackState { files, index, position ->
            viewModel.startPlaylist(this, files, index)
            viewModel.mediaController.seekTo(position)

            setContent {
              AppTheme(dynamicColor = false) {
                Mp3PlayerApp(mediaControllerViewModel = viewModel)
              }
            }
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

  @Composable
  fun PermissionRequestScreen(onPermissionGranted: () -> Unit) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Manifest.permission.READ_MEDIA_AUDIO
    } else {
      Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var hasPermission by remember { mutableStateOf(hasStoragePermission()) }
    val launcher = rememberLauncherForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
      hasPermission = isGranted
      if (isGranted) {
        onPermissionGranted()
      } else {
        showPermissionDialog = true
      }
    }

    LaunchedEffect(Unit) {
      if (!hasPermission) {
        launcher.launch(permission)
      } else {
        onPermissionGranted()
      }
    }

    if (showPermissionDialog){
      showSettingsDialog { showPermissionDialog = false }
    }
  }

  @Composable
  fun showSettingsDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
      onDismissRequest = { onDismissRequest() },
      title = { androidx.compose.material3.Text("Permission Denied") },
      text = { androidx.compose.material3.Text("To enable music playback, allow access in Settings.") },
      confirmButton = {
        androidx.compose.material3.TextButton(onClick = {
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
          intent.data = Uri.fromParts("package", packageName, null)
          startActivity(intent)
          onDismissRequest()
        }) {
          androidx.compose.material3.Text("Go to Settings")
        }
      },
      dismissButton = {
        androidx.compose.material3.TextButton(onClick = { onDismissRequest() }) {
          androidx.compose.material3.Text("Cancel")
        }
      }
    )
  }
  companion object {
    private const val REQUEST_CODE = 1001
  }
}