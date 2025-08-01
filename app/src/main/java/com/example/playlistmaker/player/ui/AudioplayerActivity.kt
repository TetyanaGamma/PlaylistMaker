package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var viewModel: AudioplayerViewModel
    private lateinit var binding: ActivityAudioplayerBinding

  //  private lateinit var interactor: AudioplayerInteractor
 /*   private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var trackTimeTextView: TextView*/
    private lateinit var currentTrack: Track

 /*   private val handler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    // целочисленная переменная, в которой хранится текущее состояние медиаплейера
    private var playerState = STATE_DEFAULT

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (interactor.isPlaying()) {
                trackTimeTextView.text = dateFormat.format(interactor.getCurrentPosition())
                handler.postDelayed(this, UPDATE_TRACK_TIME_DELAY)
            }
        }
    } */

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
     //   interactor = Creator.provideAudioplayerInteractor()
        currentTrack = intent.getParcelableExtra(SearchActivity.TRACK_EXTRA)!!
         //   ?: throw IllegalStateException("Track data is missing in intent")

        viewModel = ViewModelProvider(
            this,
            AudioplayerViewModel.getFactory(
                Creator.provideAudioplayerInteractor(),
                currentTrack.previewUrl.toString()
            )
        )[AudioplayerViewModel::class.java]

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
        //preparePlayer(currentTrack.previewUrl)
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
        binding.trackDurationValue.text =track.trackTimeMillis.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        }
       binding.trackOllectionNameValue.text = track.collectionName
        binding.trackReleaseDateValue.text = getReleaseYear(track.releaseDate)
        binding.trackPrimaryGenreNameValue.text = track.primaryGenreName
        binding.trackCountryValue.text = track.country
    }

/*    private fun preparePlayer(previewUrl: String?) {
        previewUrl?.let {
            interactor.preparePlayer(it, onPrepared = {
                playButton.isEnabled = true
            }, onCompletion = {
                playButton.visibility = ImageButton.VISIBLE
                pauseButton.visibility = ImageButton.INVISIBLE
                trackTimeTextView.text = getString(R.string.track_progress_time)
                handler.removeCallbacks(updateTimeRunnable)
            })
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
        playButton.visibility = ImageButton.INVISIBLE
        pauseButton.visibility = ImageButton.VISIBLE
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        interactor.pausePlayer()
        playButton.visibility = ImageButton.VISIBLE
        pauseButton.visibility = ImageButton.INVISIBLE
        handler.removeCallbacks(updateTimeRunnable)
    }*/

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

 /*   companion object {
        private const val STATE_DEFAULT = 0
        private const val UPDATE_TRACK_TIME_DELAY = 500L
    }*/
}