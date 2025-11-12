package com.example.playlistmaker.mediateca.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistTracksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTracksDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTracksEntity): Long

    // ВАЖНО: ORDER BY addedTimestamp DESC - сортировка по убыванию:  Последние добавленные треки будут первыми в списке
    @Query("SELECT * FROM tracks_in_playlists_table WHERE playlistId = :playlistId ORDER BY addedTimestamp DESC")
    suspend fun getTracksByPlaylistId(playlistId: Int): List<PlaylistTracksEntity>

    @Query("SELECT trackId FROM tracks_in_playlists_table WHERE playlistId = :playlistId")
    suspend fun getTrackIdsByPlaylistId(playlistId: Int): List<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM tracks_in_playlists_table WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean

    @Query("DELETE FROM tracks_in_playlists_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int)

    @Query("SELECT COUNT(*) FROM tracks_in_playlists_table WHERE playlistId = :playlistId")
    suspend fun getTracksCount(playlistId: Int): Int

    @Query("DELETE FROM tracks_in_playlists_table WHERE playlistId = :playlistId")
    suspend fun deleteAllTracksFromPlaylist(playlistId: Int)

    // Получение всех треков
    @Query("SELECT * FROM tracks_in_playlists_table")
    suspend fun getAllTracks(): List<PlaylistTracksEntity>

    //  метод для проверки использования трека в других плейлистах
    @Query("SELECT COUNT(DISTINCT playlistId) FROM tracks_in_playlists_table WHERE trackId = :trackId")
    suspend fun countPlaylistsWithTrack(trackId: Int): Int

    // Удаление треков, которые не используются ни в одном плейлисте
    @Query("DELETE FROM tracks_in_playlists_table WHERE trackId IN (SELECT DISTINCT trackId FROM tracks_in_playlists_table GROUP BY trackId HAVING COUNT(DISTINCT playlistId) = 0)")
    suspend fun cleanupUnusedTracks()

}