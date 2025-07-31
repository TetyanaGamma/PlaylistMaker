package com.example.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker.search.data.RetrofitNetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.TrackApi
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import com.example.playlistmaker.player.domain.AudioplayerRepository
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.player.domain.AudioplayerInteractorImpl
import com.example.playlistmaker.search.data.storage.PrefsStorageClient
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.SearchInteractorImpl
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.sharing.data.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val THEME_BOOLEAN_KEY = "theme_boolean"

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
  /*  private fun getTracksRepository(): TracksRepository {
        val trackApi = createTrackApi()
        val networkClient = RetrofitNetworkClient(trackApi)
        return TracksRepositoryImpl(networkClient)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }*/

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
        val sharedPrefs = context.getSharedPreferences(
            PLAYLIST_MAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(sharedPrefs)
        )
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(context)
    }

    /*   fun getSearchHistoryRepository(context: Context): SearchHistoryRepository{
           return SearchHistoryRepositoryImpl(PrefsStorageClient<ArrayList<Track>>(
               context,
               "HISTORY",
               object : TypeToken<ArrayList<Track>>() {}.type
           ))
       }

       fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
           return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
       }*/

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