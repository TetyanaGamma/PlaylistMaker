package com.example.playlistmaker.mediateca.domain.interactors

import com.example.playlistmaker.mediateca.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavouriteTracksInteractor(
    private val repository: FavouriteTracksRepository
) {
    // Получение всех избранных треков
    fun getAllFavouriteTracks(): Flow<List<Track>> {
        return repository.getAllFavouriteTracks()
    }

    suspend fun addTrackToFavourites(track: Track) {
        repository.addTrackToFavourites(track)
    }

    suspend fun removeTrackFromFavourites(track: Track) {
        repository.removeTrackFromFavourites(track)
    }
}