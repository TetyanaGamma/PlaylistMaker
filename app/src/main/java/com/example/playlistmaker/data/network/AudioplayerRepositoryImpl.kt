package com.example.playlistmaker.data.network

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioplayerRepository

class AudioplayerRepositoryImpl(
    private val mediaPlayerFactory: () -> MediaPlayer
) : AudioplayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(
        previewUrl: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        releasePlayer()

        mediaPlayer = mediaPlayerFactory().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
    }

    override fun stopPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}
