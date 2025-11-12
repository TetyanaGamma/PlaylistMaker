package com.example.playlistmaker.mediateca.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistTracksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTracksDao {
    //добавление трека в плейлист;
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTracksEntity)

    //удаление трека из плейлиста
    @Query("DELETE FROM tracks_in_playlists_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)

    //получение списка со всеми треками, добавленными в   плейлист
    @Query("SELECT * FROM tracks_in_playlists_table ORDER BY addedTimestamp DESC")
    fun getAll(): Flow<List<PlaylistTracksEntity>>

    //получение списка идентификаторов всех треков, которые добавлены в плейлист
    @Query("SELECT trackId FROM tracks_in_playlists_table")
    fun getPlaylistTrackIds(): Flow<List<Int>>
}