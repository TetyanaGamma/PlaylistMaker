package com.example.playlistmaker.player.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class AudioplayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private val currentTrack: Track by lazy {
        requireArguments().getParcelable<Track>(TRACK_EXTRA)!!
    }

    private val viewModel: AudioplayerViewModel by viewModel {
        parametersOf(currentTrack)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = currentTrack
        if (track == null) {
            // Если почему-то пришли без данных — возвращаемся назад
            findNavController().popBackStack()
            return
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
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

        viewModel.observeProgressTime().observe(viewLifecycleOwner) { time ->
            binding.trackTrackTime.text = time
        }

        // Observer для состояния избранного
        viewModel.isFavourite.observe(viewLifecycleOwner) { isFavorite ->
            val favoriteIcon = if (isFavorite) {
                if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_YES
                ) {
                    R.drawable.favourite_filled_dark
                } else {
                    R.drawable.favourite_filled //Красное сердечко
                }

            } else {
                if (resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                ) {
                    R.drawable.favourite_dark
                } else {
                    R.drawable.favourite_light
                }
            }
            binding.ibFavorite.setImageResource(favoriteIcon)
        }

        initUi()
        bindTrackData(currentTrack)

    }

    private fun initUi() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.ibPlayStop.setOnClickListener { viewModel.onPlayButtonClicked() }
        binding.ibPause.setOnClickListener { viewModel.onPause() }
    }

    private fun bindTrackData(track: Track) {

        viewModel.setTrack(currentTrack)

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

        // Настройка кнопки избранного
        binding.ibFavorite.setOnClickListener {
            viewModel.onFavoruiteClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getReleaseYear(date: String?): String {
        return if (!date.isNullOrBlank() && date.length >= 4) date.substring(0, 4)
        else getString(R.string.unknown_date)
    }

    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"

        fun createArgs(track: Track): Bundle =
            bundleOf(AudioplayerFragment.TRACK_EXTRA to track)

        fun newInstance(trackJson: String): AudioplayerFragment {
            return AudioplayerFragment().apply {
                arguments = Bundle().apply {
                    putString(TRACK_EXTRA, trackJson)
                }
            }
        }
    }


}