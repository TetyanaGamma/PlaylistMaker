package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val HISTORY_KEY = "search_history"
    private val maxSize = 10

    // получаем историю поиска
    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Сохранить текущий список истории
    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    // Добавить трек в историю
    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        // Удаляем дубликат по trackId, если есть
        history.removeAll { it.trackId == track.trackId }

        // Добавляем в начало
        history.add(0, track)

        // Ограничиваем размер
        if (history.size > maxSize) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    // Очистить историю
    fun clearHistory() {
        sharedPreferences
            .edit()
            .remove(HISTORY_KEY)
            .apply()
    }
}