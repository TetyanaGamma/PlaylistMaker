package com.example.playlistmaker.search.domain

import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TrackConsumer
    ) {
        executor.execute {
            consumer.comsume(tracksRepository.searchTracks(expression))
        }
    }
}