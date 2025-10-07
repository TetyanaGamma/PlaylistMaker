package com.example.playlistmaker.mediateca.di

import androidx.room.Room
import com.example.playlistmaker.mediateca.db.AppDataBase
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
}