package com.example.playlistmaker.mediateca.di


import com.example.playlistmaker.mediateca.data.db.AppDataBase
import com.example.playlistmaker.mediateca.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.mediateca.data.db.converters.PlaylistTrackDataConverter
import com.example.playlistmaker.mediateca.data.db.converters.TrackDbConverter
import com.example.playlistmaker.mediateca.data.repositoryImpl.FavouriteTracksRepositoryImpl
import com.example.playlistmaker.mediateca.data.repositoryImpl.PlaylistRepositoryimpl
import com.example.playlistmaker.mediateca.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.mediateca.domain.api.PlaylistRepository
import com.example.playlistmaker.mediateca.domain.interactors.FavouriteTracksInteractor
import com.example.playlistmaker.mediateca.domain.interactors.PlaylistInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<AppDataBase> {
        AppDataBase.getInstance(androidContext())
    }

    // DAO
    single { get<AppDataBase>().trackDao() }
    single { get<AppDataBase>().playlistDao() }
    single { get<AppDataBase>().playlistTracksDao() }

    // Конвертер
    single { TrackDbConverter() }
    single { PlaylistDbConverter() }
    single { PlaylistTrackDataConverter() }

    // Репозиторий
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
            context = androidContext(),
            trackDao = get<AppDataBase>().trackDao(),
            playlistTrackDataConverter = get()
        )
    }

    single { FavouriteTracksInteractor(get()) }
    single { PlaylistInteractor(get()) }

}