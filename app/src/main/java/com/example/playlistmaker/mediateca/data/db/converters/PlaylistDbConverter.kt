package com.example.playlistmaker.mediateca.data.db.converters

import com.example.playlistmaker.mediateca.data.db.entities.PlaylistEntity
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {

    private val gson = Gson()

    fun mapPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescr = playlist.playlistDescr,
            playlistCoverUrl = playlist.playlistCoverUrl,
            trackIdsJson = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackIds.size
        )
    }

    fun mapEntityToPlaylist(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<String>>() {}.type
        val trackIds: List<String> = gson.fromJson(entity.trackIdsJson, type) ?: emptyList()
        return Playlist(
            playlistId = entity.playlistId,
            playlistName = entity.playlistName,
            playlistDescr = entity.playlistDescr,
            playlistCoverUrl = entity.playlistCoverUrl,
            trackIds = trackIds
        )
    }
}
