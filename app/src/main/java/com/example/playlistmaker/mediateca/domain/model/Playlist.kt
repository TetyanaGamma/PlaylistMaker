package com.example.playlistmaker.mediateca.domain.model



data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescr: String? = null,
    val playlistCoverUrl: String? = null,
    val trackIds: List<Int> = emptyList(),
    val trackCount: Int,
    val createdTimestamp: Long = System.currentTimeMillis()
)


