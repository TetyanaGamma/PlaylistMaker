package com.example.playlistmaker.mediateca.domain.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescr: String,
    val playlistCoverUrl: String,
    val trackIdsJson: String = "[]",
    val trackCount: Int = 0
) {
    val trackIds: List<Int>
        get() {
            return try {
                val type = object : TypeToken<List<Int>>() {}.type
                Gson().fromJson(trackIdsJson, type)
            } catch (e: Exception) {
                emptyList()
            }
        }
}

