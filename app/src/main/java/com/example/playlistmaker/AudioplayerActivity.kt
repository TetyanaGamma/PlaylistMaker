package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {
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
            finish()
        }
// Получаем данные с экрана поиска
        val trackJson = intent.getStringExtra("trackJson")
        val gson = Gson()
        val track: Track = gson.fromJson(trackJson, Track::class.java)

        val trackCover = findViewById<ImageView>(R.id.track_cover)
        val trackTrackName = findViewById<TextView>(R.id.track_TrackName)
        val trackArtistName = findViewById<TextView>(R.id.track_ArtistName)
        val trackDurationValue = findViewById<TextView>(R.id.track_DurationValue)
        val trackCollectionNameValue = findViewById<TextView>(R.id.track_СollectionNameValue)
        val trackReleaseDateValue = findViewById<TextView>(R.id.track_ReleaseDateValue)
        val trackPrimaryGenreNameValue = findViewById<TextView>(R.id.track_PrimaryGenreNameValue)
        val trackCountryValue = findViewById<TextView>(R.id.track_CountryValue)

//  Улучшаем разрешениеи качество обложки альбома
        val enlargedImageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val radiusInPx = (8f * resources.displayMetrics.density).toInt()
// Получаем необходимое изображение, если обложки нет покказываем заглушку
        Glide.with(this)
            .load(enlargedImageUrl)
            .centerCrop()
            .transform(RoundedCorners(radiusInPx))  //  8dp
            .placeholder(R.drawable.placeholder)
            .into(trackCover)

        trackTrackName.text = track.trackName
        trackArtistName.text = track.artistName
        trackDurationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackCollectionNameValue.text = track.collectionName
        trackReleaseDateValue.text = track.releaseDate?.substring(0, 4)
        trackPrimaryGenreNameValue.text = track.primaryGenreName
        trackCountryValue.text = track.country
    }
}
