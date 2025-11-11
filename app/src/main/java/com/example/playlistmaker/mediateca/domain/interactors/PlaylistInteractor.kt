package com.example.playlistmaker.mediateca.domain.interactors

import android.net.Uri
import com.example.playlistmaker.mediateca.domain.api.PlaylistRepository
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistInteractor(
    private val playlistRepository: PlaylistRepository
) {
    suspend fun saveCoverImage(coverImageUri: Uri?): Uri {
        return withContext(Dispatchers.IO) {
            playlistRepository.saveCoverImage(coverImageUri)
        }
    }

    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImageUri: Uri?
    ): Result<Long> {
        return try {
            // Валидация названия
            if (name.isBlank()) {
                return Result.failure(IllegalArgumentException("Playlist name cannot be empty"))
            }

            val savedCoverUri = withContext(Dispatchers.IO) {
                playlistRepository.saveCoverImage(coverImageUri)
            }

            val playlistId = playlistRepository.createPlaylist(name, description, savedCoverUri)
            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updatePlaylist(playlist: Playlist): Result<Unit> {
        return try {
            if (playlist.playlistName.isBlank()) {
                return Result.failure(IllegalArgumentException("Playlist name cannot be empty"))
            }

            playlistRepository.updatePlaylist(playlist)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePlaylist(playlistId: Int): Result<Unit> {
        return try {
            playlistRepository.deletePlaylist(playlistId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistRepository.getPlaylistById(playlistId)
    }

    suspend fun addTrackToPlaylist(playlistId: Int, track: Track): Result<Unit> {
        return try {
            // Проверяем, есть ли уже трек в плейлисте
            val isInPlaylist = playlistRepository.isTrackInPlaylist(playlistId, track.trackId)
            if (isInPlaylist) {
                return Result.failure(TrackAlreadyInPlaylistException())
            }

            // Получаем текущий плейлист
            val playlist = playlistRepository.getPlaylistById(playlistId)
                ?: return Result.failure(PlaylistNotFoundException())

            // Добавляем трек
            playlistRepository.addTrackToPlaylist(playlistId, track)

            // Обновляем список ID треков и счетчик
            val updatedTrackIds = playlist.trackIds + track.trackId
            playlistRepository.updatePlaylistTrackIds(playlistId, updatedTrackIds)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)

        // Обновляем UI после удаления
        val updatedTracks = playlistRepository.getTracksForPlaylist(playlistId)
        playlistRepository.updatePlaylistTrackIds(
            playlistId,
            updatedTracks.map { it.trackId }
        )
        playlistRepository.updatePlaylistTrackCount(playlistId, updatedTracks.size)
    }

    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean {
        return playlistRepository.isTrackInPlaylist(playlistId, trackId)
    }

    suspend fun getTracksForPlaylist(playlistId: Int): List<Track> {
        return playlistRepository.getTracksForPlaylist(playlistId)
    }
    suspend fun getPlaylistInfo(playlistId: Int): PlaylistInfo? {
        val playlist = playlistRepository.getPlaylistById(playlistId) ?: return null
        val tracks = playlistRepository.getTracksForPlaylist(playlistId)

        return PlaylistInfo(
            playlist = playlist,
            tracks = tracks,
            totalDuration = tracks.sumOf { it.trackTimeMillis }
        )
    }


}

data class PlaylistInfo(
    val playlist: Playlist,
    val tracks: List<Track>,
    val totalDuration: Long
)

// Исключения для работы с плейлистами

class TrackAlreadyInPlaylistException : Exception("Track already exists in playlist")
class PlaylistNotFoundException : Exception("Playlist not found")