package com.example.playlistmaker.player.ui.adapters

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlatlistItemBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist

class BottomSheetPlaylistViewHolder(private val binding: PlatlistItemBinding) :
RecyclerView.ViewHolder(binding.root){

    fun bind(playlist: Playlist, onClick: (Playlist) -> Unit) {
        val radiusInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2f,
            binding.root.resources.displayMetrics
        ).toInt()

        Glide.with(binding.root.context)
            .load(playlist.playlistCoverUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(radiusInPx))
            .into(binding.playlistImageBottomSheet)

        binding.playlistNameBottonSheet.text = playlist.playlistName
        binding.trackCount.text = "${playlist.trackCount} треков"

        binding.root.setOnClickListener { onClick(playlist) }
    }
}