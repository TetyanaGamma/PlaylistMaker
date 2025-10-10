package com.example.playlistmaker.mediateca.di

import com.example.playlistmaker.mediateca.data.db.AppDataBase
import com.example.playlistmaker.mediateca.data.repositoryImpl.FavouriteTracksRepositoryImpl
import com.example.playlistmaker.mediateca.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.mediateca.domain.interactors.FavouriteTracksInteractor
import com.example.playlistmaker.mediateca.ui.FavoriteTracksViewModel
import com.example.playlistmaker.mediateca.ui.MediatekaViewModel
import com.example.playlistmaker.mediateca.ui.PlaylistsViewModel
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

    // Interactor
    factory {
        FavouriteTracksInteractor(get())
    }

    // ViewModels
    viewModel { MediatekaViewModel() }
    viewModel { PlaylistsViewModel() }
    viewModel { FavoriteTracksViewModel(interactor = get()) }

}