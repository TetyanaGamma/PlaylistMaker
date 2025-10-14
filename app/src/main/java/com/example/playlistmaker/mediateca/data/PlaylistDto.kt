package com.example.playlistmaker.mediateca.data

data class PlaylistDto(
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescr: String,
    val playlistCoverUrl: String,
    val trackIds: List<String> = emptyList()
)