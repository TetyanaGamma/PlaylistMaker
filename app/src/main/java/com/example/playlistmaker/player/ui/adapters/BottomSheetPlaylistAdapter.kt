package com.example.playlistmaker.player.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlatlistItemBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist

class BottomSheetPlaylistAdapter(
    private var playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit
) :
    RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistViewHolder {
        val binding = PlatlistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomSheetPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BottomSheetPlaylistViewHolder,
        position: Int) {
        holder.bind(playlists[position], onClick)
    }

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = playlists.size

}