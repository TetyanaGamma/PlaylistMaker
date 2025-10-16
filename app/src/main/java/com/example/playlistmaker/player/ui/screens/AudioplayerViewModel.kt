package com.example.playlistmaker.player.ui.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.interactors.FavouriteTracksInteractor
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.player.domain.interactor.AudioplayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioplayerViewModel(
    private val audioplayerInteractor: AudioplayerInteractor,
    private val track: Track,
    private val favouriteTracksInteractor: FavouriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    // Список плейлистов
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private var currentTrack: Track? = null

    fun setTrack(track: Track) {
        currentTrack = track
        // Проверяем актуальное состояние из БД
        checkFavoriteStatus(track)
    }

    private fun checkFavoriteStatus(track: Track) {
        viewModelScope.launch {
            favouriteTracksInteractor.getAllFavouriteTracks()
                .onEach { favouriteTracks ->
                    val isFav = favouriteTracks.any { it.trackId == track.trackId }
                    track.isFavourite = isFav
                    _isFavourite.postValue(isFav)
                }
                .catch { error ->
                    Log.e("AudioPlayer", "Error checking favorite status", error)
                    _isFavourite.postValue(false)
                }
                .launchIn(this)
        }
    }

    fun onFavoruiteClicked() {
        val track = currentTrack ?: return

        viewModelScope.launch {
            if (track.isFavourite) {
                favouriteTracksInteractor.removeTrackFromFavourites(track)
            } else {
                favouriteTracksInteractor.addTrackToFavourites(track)
            }
            // Обновляем состояние
            track.isFavourite = !track.isFavourite
            _isFavourite.value = track.isFavourite
        }
    }


    private var progressJob: Job? = null

    init {
        preparePlayer()
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { list ->
                _playlists.postValue(list)
            }
        }
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
                delay(PROGRESS_UPDATE_DELAY)
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

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
        }
    }


    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        private const val PROGRESS_UPDATE_DELAY = 300L // миллисекунды
    }

}