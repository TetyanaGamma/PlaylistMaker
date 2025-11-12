package com.example.playlistmaker.mediateca.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, onClick: (Playlist) -> Unit) {
        binding.playlistName.text = playlist.playlistName
        binding.playlistTracksCount.text = "${playlist.trackCount} треков"

        val coverPath = playlist.playlistCoverUrl

        val model = if (coverPath.startsWith("android.resource://") || File(coverPath).exists()) {
            // Если картинка в базе (локальном хранилище) или ресурс
            if (coverPath.startsWith("android.resource://")) coverPath else File(coverPath)
        } else {
            // Плейсхолдер
            R.drawable.placeholder
        }
        Glide.with(binding.root.context)
            .load(model)
            .centerCrop() // только обрезка, без RoundedCorners
            .placeholder(R.drawable.placeholder)
            .into(binding.playlistImage)

        binding.root.setOnClickListener { onClick(playlist) }
    }
}