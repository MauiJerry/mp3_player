package com.fallenstedt.mp3_player.services

import android.content.Context
import android.os.Environment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

class MusicManager {
    fun getMusicFiles(directory: File): List<File> {
        val musicFiles = mutableListOf<File>()
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            files?.forEach { file ->
                if (file.isDirectory || file.extension.equals("mp3", ignoreCase = true)) {
                    musicFiles.add(file)
                }
            }
        }
        val sortedMusicFiles = musicFiles.sortedBy { file -> file.name }
        return sortedMusicFiles
    }
    // Get the root Music directory
    fun getRootMusicDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    }

    fun buildPlayer(context: Context, filePath: String): ExoPlayer {
        val player = ExoPlayer.Builder(context).build()
        player.setMediaItem(MediaItem.fromUri(filePath))

        player.prepare()
        return player
    }
}