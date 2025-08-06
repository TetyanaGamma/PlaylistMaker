package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.screen.SearchActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private val currentTrack: Track by lazy {
        intent.getParcelableExtra<Track>(TRACK_EXTRA)!!
    }

    private val viewModel: AudioplayerViewModel by viewModel {
        parametersOf(currentTrack)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioPlayer_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.observePlayerState().observe(this) { state ->
            when (state) {
                AudioplayerViewModel.STATE_PREPARED, AudioplayerViewModel.STATE_PAUSED -> {
                    binding.ibPlayStop.visibility = ImageButton.VISIBLE
                    binding.ibPause.visibility = ImageButton.INVISIBLE
                }

                AudioplayerViewModel.STATE_PLAYING -> {
                    binding.ibPlayStop.visibility = ImageButton.INVISIBLE
                    binding.ibPause.visibility = ImageButton.VISIBLE
                }
            }
            binding.ibPlayStop.isEnabled = state != AudioplayerViewModel.STATE_DEFAULT
        }

        viewModel.observeProgressTime().observe(this) { time ->
            binding.trackTrackTime.text = time
        }


        initUi()
        bindTrackData(currentTrack)
    }

    private fun initUi() {

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.ibPlayStop.setOnClickListener { viewModel.onPlayButtonClicked() }
        binding.ibPause.setOnClickListener { viewModel.onPause() }
    }

    private fun bindTrackData(track: Track) {
        val radiusInPx = (8f * resources.displayMetrics.density).toInt()
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .transform(RoundedCorners(radiusInPx))
            .placeholder(R.drawable.placeholder)
            .into(binding.trackCover)

        binding.trackTrackName.text = track.trackName
        binding.trackArtistName.text = track.artistName
        binding.trackDurationValue.text = track.trackTimeMillis.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        }
        binding.trackOllectionNameValue.text = track.collectionName
        binding.trackReleaseDateValue.text = getReleaseYear(track.releaseDate)
        binding.trackPrimaryGenreNameValue.text = track.primaryGenreName
        binding.trackCountryValue.text = track.country
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun getReleaseYear(date: String?): String {
        return if (!date.isNullOrBlank() && date.length >= 4) date.substring(0, 4)
        else getString(R.string.unknown_date)
    }

    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"
    }

}