package com.fallenstedt.mp3_player.services

import android.os.Environment
import java.io.File

class FileService {
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

    fun getRootMusicDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    }


}