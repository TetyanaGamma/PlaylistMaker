package com.example.playlistmaker.mediateca.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.mediateca.data.db.dao.TrackDao
import com.example.playlistmaker.mediateca.data.db.entities.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDataBase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
}