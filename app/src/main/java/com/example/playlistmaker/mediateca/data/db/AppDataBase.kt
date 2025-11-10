package com.example.playlistmaker.mediateca.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistDao
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.mediateca.data.db.dao.TrackDao
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistEntity
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistTracksEntity
import com.example.playlistmaker.mediateca.data.db.entities.TrackEntity

@Database(
    version = 4, // новая версия
    entities = [TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTracksEntity::class],
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTracksDao(): PlaylistTracksDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "playlist_maker_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}


