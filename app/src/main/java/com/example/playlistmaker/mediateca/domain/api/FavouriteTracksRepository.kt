package com.example.playlistmaker.mediateca.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTracksRepository {
    suspend fun addTrackToFavourites(track: Track)
    suspend fun removeTrackFromFavourites(track: Track)
    fun getAllFavouriteTracks(): Flow<List<Track>>
}