package com.example.playlistmaker.mediateca.data.repositoryImpl

import com.example.playlistmaker.mediateca.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistDao
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistTracksEntity
import com.example.playlistmaker.mediateca.domain.api.PlaylistRepository
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryimpl(
    private val playlistDao: PlaylistDao,
    private val converter: PlaylistDbConverter,
    private val playlistTracksDao: PlaylistTracksDao
) : PlaylistRepository {

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
        return playlistDao.getPlaylistById(id)?.let { converter.mapEntityToPlaylist(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val entity = converter.mapPlaylistToEntity(playlist)
        val trackIds = entity.getTrackIds().toMutableList()

        if (trackIds.contains(track.trackId)) {
            return // трек уже есть — ничего не делаем
        }

        // добавляем новый id
        trackIds.add(track.trackId)

        // обновляем entity с новыми данными
        val updatedEntity = entity.setTrackIds(trackIds)

        // сохраняем обновлённый плейлист
        playlistDao.updatePlaylist(updatedEntity)

        // добавляем сам трек в таблицу треков для плейлистов
        val trackEntity = PlaylistTracksEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavourite = track.isFavourite
        )
        playlistTracksDao.insertTrack(trackEntity)
    }


}