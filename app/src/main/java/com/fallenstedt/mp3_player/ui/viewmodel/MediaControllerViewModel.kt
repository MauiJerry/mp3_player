package com.fallenstedt.mp3_player.ui.viewmodel
import android.annotation.SuppressLint
import com.fallenstedt.mp3_player.services.PreferenceManager

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.fallenstedt.mp3_player.ui.components.list.ListScreenListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID


class MediaControllerViewModel : ViewModel() {
  private lateinit var preferenceManager: PreferenceManager
  @SuppressLint("StaticFieldLeak")
  private lateinit var context: Context
  private lateinit var _mediaController: MediaController
  val mediaController: MediaController
    get() = _mediaController

  private val _uiState = MutableStateFlow(MediaUIState())
  val uiState: StateFlow<MediaUIState> = _uiState.asStateFlow()

  private var updateSliderJob: Job? = null

  var sliderPosition by mutableFloatStateOf(0f)
  var duration by mutableFloatStateOf(0f)
  var isPlaying by mutableStateOf(false)
    private set
  

  @SuppressLint("PrivateApi")
  fun setMediaController(mediaController: MediaController) {
    context = mediaController.applicationLooper.thread.contextClassLoader?.loadClass("android.app.ActivityThread")
      ?.getMethod("currentApplication")
      ?.invoke(null) as Context
    preferenceManager = PreferenceManager(context)
    _mediaController = mediaController
    _mediaController.addListener(mediaPlayerListeners)
  }
  fun restorePlaybackState(onRestore: (List<File>, Int, Long) -> Unit) {
    val playlistPaths = preferenceManager.getPlaylist()
    val files = playlistPaths.mapNotNull { path ->
      val file = File(path)
      if (file.exists()) file else null
    }
    if (files.isNotEmpty()) {
      val index = preferenceManager.getCurrentIndex().coerceIn(files.indices)
      val position = preferenceManager.getPosition()
      onRestore(files, index, position)
    }
  }


  fun seekTo(position: Long) {
    _mediaController.seekTo(position)
  }

  fun startPlaylist(context: Context, files: List<File>, startIndex: Int = 0) {
    // Save playlist and index
    preferenceManager.savePlaylist(files.map { it.absolutePath })
    preferenceManager.saveCurrentIndex(startIndex)
    Log.d("Mp3PlayerApp.MediaControllerVM", "starting playlist at index $startIndex with ${files.count()} items")
    _uiState.update { currentState ->
      currentState.copy(
        isLoadingPlaylist = true
      )
    }

    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        val (mediaItems, listScreenListItems) = generateMediaItems(files, context)

        withContext(Dispatchers.Main) {
          var index = startIndex
          if (!isStartIndexValid(listScreenListItems, startIndex)) {
            index = 0
          }
          listScreenListItems[index].emphasize = true
          updatePlaylist(listScreenListItems)

          mediaController.clearMediaItems()
          mediaController.addMediaItems(mediaItems)
          mediaController.prepare()
          mediaController.seekToDefaultPosition(index)
          preferenceManager.savePosition(mediaController.currentPosition)
          mediaController.play()

          val (title, artist, album) = getSongInfo(mediaController)
          updateCurrentPlayingSong(title, album, artist)

          _uiState.update { currentState ->
            currentState.copy(
              isLoadingPlaylist = false
            )
          }
        }
      }
    }
  }

  private fun startUpdatingSlider() {
    updateSliderJob?.cancel()
    updateSliderJob = viewModelScope.launch {
      while (isPlaying) {
        sliderPosition = mediaController.currentPosition.toFloat()
        delay(1000) // Update every 1000 milliseconds (1 second)
      }
    }
  }

  private fun stopUpdatingSlider() {
    updateSliderJob?.cancel()
    updateSliderJob = null
  }


  private val mediaPlayerListeners = object: Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
      super.onPlaybackStateChanged(playbackState)
      Log.d("Mp3PlayerApp.MediaControllerViewModel", "onPlaybackStateChanged")

      if (playbackState == Player.STATE_READY) {
        duration = mediaController.duration.toFloat()
      }
    }
    override fun onIsPlayingChanged(isPlayingValue: Boolean) {
      super.onIsPlayingChanged(isPlayingValue)
      Log.d("Mp3PlayerApp.MediaControllerViewModel", "onIsPlayingChanged")
      isPlaying = isPlayingValue
      if (isPlaying) {
        startUpdatingSlider()
      } else {
        stopUpdatingSlider()
      }
    }

    override fun onPositionDiscontinuity(
      oldPosition: Player.PositionInfo,
      newPosition: Player.PositionInfo,
      reason: Int
    ) {
      super.onPositionDiscontinuity(oldPosition, newPosition, reason)
      Log.d("Mp3PlayerApp.MediaControllerViewModel", "onPositionDiscontinuity")
      sliderPosition = mediaController.currentPosition.toFloat()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
      super.onMediaItemTransition(mediaItem, reason)
      Log.d("Mp3PlayerApp.MediaControllerViewModel", "onMediaItemTransition")

      val (title, artist, album) = getSongInfo(mediaController)

      updateCurrentPlayingSong(title, album, artist)
    }
    override fun onEvents(player: Player, events: Player.Events){
      if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
        onPlaybackStateChanged(_mediaController.playbackState)
      }
      if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
        onIsPlayingChanged(_mediaController.isPlaying)
      }
      if (events.containsAny(
          Player.EVENT_PLAY_WHEN_READY_CHANGED,
          Player.EVENT_PLAYBACK_STATE_CHANGED
        )
      ) {
        emphasizeCorrectSong()
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
        text = metadata.title?.takeIf { it.isNotBlank() }?.toString() ?: file.nameWithoutExtension,
        subtext = metadata.artist?.toString() ?: "", // Explicitly convert to String
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


  private fun emphasizeCorrectSong() {
//    isPlaying = mediaController.playWhenReady && mediaController.playbackState == Player.STATE_READY
    _uiState.update { currentState ->
      currentState.copy(
        playlist = emphasizeNextSong(currentState)
      )
    }
//    Log.d(
//      "Mp3PlayerApp.MediaControllerViewModel",
//      "Play state has changed. isPlaying: $isPlaying"
//    )
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

    if (mediaController.duration > 0) {
      duration = mediaController.duration.toFloat()
    }

  }

  private fun getMetadataFromFile(context: Context, file: File): MediaMetadata {
    val retriever = MediaMetadataRetriever()
    try {
      retriever.setDataSource(context, file.toUri())

      val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.name.toString()
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

  private fun isStartIndexValid(listScreenListItems: List<ListScreenListItem>, startIndex: Int): Boolean {
    return startIndex >= 0 && startIndex < listScreenListItems.size
  }
}
