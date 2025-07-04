package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AudioplayerInteractor
import com.example.playlistmaker.domain.api.AudioplayerRepository

class AudioplayerInteractorImpl(
    private val audioplayerRepository: AudioplayerRepository)
    : AudioplayerInteractor {

    override fun preparePlayer(
        previewUrl: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        audioplayerRepository.preparePlayer(previewUrl, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        audioplayerRepository.startPlayer()
    }

    override fun pausePlayer() {
        audioplayerRepository.pausePlayer()
    }

    override fun stopPlayer() {
       audioplayerRepository.stopPlayer()
    }

    override fun getCurrentPosition(): Int {
        return audioplayerRepository.getCurrentPosition()
    }

    override fun releasePlayer() {
        audioplayerRepository.releasePlayer()
    }

    override fun isPlaying(): Boolean {
        return audioplayerRepository.isPlaying()
    }
}
