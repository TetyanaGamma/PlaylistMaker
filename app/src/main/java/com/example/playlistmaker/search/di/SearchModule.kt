package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TrackApi
import com.example.playlistmaker.search.data.repositoryImp.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repositoryImp.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.PrefsStorageClient
import com.example.playlistmaker.search.data.storage.StorageClient
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.screen.SearchViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    single {
        Gson()
    }

    single <StorageClient<ArrayList<Track>>> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        PrefsStorageClient<ArrayList<Track>>(
            context = get<android.content.Context>(),
            gson = get(),
            dataKey = "HISTORY",
            type = type
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single {
        Retrofit.Builder()
            .baseUrl( "https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(TrackApi::class.java) }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }

    viewModel {
        SearchViewModel(get())
    }


}