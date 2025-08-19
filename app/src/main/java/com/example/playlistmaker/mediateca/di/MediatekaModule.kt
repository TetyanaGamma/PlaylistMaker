package com.example.playlistmaker.mediateca.di

import com.example.playlistmaker.mediateca.ui.FavoriteTracksViewModel
import com.example.playlistmaker.mediateca.ui.MediatekaViewModel
import com.example.playlistmaker.mediateca.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediatekaModule = module {

    viewModel { MediatekaViewModel() }
    viewModel { PlaylistsViewModel() }
    viewModel { FavoriteTracksViewModel() }

}