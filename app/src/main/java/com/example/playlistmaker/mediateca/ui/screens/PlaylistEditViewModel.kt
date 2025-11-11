package com.example.playlistmaker.mediateca.ui.screens

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.mediateca.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val interactor: PlaylistInteractor
) : PlaylistCreationViewModel(interactor) {

    private val _playlistData = MutableLiveData<Playlist>()
    val playlistData: LiveData<Playlist> = _playlistData
    private var currentPlaylistId: Int? = null
  private val converter = PlaylistDbConverter()

    // Загружаем плейлист по ID
    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            playlist?.let {
                _playlistData.value = it
                currentPlaylistId = it.playlistId // ✅ сохраняем ID
            }
        }
    }


    // Обновляем плейлист
    fun updatePlaylist(
        name: String,
        description: String?,
        coverUri: Uri?,
        onResult: (Boolean) -> Unit
    ) {
        val oldPlaylist = _playlistData.value ?: return onResult(false)
        val id = currentPlaylistId ?: oldPlaylist.playlistId

        viewModelScope.launch {
            try {
                // Если выбрали новую обложку — сохраняем в хранилище
                val finalCoverUri = coverUri?.let { interactor.saveCoverImage(it) }
                    ?: oldPlaylist.playlistCoverUrl?.let { Uri.parse(it) }

                val updatedPlaylist = oldPlaylist.copy(
                    playlistId = id,
                    playlistName = name,
                    playlistDescr = description.orEmpty(),
                    playlistCoverUrl = finalCoverUri?.toString()
                )

                // Сохраняем изменения в БД
                interactor.updatePlaylist(updatedPlaylist)
                onResult(true)

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
