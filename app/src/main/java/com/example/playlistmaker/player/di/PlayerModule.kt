package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioplayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.AudioplayerRepository
import com.example.playlistmaker.player.domain.interactor.AudioplayerInteractor
import com.example.playlistmaker.player.domain.interactor.AudioplayerInteractorImpl
import com.example.playlistmaker.player.ui.AudioplayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory<() -> MediaPlayer> {
        { MediaPlayer() }
    }

    single<AudioplayerRepository> {
        AudioplayerRepositoryImpl(mediaPlayerFactory = get())
    }

    factory<AudioplayerInteractor> {
        AudioplayerInteractorImpl(audioplayerRepository = get())
    }

    viewModel { (track: Track) ->
        AudioplayerViewModel(
            audioplayerInteractor = get(),
            track = track
        )
    }
}