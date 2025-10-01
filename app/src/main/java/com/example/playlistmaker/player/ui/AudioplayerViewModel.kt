package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.interactor.AudioplayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioplayerViewModel(
    private val audioplayerInteractor: AudioplayerInteractor,
    private val track: Track
) : ViewModel() {

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

 //   private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private var progressJob: Job? = null

    init {
        preparePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        audioplayerInteractor.preparePlayer(
            track.previewUrl.toString(),
            onPrepared = {
                playerStateLiveData.postValue(STATE_PREPARED)
            },
            onCompletion = {
                playerStateLiveData.postValue(STATE_PREPARED)
                progressTimeLiveData.postValue("00:00")
                stopProgressUpdates()
            }
        )
    }


    private fun startPlayer() {
        audioplayerInteractor.startPlayer()
        playerStateLiveData.postValue(STATE_PLAYING)
        startProgressUpdates()
    }

    private fun pausePlayer() {

        audioplayerInteractor.pausePlayer()
        playerStateLiveData.postValue(STATE_PAUSED)
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = viewModelScope.launch {
            while (isActive) {
                if (playerStateLiveData.value == STATE_PLAYING) {
                    val currentPosition = audioplayerInteractor.getCurrentPosition()
                    progressTimeLiveData.postValue(formatTime(currentPosition))
                }
                delay(300)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun resetProgress() {
        stopProgressUpdates()
        progressTimeLiveData.postValue("00:00")
    }

    fun onPause() {
        if (audioplayerInteractor.isPlaying()) pausePlayer()
    }

    fun onDestroy() {
        audioplayerInteractor.releasePlayer()
        resetProgress()
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

    }

}