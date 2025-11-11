package com.example.playlistmaker.media.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInfo
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class OpenPlaylistViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {

    private val _playlistInfo = MutableLiveData<PlaylistInfo?>()
    val playlistInfo: LiveData<PlaylistInfo?> = _playlistInfo

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracks

    fun loadPlaylistInfo(playlistId: Int) {
        viewModelScope.launch {
            val info = playlistInteractor.getPlaylistInfo(playlistId)
            if (info != null) {
                _playlistInfo.postValue(info)
                _playlistTracks.postValue(info.tracks)
            } else {
                _playlistInfo.postValue(null)
                _playlistTracks.postValue(emptyList())
            }
        }
    }

    fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
           // val updatedTracks = playlistInteractor.getTracksForPlaylist(playlistId)
          //  _playlistTracks.postValue(updatedTracks)
            loadPlaylistInfo(playlistId) // обновляем и треки, и инфо
        }
    }

    /**
     * Формирует текст сообщения для кнопки "Поделиться"
     * Возвращает null, если в плейлисте нет треков
     */
    fun getShareMessage(): String? {
        val info = _playlistInfo.value ?: return null
        val playlist = info.playlist
        val tracks = _playlistTracks.value ?: emptyList()
        if (tracks.isEmpty()) return null

        val context = getApplication<Application>().applicationContext
        val builder = StringBuilder()

        builder.appendLine(playlist.playlistName)
        if (!playlist.playlistDescr.isNullOrBlank()) {
            builder.appendLine(playlist.playlistDescr)
        }

        // Количество треков через plurals
        val trackCountText = context.resources.getQuantityString(
            R.plurals.track_count,
            tracks.size,
            tracks.size
        )
        builder.appendLine(trackCountText)
        builder.appendLine()

        tracks.forEachIndexed { index, track ->
            val minutes = (track.trackTimeMillis / 1000) / 60
            val seconds = (track.trackTimeMillis / 1000) % 60
            val duration = String.format("%02d:%02d", minutes, seconds)
            builder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
        }

        return builder.toString().trim()
    }

    fun deletePlaylist(
        playlistId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                playlistInteractor.deletePlaylist(playlistId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Ошибка при удалении плейлиста")
            }
        }
    }


}
