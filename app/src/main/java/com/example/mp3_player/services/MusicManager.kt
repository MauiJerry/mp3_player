package com.example.mp3_player.services

import android.content.Context
import android.os.Environment
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
        return musicFiles
    }
    // Get the root Music directory
    fun getRootMusicDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    }
}