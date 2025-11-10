package com.example.playlistmaker.search.data.repositoryImp

import com.example.playlistmaker.mediateca.data.db.AppDataBase
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.TrackResponse
import com.example.playlistmaker.search.data.network.TrackSearchRequest
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient,
                           private val database: AppDataBase) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        // эмитим Loading
        emit(Resource.Loading())

        val response = networkClient.doRequest(TrackSearchRequest(expression))

        if (response.resultCode == 200) {
            val tracks = (response as? TrackResponse)?.results?.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.country,
                    it.primaryGenreName,
                    it.releaseDate,
                    it.previewUrl
                )
            } ?: emptyList()


            // Проставляем флаг isFavorite
            database.trackDao().getFavouriteTrackIds().collect { favouriteIds ->
                val updatedTracks = tracks.map { track ->
                    track.copy(isFavourite = favouriteIds.contains(track.trackId))
                }

                emit(Resource.Success(updatedTracks))
            }
        } else {
            emit(Resource.Error(Throwable("Ошибка сети или запроса: код ${response.resultCode}")))
        }

    }.catch { e ->
        emit(Resource.Error(e))
    }
}
