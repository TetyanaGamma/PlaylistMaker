package com.example.playlistmaker.mediateca.ui.screens

import android.net.Uri
import android.util.Log
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

    // Загружаем плейлист по ID
    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            playlist?.let {
                _playlistData.value = it
                currentPlaylistId = it.playlistId //  сохраняем ID
                Log.d("PlaylistEditVM", "Loaded playlist id=${it.playlistId}")
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
        val oldPlaylist = _playlistData.value
        Log.d("PlaylistEditVM", "oldPlaylist = $oldPlaylist, name = $name")
        if (oldPlaylist == null || name.isBlank()) {
            Log.d("PlaylistEditVM", "Cannot save: oldPlaylist is null or name is blank")
            onResult(false)
            return
        }
        val id = currentPlaylistId ?: oldPlaylist.playlistId
        Log.d("PlaylistEditVM", "Updating playlist id=$id, name=$name, coverUri=$coverUri")

        viewModelScope.launch {
            try {
                // Определяем финальный URL обложки
                val finalCoverUrl: String = if (coverUri != null) {
                    // Сохраняем новую выбранную обложку
                    interactor.saveCoverImage(coverUri).toString()
                } else {
                    // Используем существующую, если есть, или пустую строку
                    oldPlaylist.playlistCoverUrl.orEmpty()
                }

                val updatedPlaylist = oldPlaylist.copy(
                    playlistId = id,
                    playlistName = name,
                    playlistDescr = description.orEmpty(),
                    playlistCoverUrl = finalCoverUrl
                )
                // Сохраняем изменения в БД
                interactor.updatePlaylist(updatedPlaylist)
                onResult(true)

            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
