package com.example.playlistmaker.mediateca.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediateca.data.db.entities.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    //добавлениу трека в таблицу с избранными треками;
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    //удаление трека из таблицы избранных треков
    @Query("DELETE FROM favourite_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)

    //получение списка со всеми треками, добавленными в избранное
    @Query("SELECT * FROM favourite_tracks_table ORDER BY addedTimestamp DESC")
    fun getAll(): Flow<List<TrackEntity>>

    //получение списка идентификаторов всех треков, которые добавлены в избранное
    @Query("SELECT trackId FROM favourite_tracks_table")
    fun getFavouriteTrackIds(): Flow<List<Int>>
}