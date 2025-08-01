package com.example.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repositoryImp.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.repositoryImpl.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.network.TrackApi
import com.example.playlistmaker.search.data.repositoryImp.TracksRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.AudioplayerInteractor
import com.example.playlistmaker.player.domain.api.AudioplayerRepository
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.player.domain.interactor.AudioplayerInteractorImpl
import com.example.playlistmaker.search.data.storage.PrefsStorageClient
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractorImpl
import com.example.playlistmaker.settings.data.storage.ThemeStorageClient
import com.example.playlistmaker.sharing.data.repositoryImpl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Creator {

    private const val BASE_URL = "https://itunes.apple.com"

    private fun createRetrofit(): Retrofit {
     return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    private fun createTrackApi(): TrackApi {
        return createRetrofit().create(TrackApi::class.java)
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {

        val trackApi = createTrackApi()
        val networkClient = RetrofitNetworkClient( trackApi)
        val tracksRepository = TracksRepositoryImpl(networkClient)

        val storageClient = PrefsStorageClient<ArrayList<Track>>(
            context,
            "HISTORY",
            type = object : TypeToken<ArrayList<Track>>() {}.type
        )
        val historyRepository = SearchHistoryRepositoryImpl(storageClient)

        return SearchInteractorImpl( tracksRepository,
           historyRepository)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val themeStorage = ThemeStorageClient(
             context
        )
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(themeStorage)
        )
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(context)
    }


    // MediaPlayer фабрика
    private fun createMediaPlayerFactory(): () -> MediaPlayer {
        return { MediaPlayer() }
    }

    private var audioplayerRepository: AudioplayerRepository? = null

    private fun getAudioplayerRepository(): AudioplayerRepository {
        if (audioplayerRepository == null) {
            val mediaPlayerFactory = createMediaPlayerFactory()
            audioplayerRepository = AudioplayerRepositoryImpl(mediaPlayerFactory)
        }
        return audioplayerRepository!!
    }


    fun provideAudioplayerInteractor(): AudioplayerInteractor {
        return AudioplayerInteractorImpl(getAudioplayerRepository())
    }
}