package com.example.playlistmaker.domain.impl

import java.util.concurrent.Executors
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository

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