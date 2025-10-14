package com.example.playlistmaker.mediateca.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, onClick: (Playlist) -> Unit) {
        binding.playlistName.text = playlist.playlistName
        binding.playlistTracksCount.text = "${playlist.trackCount} треков"

        Glide.with(binding.root.context)
            .load(playlist.playlistCoverUrl)
            .placeholder(R.drawable.placeholder)
            .into(binding.playlistImage)

        binding.root.setOnClickListener { onClick(playlist) }
    }
}