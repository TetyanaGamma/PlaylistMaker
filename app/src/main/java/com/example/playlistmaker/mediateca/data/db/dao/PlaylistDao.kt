package com.example.playlistmaker.mediateca.data.db.dao

import androidx.room.*
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY createdTimestamp DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity?

    @Query("DELETE FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    // Flow версия для наблюдения за плейлистом
    @Query("SELECT * FROM playlists_table WHERE playlistId = :playlistId")
    fun getPlaylistByIdFlow(playlistId: Int): Flow<PlaylistEntity?>
}
