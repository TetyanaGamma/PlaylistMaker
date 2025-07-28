package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.network.AudioplayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.SettingsRepositoryImpl
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.AudioplayerInteractor
import com.example.playlistmaker.domain.api.AudioplayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.AudioplayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPrefs = context.getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(sharedPrefs)
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val prefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        val repository = SearchHistoryRepositoryImpl(prefs)
        return SearchHistoryInteractorImpl(repository)
    }

    private var audioplayerRepository: AudioplayerRepository? = null

    fun getAudioplayerRepository(): AudioplayerRepository {
        if (audioplayerRepository == null) {
            audioplayerRepository = AudioplayerRepositoryImpl()
        }
        return audioplayerRepository!!
    }

    fun provideAudioplayerInteractor(): AudioplayerInteractor {
        return AudioplayerInteractorImpl(getAudioplayerRepository())
    }
}