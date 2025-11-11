package com.example.playlistmaker.search.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackViewHolder

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()
    private var listener: OnTrackClicklistener? = null
    private var longClickListener: OnTrackLongClickListener? = null

    fun interface OnTrackClicklistener {
        fun onTrackClick(track: Track)
    }

    fun interface OnTrackLongClickListener {
        fun onTrackLongClick(track: Track)
    }

    fun setOnTrackClickListener(listener: OnTrackClicklistener) {
        this.listener = listener
    }

    fun setOnTrackLongClickListener(listener: OnTrackLongClickListener) {
        this.longClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : TrackViewHolder = TrackViewHolder.Companion.from(parent)

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        // Обработка клика по элементу
        holder.itemView.setOnClickListener {
            listener?.onTrackClick(tracks[position])
        }
        holder.itemView.setOnLongClickListener {
            longClickListener?.onTrackLongClick(tracks[position])
            true
        }
    }
}