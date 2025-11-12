package com.example.playlistmaker.mediateca.data.db.converters

import com.example.playlistmaker.mediateca.data.db.entities.PlaylistEntity
import com.example.playlistmaker.mediateca.domain.model.Playlist


class PlaylistDbConverter {

    fun mapPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescr = playlist.playlistDescr,
            playlistCoverUrl = playlist.playlistCoverUrl,
            trackCount = playlist.trackCount,
            createdTimestamp = playlist.createdTimestamp
        )
    }

    fun mapEntityToPlaylist(entity: PlaylistEntity, trackIds: List<Int> = emptyList()): Playlist {

        return Playlist(
            playlistId = entity.playlistId,
            playlistName = entity.playlistName,
            playlistDescr = entity.playlistDescr,
            playlistCoverUrl = entity.playlistCoverUrl,
            trackIds = trackIds,
            trackCount = entity.trackCount,
            createdTimestamp = entity.createdTimestamp
        )
    }
}
