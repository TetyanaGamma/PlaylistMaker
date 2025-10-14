package com.example.playlistmaker.mediateca.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistDao
import com.example.playlistmaker.mediateca.data.db.dao.TrackDao
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistEntity
import com.example.playlistmaker.mediateca.data.db.entities.TrackEntity

@Database(
    version = 2, // новая версия
    entities = [TrackEntity::class, PlaylistEntity::class]
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}
