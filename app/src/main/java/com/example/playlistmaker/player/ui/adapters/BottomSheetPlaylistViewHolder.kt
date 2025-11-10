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

        val model = when {
            coverPath.isNullOrBlank() -> R.drawable.placeholder
            coverPath.startsWith("android.resource://") -> coverPath
            coverPath.startsWith("content://") || coverPath.startsWith("file://") -> coverPath
            File(coverPath).exists() -> File(coverPath)
            else -> R.drawable.placeholder
        }


        Glide.with(binding.root.context)
            .load(model)
            .centerCrop() // обрезка под размер ImageView
            .placeholder(R.drawable.placeholder)
            .into(binding.playlistImageBottomSheet)

        binding.playlistNameBottonSheet.text = playlist.playlistName
        binding.trackCount.text =    binding.root.context.resources.getQuantityString(
            R.plurals.track_count, playlist.trackCount, playlist.trackCount)

        binding.root.setOnClickListener { onClick(playlist) }
    }
}