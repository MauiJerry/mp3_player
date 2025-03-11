package com.fallenstedt.mp3_player.ui.components

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fallenstedt.mp3_player.ui.viewmodel.MediaControllerViewModel


@Composable
fun MySlider(mediaControllerViewModel: MediaControllerViewModel) {
  Column(
    modifier = Modifier
      .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
      .fillMaxWidth()
  ) {
    Slider(
      value = mediaControllerViewModel.sliderPosition,
      onValueChange = {
        Log.d("Mp3PlayerApp.MySlider", "onValueChange $it")

        mediaControllerViewModel.seekTo(it.toLong())
      },

      valueRange = 0f..mediaControllerViewModel.duration,
      enabled = mediaControllerViewModel.duration > 0f
    )
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Text(text = formatDuration(mediaControllerViewModel.sliderPosition.toLong()))
      Text(text = formatDuration(mediaControllerViewModel.duration.toLong()))

    }
  }
}

fun formatDuration(durationMs: Long): String {
  return DateUtils.formatElapsedTime(durationMs / 1000)
}