package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
) : SearchHistoryRepository {


    private val maxSize = 10

    override fun getHistory(): List<Track> {
        return storage.getData() ?: emptyList()
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        // Удаляем дубликат по trackId, если есть
        history.removeAll { it.trackId == track.trackId }

        // Добавляем в начало
        history.add(0, track)

        // Ограничиваем размер
        if (history.size > maxSize) {
            history.removeAt(history.size - 1)
        }
        storage.storeData(ArrayList(history))
    }

    override fun clearHistory() {
        storage.storeData(arrayListOf())
    }


}