package com.example.playlistmaker.player.ui.adapters

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlatlistItemBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist
import java.io.File

class BottomSheetPlaylistViewHolder(private val binding: PlatlistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, onClick: (Playlist) -> Unit) {
        val coverPath = playlist.playlistCoverUrl

        val model = if (coverPath.startsWith("android.resource://") || File(coverPath).exists()) {
            if (coverPath.startsWith("android.resource://")) coverPath else File(coverPath)
        } else {
            R.drawable.placeholder
        }

        Glide.with(binding.root.context)
            .load(model)
            .centerCrop() // обрезка под размер ImageView
            .placeholder(R.drawable.placeholder)
            .into(binding.playlistImageBottomSheet)

        binding.playlistNameBottonSheet.text = playlist.playlistName
        binding.trackCount.text = "${playlist.trackCount} треков"

        binding.root.setOnClickListener { onClick(playlist) }
    }
}