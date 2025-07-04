package com.example.playlistmaker.domain.api

interface AudioplayerRepository {
    fun preparePlayer(previewUrl: String,
                      onPrepared: () -> Unit,
                      onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean

}