package com.fallenstedt.mp3_player.ui.viewmodel

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File


class MediaControllerViewModel : ViewModel() {
  private lateinit var _mediaController: MediaController
  val mediaController: MediaController
    get() = _mediaController

  private val _uiState = MutableStateFlow(MediaUIState())
  val uiState: StateFlow<MediaUIState> = _uiState.asStateFlow()
  var hasPlaylistLoaded by mutableStateOf(false)
    private set
  
  private val mediaPlayerListeners = object: Player.Listener {
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
      super.onMediaItemTransition(mediaItem, reason)
      val (title, artist, album) = getSongInfo(mediaController)

      updateCurrentPlayingSong(title, album, artist)
    }
  }
  
  fun setMediaController(mediaController: MediaController) {
    _mediaController = mediaController
    _mediaController.addListener(mediaPlayerListeners)
  }

  fun startPlaylist(context: Context, files: List<File>, startIndex: Int = 0) {
    Log.d("Mp3PlayerApp.MediaControllerVM", "starting playlist at index $startIndex with ${files.count()} items")

    mediaController.clearMediaItems()
    mediaController.addMediaItems(generateMediaItems(files, context))
    mediaController.prepare()
    mediaController.seekToDefaultPosition(startIndex)
    mediaController.play()

    hasPlaylistLoaded = mediaController.mediaItemCount > 0
    
    val (title, artist, album) = getSongInfo(mediaController)
    updateCurrentPlayingSong(title, album, artist)
  }

  private fun generateMediaItems(
    files: List<File>,
    context: Context
  ) = files.map { file ->
    val metadata = getMetadataFromFile(context, file.toUri())
    MediaItem.Builder().setUri(file.toUri()).setMediaMetadata(metadata).build()
  }

  private fun updateCurrentPlayingSong(currentTitle: String, currentAlbum: String, currentArtist: String) {
    _uiState.update { currentState ->
      currentState.copy(
        currentTitle = currentTitle,
        currentArtist = currentArtist,
        currentAlbum = currentAlbum
      )
    }
  }

  private fun getMetadataFromFile(context: Context, fileUri: Uri): MediaMetadata {
    val retriever = MediaMetadataRetriever()
    try {
      retriever.setDataSource(context, fileUri)

      val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
      val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
      val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
      val trackNo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)

      return MediaMetadata.Builder()
        .setTitle(title)
        .setArtist(artist)
        .setAlbumTitle(album)
        .setTrackNumber(trackNo?.toInt())
        .build()
    } catch (e: Exception) {
      // Handle exceptions (e.g., file not found, unsupported format)
      Log.e("Mp3PlayerApp.MediaControllerViewModel", "Error extracting metadata: ${e.message}")
      return MediaMetadata.EMPTY
    } finally {
      retriever.release()
    }
  }

  private fun getSongInfo(mediaController: MediaController): Triple<String, String, String> {
    val currentMediaItem = mediaController.currentMediaItem
    val currentMediaMetadata = currentMediaItem?.mediaMetadata
    val title = currentMediaMetadata?.title?.toString() ?: ""
    val artist = currentMediaMetadata?.artist?.toString() ?: ""
    val album = currentMediaMetadata?.albumTitle?.toString() ?: ""
    return Triple(title, artist, album)
  }
}