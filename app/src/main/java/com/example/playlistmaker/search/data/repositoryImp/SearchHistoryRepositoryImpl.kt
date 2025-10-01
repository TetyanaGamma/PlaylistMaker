package com.example.playlistmaker.search.data.repositoryImp

import com.example.playlistmaker.search.data.storage.StorageClient
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val MAX_SIZE = 10

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>
) : SearchHistoryRepository {

    override fun getHistory(): Flow<Resource<List<Track>>> = flow {
        emit(Resource.Loading())
        try {
            val data = storage.getData() ?: emptyList()
            emit(Resource.Success(data))
        } catch (e: Throwable) {
            emit(Resource.Error(e))
        }
    }

    override fun addTrack(track: Track) {
        val history = storage.getData()?.toMutableList() ?: mutableListOf()
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