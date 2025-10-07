package com.example.playlistmaker.mediateca.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.mediateca.db.dao.TrackDao
import com.example.playlistmaker.mediateca.db.entities.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDataBase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
}