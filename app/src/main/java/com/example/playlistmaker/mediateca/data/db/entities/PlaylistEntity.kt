package com.example.playlistmaker.mediateca.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescr: String,
    val playlistCoverUrl: String, //путь к изображению обложки альбома
    val trackIdsJson: String = "[]", // пустой список треков, хранится как JSON
    val trackCount: Int = 0          // количество треков в плейлисте
) {
    fun getTrackIds(): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(trackIdsJson, type)
    }

    fun setTrackIds(trackIds: List<Int>): PlaylistEntity {
        return copy(trackIdsJson = Gson().toJson(trackIds), trackCount = trackIds.size)
    }
}
