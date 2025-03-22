package com.fallenstedt.mp3_player.services

import android.os.Environment
import android.util.Log
import java.io.File

class FileService {
    private fun getFilesInternal(directory: File, filter: (File) -> Boolean): List<File> {
        val musicFiles = mutableListOf<File>()
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            files?.forEach { file ->
                if (filter(file)) {
                    Log.d(
                        "Mp3PlayerApp.FileService",
                        "loading file ${file.name}, isDirectory ${file.isDirectory}"
                    )
                    musicFiles.add(file)
                }
            }
        }
        return musicFiles.sortedBy { file -> file.name }
    }

    fun getFiles(directory: File): List<File> {
        return getFilesInternal(directory) { file -> !file.name.startsWith(".") }
    }

    fun getMusicFilesInDirectory(directory: File): List<File> {
        return getFilesInternal(directory) { file -> !file.name.startsWith(".") && file.isFile }
    }

    fun getRootMusicDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    }
}