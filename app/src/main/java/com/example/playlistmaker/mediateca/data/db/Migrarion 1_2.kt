package com.example.playlistmaker.mediateca.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Создаем таблицу playlists_table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlists_table (
                 playlistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                playlistName TEXT NOT NULL,
                playlistDescr TEXT DEFAULT '',
                playlistCoverUrl TEXT DEFAULT 'placeholder', 
                trackIdsJson TEXT NOT NULL DEFAULT '[]',
               
                trackCount INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }
}