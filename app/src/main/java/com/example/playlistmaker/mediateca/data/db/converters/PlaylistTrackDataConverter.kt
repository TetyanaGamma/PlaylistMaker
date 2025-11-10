package com.example.playlistmaker.mediateca.data.db.converters

import com.example.playlistmaker.mediateca.data.db.entities.PlaylistTracksEntity
import com.example.playlistmaker.search.domain.model.Track

class PlaylistTrackDataConverter {

    // Конвертация из Entity в Domain модель
    fun mapEntityToTrack(entity: PlaylistTracksEntity, isFavorite: Boolean = false): Track {
        return Track(
            trackId = entity.trackId,
            artworkUrl100 = entity.artworkUrl100,
            trackName = entity.trackName,
            artistName = entity.artistName,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            trackTimeMillis = entity.trackTimeMillis,
            previewUrl = entity.previewUrl,
            isFavourite = isFavorite
        )
    }

    // Конвертация из Domain модели в Entity (только внутри Data слоя)
    fun mapTrackToEntity(track: Track, playlistId: Int): PlaylistTracksEntity {
        return PlaylistTracksEntity(
            playlistId = playlistId,
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            isFavourite = track.isFavourite

        )
    }

}