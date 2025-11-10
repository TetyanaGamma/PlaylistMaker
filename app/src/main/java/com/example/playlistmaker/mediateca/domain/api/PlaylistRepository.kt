package com.example.playlistmaker.mediateca.domain.api

import android.net.Uri
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {


    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImageUri: Uri?
    ): Long

    suspend fun saveCoverImage(uri: Uri?): Uri

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlistId: Int)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistById(playlistId: Int): Playlist?

    suspend fun addTrackToPlaylist(playlistId: Int, track: Track)

    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int)

    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean

    suspend fun getTracksForPlaylist(playlistId: Int): List<Track>

    suspend fun updatePlaylistTrackCount(playlistId: Int, newCount: Int)

    suspend fun updatePlaylistTrackIds(playlistId: Int, trackIds: List<Int>)

    suspend fun getTracksByIds(trackIds: List<Int>): List<Track>

}