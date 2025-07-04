package com.example.playlistmaker.domain.api


interface AudioPlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onError: (String) -> Unit)
    fun start()
    fun pause()
    fun stop()
    fun release()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
}
