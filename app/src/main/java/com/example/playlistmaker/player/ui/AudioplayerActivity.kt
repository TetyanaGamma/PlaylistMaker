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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.AudioplayerInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var interactor: AudioplayerInteractor
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var trackTimeTextView: TextView
    private lateinit var currentTrack: Track

    private val handler = Handler(Looper.getMainLooper())
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audioplayer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioPlayer_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        interactor = Creator.provideAudioplayerInteractor()
        currentTrack = intent.getParcelableExtra(SearchActivity.Companion.TRACK_EXTRA)!!

        initUi()
        bindTrackData(currentTrack)
        preparePlayer(currentTrack.previewUrl)
    }

    private fun initUi() {
        playButton = findViewById(R.id.ib_Play_Stop)
        pauseButton = findViewById(R.id.ib_Pause)
        trackTimeTextView = findViewById(R.id.track_TrackTime)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            interactor.stopPlayer()
            finish()
        }

        playButton.setOnClickListener { startPlayer() }
        pauseButton.setOnClickListener { pausePlayer() }
    }

    private fun bindTrackData(track: Track) {
        val radiusInPx = (8f * resources.displayMetrics.density).toInt()
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .transform(RoundedCorners(radiusInPx))
            .placeholder(R.drawable.placeholder)
            .into(findViewById<ImageView>(R.id.track_cover))

        findViewById<TextView>(R.id.track_TrackName).text = track.trackName
        findViewById<TextView>(R.id.track_ArtistName).text = track.artistName
        findViewById<TextView>(R.id.track_DurationValue).text =
            dateFormat.format(track.trackTimeMillis)
        findViewById<TextView>(R.id.track_СollectionNameValue).text = track.collectionName
        findViewById<TextView>(R.id.track_ReleaseDateValue).text = getReleaseYear(track.releaseDate)
        findViewById<TextView>(R.id.track_PrimaryGenreNameValue).text = track.primaryGenreName
        findViewById<TextView>(R.id.track_CountryValue).text = track.country
    }

    private fun preparePlayer(previewUrl: String?) {
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
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.releasePlayer()
    }

    private fun getReleaseYear(date: String?): String {
        return if (!date.isNullOrBlank() && date.length >= 4) date.substring(0, 4)
        else getString(R.string.unknown_date)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val UPDATE_TRACK_TIME_DELAY = 500L
    }
}