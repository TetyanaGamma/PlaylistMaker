package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.Track

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