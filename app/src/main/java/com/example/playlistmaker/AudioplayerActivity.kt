package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var trackTimeTextView: TextView
    private lateinit var currentTrack: Track

    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    // целочисленная переменная, в которой хранится текущее состояние медиаплейера
    private var playerState = STATE_DEFAULT

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                trackTimeTextView.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, 500)
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

        // Обрабатываем кнопку "Назад" в Toolbar
        val toolbar = findViewById<ImageButton>(R.id.backButton)
        toolbar.setOnClickListener {
            stopPlayer()
            finish()
        }
// Получаем данные с экрана поиска
        val track = intent.getParcelableExtra<Track>(SearchActivity.TRACK_EXTRA)
        if (track != null) {
            currentTrack = track
            preparePlayer(currentTrack)
        }

        val trackCover = findViewById<ImageView>(R.id.track_cover)
        val trackTrackName = findViewById<TextView>(R.id.track_TrackName)
        val trackArtistName = findViewById<TextView>(R.id.track_ArtistName)
        val trackDurationValue = findViewById<TextView>(R.id.track_DurationValue)
        val trackCollectionNameValue = findViewById<TextView>(R.id.track_СollectionNameValue)
        val trackReleaseDateValue = findViewById<TextView>(R.id.track_ReleaseDateValue)
        val trackPrimaryGenreNameValue = findViewById<TextView>(R.id.track_PrimaryGenreNameValue)
        val trackCountryValue = findViewById<TextView>(R.id.track_CountryValue)
        trackTimeTextView = findViewById(R.id.track_TrackTime)

//  Улучшаем разрешениеи качество обложки альбома
        val enlargedImageUrl = track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

        val radiusInPx = (8f * resources.displayMetrics.density).toInt()
// Получаем необходимое изображение, если обложки нет покказываем заглушку
        Glide.with(this)
            .load(enlargedImageUrl)
            .centerCrop()
            .transform(RoundedCorners(radiusInPx))  //  8dp
            .placeholder(R.drawable.placeholder)
            .into(trackCover)

        trackTrackName.text = track?.trackName
        trackArtistName.text = track?.artistName
        trackDurationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        trackCollectionNameValue.text = track?.collectionName
        trackReleaseDateValue.text = track?.releaseDate?.substring(0, 4)
        trackPrimaryGenreNameValue.text = track?.primaryGenreName
        trackCountryValue.text = track?.country

        playButton = findViewById<ImageButton>(R.id.ib_Play_Stop)
        pauseButton = findViewById<ImageButton>(R.id.ib_Pause)

        //    preparePlayer(track)
        setupControls()
    }

    private fun preparePlayer(track: Track?) {
        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateTimeRunnable)
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.INVISIBLE
            pauseButton.isEnabled = false
            playerState = STATE_PREPARED
            trackTimeTextView.text = getString(R.string.track_progress_time)

            // reset плеера и переинициализация
            mediaPlayer.reset()
            preparePlayer(track) // снова подготовить трек для воспроизведения
        }
    }

    private fun startPlayer() {
        if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
            mediaPlayer.start()
            playButton.visibility = View.INVISIBLE
            playButton.isEnabled = false
            pauseButton.visibility = View.VISIBLE
            pauseButton.isEnabled = true
            playerState = STATE_PLAYING
            handler.post(updateTimeRunnable)
        }
    }

    private fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            playButton.visibility = View.VISIBLE
            playButton.isEnabled = true
            pauseButton.visibility = View.INVISIBLE
            pauseButton.isEnabled = false
            playerState = STATE_PAUSED
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    private fun stopPlayer() {
        if (playerState == STATE_PLAYING || playerState == STATE_PAUSED) {
            mediaPlayer.stop()
            playerState = STATE_DEFAULT
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    private fun setupControls() {
        playButton.setOnClickListener { startPlayer() }
        pauseButton.setOnClickListener { pausePlayer() }
    }


    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        mediaPlayer.release()
    }

    // константы для состояния медиаплейера
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}
