package com.example.playlistmaker.mediateca.data.repositoryImpl

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.mediateca.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.mediateca.data.db.converters.PlaylistTrackDataConverter
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistDao
import com.example.playlistmaker.mediateca.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.mediateca.data.db.dao.TrackDao
import com.example.playlistmaker.mediateca.data.db.entities.PlaylistTracksEntity
import com.example.playlistmaker.mediateca.domain.api.PlaylistRepository
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.InputStream

class PlaylistRepositoryimpl(
    private val context: Context,
    private val playlistDao: PlaylistDao,
    private val converter: PlaylistDbConverter,
    private val playlistTrackDataConverter:  PlaylistTrackDataConverter,
    private val playlistTracksDao: PlaylistTracksDao,
    private val trackDao: TrackDao
) : PlaylistRepository {

    // --- Получение всех плейлистов ---
    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                val trackIds = playlistTracksDao.getTrackIdsByPlaylistId(entity.playlistId)
                converter.mapEntityToPlaylist(entity, trackIds)
            }
        }
    }


    // Создание нового плейлиста
    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImageUri: Uri?
    ): Long {
        val playlist = Playlist(
            playlistId = 0,
            playlistName = name,
            playlistDescr = description ?: "",
            playlistCoverUrl = coverImageUri?.toString() ?: "",
            trackIds = emptyList(),
            trackCount = 0
        )
        val entity = converter.mapPlaylistToEntity(playlist)
        return playlistDao.insertPlaylist(entity)
    }

    // Обновление плейлиста
    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(converter.mapPlaylistToEntity(playlist))
    }

    override suspend fun saveCoverImage(uri: Uri?): Uri {
        if (uri == null) return Uri.EMPTY

        val file = File(context.filesDir, "playlist_${System.currentTimeMillis()}.jpg")
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        file.outputStream().use { output -> inputStream?.copyTo(output) }
        return Uri.fromFile(file)
    }

    // Удаление плейлиста
    override suspend fun deletePlaylist(playlistId: Int) {
        // Удаляем сам плейлист
        playlistDao.deletePlaylist(playlistId)
        // Удаляем все его треки
        playlistTracksDao.deleteAllTracksFromPlaylist(playlistId)
    }

    // Получение плейлиста по ID
    override suspend fun getPlaylistById(id: Int): Playlist? {
        val entity = playlistDao.getPlaylistById(id) ?: return null
        val trackIds = playlistTracksDao.getTrackIdsByPlaylistId(id)
        return converter.mapEntityToPlaylist(entity, trackIds)
    }

    // Добавление трека в плейлист
    override suspend fun addTrackToPlaylist(playlistId: Int, track: Track) {
        // Проверяем, есть ли трек в плейлисте
        if (playlistTracksDao.isTrackInPlaylist(playlistId, track.trackId)) return

        val trackEntity = PlaylistTracksEntity(
            playlistId = playlistId,
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

        // Обновляем данные плейлиста: количество треков и список ID
        val trackIds = playlistTracksDao.getTrackIdsByPlaylistId(playlistId)
        updatePlaylistTrackIds(playlistId, trackIds)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        // Удаляем трек из плейлиста
        playlistTracksDao.removeTrackFromPlaylist(playlistId, trackId)

        // Проверяем, используется ли трек в других плейлистах
        val playlistsCount = playlistTracksDao.countPlaylistsWithTrack(trackId)

        // Если трек больше не используется ни в одном плейлисте - удаляем его из таблицы
        if (playlistsCount == 0) {
            // Очищаем треки, которые больше не используются ни в одном плейлисте
            playlistTracksDao.cleanupUnusedTracks()
        }
        // Получаем актуальное количество треков после удаления
        val newCount = playlistTracksDao.getTracksCount(playlistId)

        // Обновляем счетчик треков в плейлисте
        updatePlaylistTrackCount(playlistId, newCount)
    }

    // Проверка наличия трека в плейлисте
    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean {
        return playlistTracksDao.isTrackInPlaylist(playlistId, trackId)
    }

    // Обновление списка треков (ID + счётчик)
    override suspend fun updatePlaylistTrackIds(playlistId: Int, trackIds: List<Int>) {
        val playlist = getPlaylistById(playlistId) ?: return
        val updated = playlist.copy(
            trackIds = trackIds,
            trackCount = trackIds.size
        )
        updatePlaylist(updated)
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()

        val allTracks = playlistTracksDao.getAllTracks()
        val favoriteTrackIds =trackDao.getFavouriteTrackIds().first()

        return allTracks
            .filter { it.trackId in trackIds }
            .map { entity ->
                playlistTrackDataConverter.mapEntityToTrack(
                    entity,
                    isFavorite = entity.trackId in favoriteTrackIds
                )
            }
    }


    // Получение треков для конкретного плейлиста
    override suspend fun getTracksForPlaylist(playlistId: Int): List<Track> {
        return playlistTracksDao.getTracksByPlaylistId(playlistId).map {
            Track(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                artworkUrl100 = it.artworkUrl100,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl,
                isFavourite = it.isFavourite
            )
        }
    }

    override suspend fun updatePlaylistTrackCount(playlistId: Int, newCount: Int) {
        val playlist = playlistDao.getPlaylistById(playlistId) ?: return
        val updatedPlaylist = playlist.copy(trackCount = newCount)
        playlistDao.updatePlaylist(updatedPlaylist)
    }
}