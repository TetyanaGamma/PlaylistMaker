package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TrackViewHolder

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>()  {

       var tracks = ArrayList<Track>()
    private  var listener: OnTrackClicklistener? = null

    interface OnTrackClicklistener {
        fun onTrackClick(track: Track)
    }
    fun setOnTrackClickListener(listener: OnTrackClicklistener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        // Обработка клика по элементу
      holder.itemView.setOnClickListener {
          listener?.onTrackClick(tracks[position])
      }
    }



}