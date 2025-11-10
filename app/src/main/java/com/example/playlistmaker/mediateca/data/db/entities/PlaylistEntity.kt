package com.example.playlistmaker.mediateca.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescr: String,
    val playlistCoverUrl: String, //путь к изображению обложки альбома
           // количество треков в плейлисте
    val trackCount: Int = 0,
    val createdTimestamp: Long = System.currentTimeMillis()
)
