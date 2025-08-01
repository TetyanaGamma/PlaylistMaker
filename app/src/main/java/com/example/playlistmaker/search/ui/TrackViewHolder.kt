package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: TrackViewBinding)
    : RecyclerView.ViewHolder(binding.root) {




    init {
      //  trackImageView = itemView.findViewById(R.id.track_image)
       // trackNameTextView = itemView.findViewById(R.id.track_name)
      //  trackArtistTextView = itemView.findViewById(R.id.track_artist)
        binding.dotDivider
     //   trackTimeTextView = itemView.findViewById(R.id.track_time)
        binding.trackDetails
    }

    fun bind(model: Track) {
        Glide.with(binding.root)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(binding.trackImage)

        binding.trackName.text = model.trackName
        binding.trackArtist.text = model.artistName
       binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
    }

    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackViewBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}