package com.example.playlistmaker.mediateca.domain.model

data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescr: String,
    val playlistCoverUrl: String,
    val trackIds: List<String> = emptyList()
) {
    val trackCount: Int
        get() = trackIds.size
}

