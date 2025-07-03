package com.example.playlistmaker.presentation

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
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.network.TrackApi
import com.example.playlistmaker.domain.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val trackBaseURL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(trackBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)

    private val searchRunnable = Runnable { trackSearch(searchText) }

    private lateinit var mainThreadHandler: Handler

    private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private var searchText: String = SAVED_TEXT  // переменная для хранения введённого текста
    private var lastInput: String? =
        null       // переменная для хранения последнего введенного текста

    private lateinit var placeholderNoFound: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var listTracks: RecyclerView
    private lateinit var searchHistory: SearchHistory
    private lateinit var placeholderHistory: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyHeadder: TextView
    private lateinit var historyClearButton: Button
    private lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private var historyAdapter = TrackAdapter()
    private var isClickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())
    private val CLICK_DEBOUNCE_DELAY = 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initUi()
        initAdapters()

        val toolbar: Toolbar = findViewById(R.id.search_toolbar)
        // нажатие на иконку назад
        toolbar.setNavigationOnClickListener {
            finish()
        }

        mainThreadHandler = Handler(Looper.getMainLooper())

        initTrackHistory()
        initListeners()

        // получаем сохраненное значение
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(INPUT_TEXT, SAVED_TEXT)
            searchInput.setText(searchText)
        }

        // Устанавливаем фокус на поле ввода
        searchInput.post {
            searchInput.requestFocus()
            showKeyBoard()
        }

        updateTrackHistory()

    }

    private fun initUi() {
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
                    searchHistory.addTrack(track)
                    val intent =
                        Intent(this@SearchActivity, AudioplayerActivity::class.java).apply {
                            putExtra(TRACK_EXTRA, track)
                        }
                    startActivity(intent)
                }
            }
        })

        // Обработчик кликов для истории
        historyAdapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    searchHistory.addTrack(track)
                    val intent =
                        Intent(this@SearchActivity, AudioplayerActivity::class.java).apply {
                            putExtra(TRACK_EXTRA, track)
                        }
                    startActivity(intent)
                }
            }
        })
    }

    private fun initTrackHistory() {
        val prefs = getSharedPreferences("search_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)
        updateTrackHistory()
    }

    private fun initListeners() {
        // создаём анонимный класс TextWatcher для обработки ввода текста
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility =
                        View.GONE // если в строке поиска ничего нет, кнопка очистить не нужна
                    updateTrackHistory()
                } else {
                    clearButton.visibility = View.VISIBLE
                    updateTrackHistory()
                }
                searchText = s.toString()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchInput.addTextChangedListener(searchTextWatcher)

        // по клику на строку поиска выводим клавиатуру
        searchInput.setOnClickListener() {
            showKeyBoard()
        }

        // Отслеживание фокуса в поле поиска
        searchInput.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            // Обновляем видимость истории только при потере фокуса
            if (!hasFocus) {
                updateTrackHistory(hasFocus)
            }
        }

        // обрабатываем нажатие на кнопку очистить
        clearButton.setOnClickListener() {
            searchInput.text.clear()
            clearButton.visibility = View.GONE
            hideKeyBoard()
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderNoFound.visibility = View.GONE
            updateTrackHistory()
        }

        // по нажатию на кнопку обновить - выполняем последний сохраненный поисковый API запрос
        updateButton.setOnClickListener {
            val input = searchInput.text.toString()
            lastInput = input
            lastInput?.let { input ->
                trackSearch(input)
            }
        }

        historyClearButton.setOnClickListener {
            searchHistory.clearHistory()
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
    private fun trackSearch(input: String) {
        if (input.isNotEmpty()) {
            placeholderNoFound.visibility = View.GONE
            placeholderError.visibility = View.GONE
            listTracks.visibility = View.GONE
            placeholderHistory.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            trackService.search(input)
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.isSuccessful) {
                            progressBar.visibility = View.GONE
                            tracks.clear()
                            val results = response.body()?.results

                            if (!results.isNullOrEmpty()) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                listTracks.visibility = View.VISIBLE
                                placeholderNoFound.visibility = View.GONE
                                placeholderError.visibility = View.GONE
                                placeholderHistory.visibility = View.GONE
                            } else {
                                listTracks.visibility = View.GONE
                                placeholderNoFound.visibility = View.VISIBLE
                                placeholderError.visibility = View.GONE
                                placeholderHistory.visibility = View.GONE
                            }
                        } else {
                            listTracks.visibility = View.GONE
                            placeholderError.visibility = View.VISIBLE
                            placeholderNoFound.visibility = View.GONE
                            placeholderHistory.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        listTracks.visibility = View.GONE
                        placeholderError.visibility = View.VISIBLE
                        placeholderNoFound.visibility = View.GONE
                        placeholderHistory.visibility = View.GONE
                    }
                })
        }
    }

    private fun updateTrackHistory(hasFocus: Boolean = searchInput.hasFocus()) {
        val history = searchHistory.getHistory()
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

    //
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

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(searchRunnable)
    }

    // константы для сохранения и извлечения данных
    companion object {
        const val INPUT_TEXT = "SEARCH_TEXT"
        const val SAVED_TEXT = ""

        // Новые константы для передачи данных на экран аудиоплейера
        const val TRACK_EXTRA = "trackJson"
        const val SHARED_PREFS_NAME = "playlist_maker_prefs"

        // константа для отложенного поиского запроса
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}