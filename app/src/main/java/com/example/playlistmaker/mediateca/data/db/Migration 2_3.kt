package com.example.playlistmaker.mediateca.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS tracks_in_playlists_table (
                trackId INTEGER PRIMARY KEY NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT,
                releaseDate TEXT,
                primaryGenreName TEXT,
                country TEXT,
                previewUrl TEXT,
                isFavourite INTEGER NOT NULL,
                addedTimestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}
