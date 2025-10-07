package com.example.playlistmaker.mediateca.di

import androidx.room.Room
import com.example.playlistmaker.mediateca.data.db.AppDataBase
import com.example.playlistmaker.mediateca.data.db.converters.TrackDbConverter
import com.example.playlistmaker.mediateca.data.repositoryImpl.FavouriteTracksRepositoryImpl
import com.example.playlistmaker.mediateca.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.mediateca.domain.interactors.FavouriteTracksInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            "playlist_maker_database"
        ).build()
    }

    // DAO
    single { get<AppDataBase>().trackDao() }

    // Конвертер
    single { TrackDbConverter() }

    // Репозиторий
    single<FavouriteTracksRepository> {
        FavouriteTracksRepositoryImpl(
            database = get(),
            converter = get()
        )
    }

    single { FavouriteTracksInteractor(get()) }


}