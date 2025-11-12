package com.example.playlistmaker.mediateca.di

import com.example.playlistmaker.media.ui.screens.OpenPlaylistViewModel
import com.example.playlistmaker.mediateca.data.db.AppDataBase
import com.example.playlistmaker.mediateca.data.repositoryImpl.FavouriteTracksRepositoryImpl
import com.example.playlistmaker.mediateca.data.repositoryImpl.PlaylistRepositoryimpl
import com.example.playlistmaker.mediateca.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.mediateca.domain.api.PlaylistRepository
import com.example.playlistmaker.mediateca.domain.interactors.FavouriteTracksInteractor
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import com.example.playlistmaker.mediateca.ui.screens.FavoriteTracksViewModel
import com.example.playlistmaker.mediateca.ui.screens.MediatekaViewModel
import com.example.playlistmaker.mediateca.ui.screens.PlaylistCreationViewModel
import com.example.playlistmaker.mediateca.ui.screens.PlaylistEditViewModel
import com.example.playlistmaker.mediateca.ui.screens.PlaylistsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediatekaModule = module {

    // Repository
    single<FavouriteTracksRepository> {
        FavouriteTracksRepositoryImpl(
            trackDao = get<AppDataBase>().trackDao(),
            converter = get()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryimpl(
            playlistDao = get<AppDataBase>().playlistDao(),
            converter = get(),
            playlistTracksDao = get<AppDataBase>().playlistTracksDao(),
            playlistTrackDataConverter = get(),
            context = androidContext(),
            trackDao = get<AppDataBase>().trackDao(),
        )
    }

    // Interactor
    single { FavouriteTracksInteractor(get()) }
    single { PlaylistInteractor(get()) }


    // ViewModels
    viewModel { MediatekaViewModel() }
    viewModel { PlaylistsViewModel(interactor = get()) }
    viewModel { FavoriteTracksViewModel(interactor = get()) }
    viewModel { PlaylistCreationViewModel(interactor = get()) }
    viewModel { OpenPlaylistViewModel( application = androidApplication(),
        playlistInteractor = get()) }
    viewModel { PlaylistEditViewModel(interactor = get()) }

}