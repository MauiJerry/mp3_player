package com.fallenstedt.mp3_player.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.fallenstedt.mp3_player.components.GoBackHome
import com.fallenstedt.mp3_player.services.MusicManager

@Composable
fun PlayerScreen(navController: NavController, context: Context, filePath: String?) {

    val player = remember { mutableStateOf<ExoPlayer?>(null)}

    Log.d("NavigationDebug", "Received filePath: $filePath")
    if (filePath.isNullOrEmpty()) {
        return GoBackHome(navController)
    }

    LaunchedEffect(filePath) {
        player.value = MusicManager().buildPlayer(context, filePath)
    }

    DisposableEffect(Unit) {
        onDispose {
            player.value?.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = {navController.popBackStack()}) {
            Text("Back")
        }

        Button(onClick = {player.value?.play()}) {
            Text("Play")
        }
    }
}
