package com.fallenstedt.mp3_player.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fallenstedt.mp3_player.Mp3PlayerScreens
import com.fallenstedt.mp3_player.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mp3PlayerAppBar(
  currentScreen: Mp3PlayerScreens,
  canNavigateBack: Boolean,
  navigateUp: () -> Unit,
  modifier: Modifier = Modifier
) {

  TopAppBar(
    title = { Text(stringResource(currentScreen.title)) },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer
    ),
    modifier = modifier,
    navigationIcon = {
      if (canNavigateBack) {
        IconButton(onClick = navigateUp) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back_button)
          )
        }
      }
    }
  )
}

