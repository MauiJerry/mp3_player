package com.fallenstedt.mp3_player.services

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.json.JSONArray

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun saveSource(source: String) {
        prefs.edit().putString("source", source).apply()
    }

    fun getSource(): String {
        return prefs.getString("source", "Music") ?: "Music"
    }

    fun savePlaylist(paths: List<String>) {
        val json = JSONArray(paths).toString()
        prefs.edit().putString("playlist", json).apply()
    }

    fun getPlaylist(): List<String> {
        val json = prefs.getString("playlist", null) ?: return emptyList()
        val array = JSONArray(json)
        return List(array.length()) { i -> array.getString(i) }
    }

    fun saveCurrentIndex(index: Int) {
        prefs.edit().putInt("current_index", index).apply()
    }

    fun getCurrentIndex(): Int {
        return prefs.getInt("current_index", 0)
    }

    fun savePosition(positionMs: Long) {
        prefs.edit().putLong("position_ms", positionMs).apply()
    }

    fun getPosition(): Long {
        return prefs.getLong("position_ms", 0L)
    }

    fun clearPlaybackState() {
        prefs.edit()
            .remove("playlist")
            .remove("current_index")
            .remove("position_ms")
            .apply()
    }
}
