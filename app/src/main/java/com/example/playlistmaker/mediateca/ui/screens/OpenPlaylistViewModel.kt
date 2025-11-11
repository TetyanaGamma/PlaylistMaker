package com.example.playlistmaker.media.ui.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInfo
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class OpenPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    // Вся информация о плейлисте (playlist + tracks + totalDuration)
    private val _playlistInfo = MutableLiveData<PlaylistInfo?>()
    val playlistInfo: LiveData<PlaylistInfo?> = _playlistInfo


    // Отдельно список треков
    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracks

    // Метод для загрузки информации о плейлисте
    fun loadPlaylistInfo(playlistId: Int) {
        viewModelScope.launch {
            val info = playlistInteractor.getPlaylistInfo(playlistId)
            if (info != null) {
                _playlistInfo.postValue(info)
                _playlistTracks.postValue(info.tracks) // сразу обновляем список треков
            } else {
                _playlistInfo.postValue(null)
                _playlistTracks.postValue(emptyList())
            }
        }
    }

    fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
            // После удаления обновляем список треков
            val updatedTracks = playlistInteractor.getTracksForPlaylist(playlistId)
            _playlistTracks.postValue(updatedTracks)
        }
    }

}
