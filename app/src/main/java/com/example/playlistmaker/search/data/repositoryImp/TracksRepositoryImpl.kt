package com.example.playlistmaker.search.data.repositoryImp

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.TrackResponse
import com.example.playlistmaker.search.data.network.TrackSearchRequest
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.api.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {

        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackResponse).results.map {
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
            }
        } else {
            return emptyList()
        }
    }
}