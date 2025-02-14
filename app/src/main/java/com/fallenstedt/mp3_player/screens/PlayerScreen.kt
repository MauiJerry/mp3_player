package com.fallenstedt.mp3_player.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
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
        val exoPlayer = MusicManager().buildPlayer(context, filePath)
        player.value = exoPlayer
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(Unit) {
        onDispose {
            player.value?.release()
        }
    }

    if (player.value != null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            ) {
            AndroidView(
                modifier = Modifier.fillMaxSize().wrapContentSize(),
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        Log.d("PlayerViewDebug", "PlayerView initialized")
                        this.setBackgroundColor(android.graphics.Color.WHITE)
                        this.player = player.value
                        this.useController = true
                    }
                },
                update = {playerView ->
                    Log.d("PlayerViewDebug", "Updating playerview")
                    playerView.player = player.value
                    playerView.setBackgroundColor(android.graphics.Color.WHITE)
                }
            )
        }

    } else {
        // Show a loading UI while the player initializes
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Loading...")
        }
    }
}
