package com.fallenstedt.mp3_player

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fallenstedt.mp3_player.ui.theme.Mp3_playerTheme
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fallenstedt.mp3_player.services.PlaybackService

class MainActivity : ComponentActivity() {

    @Composable
    fun startService() {
        LocalContext.current.startService(Intent(this, PlaybackService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (hasStoragePermission()) {
            startApp()
        } else {
            requestStoragePermission()
        }
    }

    private fun startApp() {
        setContent {
            Mp3_playerTheme {
                Mp3PlayerApp()
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
