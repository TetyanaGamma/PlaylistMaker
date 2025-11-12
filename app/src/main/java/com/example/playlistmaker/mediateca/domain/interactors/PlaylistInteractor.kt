package com.example.playlistmaker.mediateca.domain.interactors

import com.example.playlistmaker.mediateca.domain.api.PlaylistRepository
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractor(
    private val playlistRepository: PlaylistRepository
) {
    fun getAllPlaylists(): Flow<List<Playlist>> {
    return    playlistRepository.getAllPlaylists()
    }
    suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }
    suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }
    suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }
    suspend fun getPlaylistById(id: Int): Playlist? {
        return playlistRepository.getPlaylistById(id)
    }

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

}