package com.example.playlistmaker.mediateca.db.converters

import com.example.playlistmaker.mediateca.db.entities.TrackEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConverter {

    fun mapTrackToEntity(track: Track): TrackEntity {
        return TrackEntity(
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
            isFavourite = true, // Для сохранения в избранное
            addedTimestamp = System.currentTimeMillis()
        )
    }

    fun mapEntityToTrack(entity: TrackEntity): Track {
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
            isFavourite = entity.isFavourite // Используем значение из БД
        )
    }
}