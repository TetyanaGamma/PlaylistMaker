package com.example.playlistmaker.search.data.repositoryImp

import com.example.playlistmaker.search.data.storage.StorageClient
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.model.Track
  const val MAX_SIZE = 10

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
) : SearchHistoryRepository {

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
        if (history.size > MAX_SIZE) {
            history.removeAt(history.size - 1)
        }
        storage.storeData(ArrayList(history))
    }

    override fun clearHistory() {
        storage.storeData(arrayListOf())
    }


}