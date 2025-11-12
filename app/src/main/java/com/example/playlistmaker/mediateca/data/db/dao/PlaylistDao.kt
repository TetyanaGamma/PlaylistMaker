package com.example.playlistmaker.mediateca.data.db.dao

import androidx.room.*
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // Получить все плейлисты
    @Query("SELECT * FROM playlists_table ORDER BY playlistId DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    // Получить один плейлист по id
    @Query("SELECT * FROM playlists_table WHERE playlistId = :id")
    suspend fun getPlaylistById(id: Int): PlaylistEntity?

    // Добавить новый плейлист
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    // Обновить плейлист (например, название, описание или список треков)
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    // Удалить плейлист
    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    // --- Методы для работы со списком треков в плейлисте ---

    @Transaction
    suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int) {
        val playlist = getPlaylistById(playlistId) ?: return
        val currentTracks = playlist.getTrackIds().toMutableList()
        if (!currentTracks.contains(trackId)) {
            currentTracks.add(trackId)
            updatePlaylist(playlist.setTrackIds(currentTracks))
        }
    }

    @Transaction
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        val playlist = getPlaylistById(playlistId) ?: return
        val currentTracks = playlist.getTrackIds().toMutableList()
        if (currentTracks.contains(trackId)) {
            currentTracks.remove(trackId)
            updatePlaylist(playlist.setTrackIds(currentTracks))
        }
    }
}
