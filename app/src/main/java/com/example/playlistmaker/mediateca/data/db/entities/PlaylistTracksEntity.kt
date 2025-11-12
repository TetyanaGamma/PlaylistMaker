package com.example.playlistmaker.mediateca.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Трек в плейлисте с прямой связью One-to-Many
@Entity(
    tableName = "tracks_in_playlists_table",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playlistId"])]
)
data class PlaylistTracksEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trackId: Int, //Id
    val playlistId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?, //Альбом
    val releaseDate: String?,  //Дата выхода песни
    val primaryGenreName: String?,//Жанр
    val country: String?, //Страна
    val previewUrl: String?, //Ссылка на аудио
    val isFavourite: Boolean, //Избранное
    val addedTimestamp: Long = System.currentTimeMillis() // для сортировки по времени добавления
)
