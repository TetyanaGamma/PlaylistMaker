package com.example.playlistmaker.media.ui.screens

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor

class OpenPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    suspend fun getPlaylistInfo(playlistId: Int) =
        playlistInteractor.getPlaylistInfo(playlistId)
}
