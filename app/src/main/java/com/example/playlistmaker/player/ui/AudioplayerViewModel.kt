package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale


class AudioplayerViewModel(private val audioplayerInteractor: AudioplayerInteractor,
                           private val previewUrl: String) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getFactory(audioplayerInteractor: AudioplayerInteractor,
                       trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioplayerViewModel(audioplayerInteractor,trackUrl)
            }
        }
    }

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

  //  private val mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        preparePlayer()
    }

 /*   override fun onCleared() {
        super.onCleared()
        audioplayerInteractor.release()
        resetTimer()
    }*/

    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        audioplayerInteractor.preparePlayer(
            previewUrl,
            onPrepared = {
                playerStateLiveData.postValue(STATE_PREPARED)
            },
            onCompletion = {
                playerStateLiveData.postValue(STATE_PREPARED)
                progressTimeLiveData.postValue("00:00")
                resetTimer()
            }
        )
    }


    private fun startPlayer() {
        audioplayerInteractor.startPlayer()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        audioplayerInteractor.pausePlayer()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startTimerUpdate() {
        progressTimeLiveData.postValue(SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(audioplayerInteractor.getCurrentPosition()))
        handler.postDelayed(timerRunnable, 200)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue("00:00")
    }

    fun onPause() {
        if (audioplayerInteractor.isPlaying()) pausePlayer()
    }

    fun onDestroy() {
        audioplayerInteractor.releasePlayer()
        resetTimer()
    }

}