package com.fallenstedt.mp3_player.ui.viewmodel

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.fallenstedt.mp3_player.ui.components.list.ListScreenListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.UUID

class MediaControllerViewModel : ViewModel() {
  private lateinit var _mediaController: MediaController
  val mediaController: MediaController
    get() = _mediaController

  private val _uiState = MutableStateFlow(MediaUIState())
  val uiState: StateFlow<MediaUIState> = _uiState.asStateFlow()

  var isPlaying by mutableStateOf(false)
    private set
  

  fun setMediaController(mediaController: MediaController) {
    _mediaController = mediaController
    _mediaController.addListener(mediaPlayerListeners)
  }

  fun startPlaylist(context: Context, files: List<File>, startIndex: Int = 0) {
    Log.d("Mp3PlayerApp.MediaControllerVM", "starting playlist at index $startIndex with ${files.count()} items")

    val (mediaItems, listScreenListItems) = generateMediaItems(files, context)
    listScreenListItems[startIndex].emphasize = true

    mediaController.clearMediaItems()
    mediaController.addMediaItems(mediaItems)
    mediaController.prepare()
    mediaController.seekToDefaultPosition(startIndex)
    mediaController.play()

    val (title, artist, album) = getSongInfo(mediaController)
    updateCurrentPlayingSong(title, album, artist)
    updatePlaylist(listScreenListItems)
  }

  private val mediaPlayerListeners = object: Player.Listener {
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
      super.onMediaItemTransition(mediaItem, reason)
      val (title, artist, album) = getSongInfo(mediaController)

      updateCurrentPlayingSong(title, album, artist)
    }
    override fun onEvents(player: Player, events: Player.Events){
      if (events.containsAny(
          Player.EVENT_PLAY_WHEN_READY_CHANGED,
          Player.EVENT_PLAYBACK_STATE_CHANGED
        )
      ) {
        updatePlayState()
      }
    }
  }

  private fun emphasizeNextSong(currentState: MediaUIState): List<ListScreenListItem> {
    val stateCopy = currentState.copy()
    val prevEmphasizeIndex = stateCopy.playlist.indexOfFirst { it.emphasize == true }
    if (prevEmphasizeIndex > -1) {
      stateCopy.playlist[prevEmphasizeIndex].emphasize = false
    }
    val nextIndex = mediaController.currentMediaItemIndex
    if (nextIndex > -1) {
      stateCopy.playlist[nextIndex].emphasize = true
    }
    return stateCopy.playlist
  }

  private fun generateMediaItems(
    files: List<File>,
    context: Context
  ): Pair<List<MediaItem>, List<ListScreenListItem>> {
    val mediaItems = mutableListOf<MediaItem>()
    val listScreenListItems = mutableListOf<ListScreenListItem>()

    files.forEachIndexed { index, file ->
      val metadata = getMetadataFromFile(context, file)
      val mediaItem = MediaItem.Builder()
        .setUri(file.toUri())
        .setMediaMetadata(metadata)
        .build()
      val listScreenListItem = ListScreenListItem(
        key = UUID.randomUUID().toString(),
        text = metadata.title.toString(),
        subtext = metadata.artist.toString(),
        onClick = {
          mediaController.seekTo(index, 0)
          mediaController.play()
        }
      )

      mediaItems.add(mediaItem)
      listScreenListItems.add(listScreenListItem)
    }

    return Pair(mediaItems, listScreenListItems)
  }

  private fun updatePlaylist(items: List<ListScreenListItem>) {
    _uiState.update { currentState ->
      currentState.copy(
        playlist = items
      )
    }
  }


  private fun updatePlayState() {
    isPlaying = mediaController.playWhenReady && mediaController.playbackState == Player.STATE_READY
    _uiState.update { currentState ->
      currentState.copy(
        playlist = emphasizeNextSong(currentState)
      )
    }
    Log.d(
      "Mp3PlayerApp.MediaControllerViewModel",
      "Play state has changed. isPlaying: $isPlaying"
    )
  }

  private fun updateCurrentPlayingSong(currentTitle: String, currentAlbum: String, currentArtist: String) {

    _uiState.update { currentState ->
      currentState.copy(
        playlist = emphasizeNextSong(currentState),
        currentTitle = currentTitle,
        currentArtist = currentArtist,
        currentAlbum = currentAlbum
      )
    }
  }

  private fun getMetadataFromFile(context: Context, file: File): MediaMetadata {
    val retriever = MediaMetadataRetriever()
    try {
      retriever.setDataSource(context, file.toUri())

      val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.name
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