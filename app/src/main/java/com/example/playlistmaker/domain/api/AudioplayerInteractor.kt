package com.example.playlistmaker.domain.api

interface AudioplayerInteractor {
    fun preparePlayer(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun getCurrentPosition(): Int
    fun releasePlayer()
    fun isPlaying(): Boolean
}