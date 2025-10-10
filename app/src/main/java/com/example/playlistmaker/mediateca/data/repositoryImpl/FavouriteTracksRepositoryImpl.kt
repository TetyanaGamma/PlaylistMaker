package com.example.playlistmaker.mediateca.data.repositoryImpl

import com.example.playlistmaker.mediateca.data.db.AppDataBase
import com.example.playlistmaker.mediateca.data.db.converters.TrackDbConverter
import com.example.playlistmaker.mediateca.data.db.dao.TrackDao
import com.example.playlistmaker.mediateca.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteTracksRepositoryImpl(
    private val trackDao: TrackDao,
    private val converter: TrackDbConverter
) : FavouriteTracksRepository {


    override suspend fun addTrackToFavourites(track: Track) {
        val entity = converter.mapTrackToEntity(track)
        trackDao.insertTrack(entity)
    }

    override suspend fun removeTrackFromFavourites(track: Track) {
        trackDao.deleteTrack(track.trackId)
    }

    override fun getAllFavouriteTracks(): Flow<List<Track>> {
        return trackDao.getAll()
            .map { entities ->
                entities.map { converter.mapEntityToTrack(it) }
            }
    }

    override fun getFavouriteTrackIds(): Flow<List<Int>> {
        return trackDao.getFavouriteTrackIds()
    }
}