package com.example.playlistmaker.search.ui.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: TrackViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.dotDivider
        binding.trackDetails
    }

    fun bind(model: Track) {
        val radiusInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2f,
            binding.root.resources.displayMetrics
        ).toInt()

        Glide.with(binding.root)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(radiusInPx))
            .into(binding.trackImage)

        binding.trackName.text = model.trackName
        binding.trackArtist.text = model.artistName
        binding.trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
    }

    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackViewBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}