package com.example.playlistmaker.mediateca.di

import androidx.room.Room
import com.example.playlistmaker.mediateca.data.db.AppDataBase
import com.example.playlistmaker.mediateca.data.db.MIGRATION_1_2
import com.example.playlistmaker.mediateca.data.db.converters.PlaylistDbConverter
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

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            "playlist_maker_database"
        ) .addMigrations(MIGRATION_1_2)
            .build()
    }

    // DAO
    single { get<AppDataBase>().trackDao() }
    single { get<AppDataBase>().playlistDao() }

    // Конвертер
    single { TrackDbConverter() }
    single { PlaylistDbConverter() }

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
            converter = get()
        )
    }

    single { FavouriteTracksInteractor(get()) }
     single { PlaylistInteractor(get()) }


}