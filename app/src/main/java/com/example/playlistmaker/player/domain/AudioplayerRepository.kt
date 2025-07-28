package com.example.playlistmaker.player.domain

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