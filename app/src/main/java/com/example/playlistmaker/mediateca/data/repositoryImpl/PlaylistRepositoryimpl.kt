package com.example.playlistmaker.mediateca.data.repositoryImpl

import com.example.playlistmaker.mediateca.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistDao
import com.example.playlistmaker.mediateca.domain.api.PlaylistRepository
import com.example.playlistmaker.mediateca.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryimpl(
    private val playlistDao: PlaylistDao,
    private val converter: PlaylistDbConverter
): PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { entities ->
                entities.map { converter.mapEntityToPlaylist(it) }
            }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val entity = converter.mapPlaylistToEntity(playlist)
        playlistDao.insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(converter.mapPlaylistToEntity(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(converter.mapPlaylistToEntity(playlist))
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
      return  playlistDao.getPlaylistById(id)?.let { converter.mapEntityToPlaylist(it) }
    }

}