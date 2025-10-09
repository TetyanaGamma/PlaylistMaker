package com.example.playlistmaker.mediateca.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.interactors.FavouriteTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoriteTracksViewModel(
    private val interactor: FavouriteTracksInteractor
) : ViewModel() {

    // Состояния экрана
    sealed class FavoriteTracksState {
        object Empty : FavoriteTracksState()
        data class Content(val tracks: List<Track>) : FavoriteTracksState()
    }

    private val _state = MutableLiveData<FavoriteTracksState>()
    val state: LiveData<FavoriteTracksState> = _state

    init {
        observeFavouriteTracks()
    }

    private fun observeFavouriteTracks() {
        interactor.getAllFavouriteTracks()
            .onEach { tracks ->
                _state.value = if (tracks.isEmpty()) {
                    FavoriteTracksState.Empty
                } else {
                    FavoriteTracksState.Content(tracks)
                }
            }
            .launchIn(viewModelScope)
    }

    // Метод для ручного обновления
    fun refresh() {
        observeFavouriteTracks()
    }
}
