package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.player.ui.AudioplayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var mainThreadHandler: Handler

    private val searchRunnable = Runnable { performTrackSearch(searchInput.text.toString()) }
    private var isClickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())
    private val CLICK_DEBOUNCE_DELAY = 1000L


    private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private var searchText: String = SAVED_TEXT  // переменная для хранения введённого текста

    private lateinit var placeholderNoFound: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var listTracks: RecyclerView
    private lateinit var placeholderHistory: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyHeadder: TextView
    private lateinit var historyClearButton: Button
    private lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private var historyAdapter = TrackAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)
        mainThreadHandler = Handler(Looper.getMainLooper())

        initUi()
        initAdapters()
        initListeners()

        restoreSearchText(savedInstanceState)
        updateTrackHistory()

        // Устанавливаем фокус на поле ввода и показываем клавиатуру
        searchInput.post {
            searchInput.requestFocus()
            showKeyBoard()
        }
    }

    private fun initUi() {
        val toolbar: Toolbar = findViewById(R.id.search_toolbar)
        // нажатие на иконку назад
        toolbar.setNavigationOnClickListener {
            finish()
        }
        searchInput = findViewById(R.id.serch_input)
        clearButton = findViewById<ImageView>(R.id.clear_icon)
        updateButton = findViewById(R.id.button_update)
        placeholderNoFound = findViewById(R.id.notFound_placeholder)
        placeholderError = findViewById(R.id.error_placeholder)
        listTracks = findViewById(R.id.track_list)
        placeholderHistory = findViewById(R.id.search_history)
        historyRecyclerView = findViewById(R.id.history_track_list)
        historyHeadder = findViewById(R.id.history_headder)
        historyClearButton = findViewById(R.id.button_clear_history)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun initAdapters() {
        adapter.tracks = tracks
        listTracks.adapter = adapter
        historyRecyclerView.adapter = historyAdapter

        // Обработчик кликов для основного списка треков
        adapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    searchHistoryInteractor.addTrack(track)
                    openPlayer(track)
                }
            }
        })

        // Обработчик кликов для истории
        historyAdapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    searchHistoryInteractor.addTrack(track)
                    openPlayer(track)
                }
            }
        })
    }

    private fun initListeners() {
        // создаём анонимный класс TextWatcher для обработки ввода текста
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) updateTrackHistory() else searchDebounce()
            }
        })

        clearButton.setOnClickListener {
            searchInput.text.clear()
            hideKeyBoard()
            clearSearchResults()
            updateTrackHistory()
        }

        updateButton.setOnClickListener {
            performTrackSearch(searchInput.text.toString())
        }

        historyClearButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            updateTrackHistory()
        }

    }

    private fun showKeyBoard() {
        searchInput.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        if (searchInput.isFocused && searchInput.windowToken != null) {
            imm?.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyBoard() {
        (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    // сохраняем  введенное значение
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, searchText)
    }

    // выделяем отдельный метод для поискового запроса
    private fun performTrackSearch(query: String) {
        if (query.isEmpty()) return

        progressBar.visibility = View.VISIBLE
        placeholderNoFound.visibility = View.GONE
        placeholderError.visibility = View.GONE
        listTracks.visibility = View.GONE
        placeholderHistory.visibility = View.GONE

        tracksInteractor.searchTracks(query, object : TracksInteractor.TrackConsumer {
            override fun comsume(foundTracks: List<Track>) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    tracks.clear()
                    if (foundTracks.isNotEmpty()) {
                        tracks.addAll(foundTracks)
                        adapter.notifyDataSetChanged()
                        listTracks.visibility = View.VISIBLE
                    } else {
                        placeholderNoFound.visibility = View.VISIBLE
                    }
                }
            }
        })

    }

    private fun updateTrackHistory(hasFocus: Boolean = searchInput.hasFocus()) {
        val history = searchHistoryInteractor.getHistory()
        val isSearchFieldEmpty = searchInput.text.isEmpty()
        val showHistory = isSearchFieldEmpty && history.isNotEmpty()

        placeholderHistory.visibility = if (!showHistory) View.GONE else View.VISIBLE
        listTracks.visibility = if (showHistory) View.GONE else View.VISIBLE

        if (showHistory) {
            historyAdapter.tracks.clear()
            historyAdapter.tracks.addAll(history)
            historyAdapter.notifyDataSetChanged()
        }
    }


    private fun restoreSearchText(savedInstanceState: Bundle?) {
        val savedText = savedInstanceState?.getString(INPUT_TEXT, "") ?: ""
        searchInput.setText(savedText)
    }

    private fun clearSearchResults() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        placeholderNoFound.visibility = View.GONE
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        return if (isClickAllowed) {
            isClickAllowed = false
            clickHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
            true
        } else {
            false
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioplayerActivity::class.java)
        intent.putExtra(TRACK_EXTRA, track)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(searchRunnable)
    }

    // константы для сохранения и извлечения данных
    companion object {
        const val INPUT_TEXT = "SEARCH_TEXT"
        const val SAVED_TEXT = ""

        // Новые константы для передачи данных на экран аудиоплейера
        const val TRACK_EXTRA = "TRACK_EXTRA"

        // константа для отложенного поиского запроса
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}