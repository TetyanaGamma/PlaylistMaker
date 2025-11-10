package com.example.playlistmaker.mediateca.domain.model


data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescr: String,
    val playlistCoverUrl: String,
    val trackIds: List<Int> = emptyList(),
    val trackCount: Int = trackIds.size,
    val createdTimestamp: Long = System.currentTimeMillis()
)

