package com.example.playlistmaker.settings.data.storage

import android.content.Context
import android.content.SharedPreferences

import com.example.playlistmaker.search.data.StorageClient

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val THEME_BOOLEAN_KEY = "theme_boolean"

class ThemeStorageClient(context: Context) : StorageClient<Boolean> {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

    override fun storeData(data: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_BOOLEAN_KEY, data).apply()
    }

    override fun getData(): Boolean {
        return sharedPrefs.getBoolean(THEME_BOOLEAN_KEY, false)
    }
}