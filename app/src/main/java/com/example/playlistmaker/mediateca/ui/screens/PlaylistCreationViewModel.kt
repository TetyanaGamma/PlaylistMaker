package com.example.playlistmaker.mediateca.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistCreationViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    fun createPlaylist(
        name: String,
        description: String?,
        coverUri: Uri?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Сохраняем обложку только если выбрана
                val savedCoverUri = coverUri?.let { interactor.saveCoverImage(it) }

                // Создаём плейлист (описание и обложка могут быть null)
                val result = interactor.createPlaylist(name, description, savedCoverUri)

                onResult(result.isSuccess)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
