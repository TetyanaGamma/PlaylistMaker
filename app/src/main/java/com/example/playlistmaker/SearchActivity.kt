package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private var searchText: String = SAVED_TEXT  // переменная для хранения введённого текста
    private var lastInput: String? = null       // переменная для хранения последнего введенного текста

    // константы для сохранения и извлечения данных
    companion object {
        const val INPUT_TEXT = "SEARCH_TEXT"
        const val SAVED_TEXT = ""
    }

    private lateinit var placeholderNoFound: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var listTracks: RecyclerView
    private lateinit var searchHistory: SearchHistory
    private lateinit var placeholderHistory: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyHeadder: TextView
    private lateinit var historyClearButton: Button

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private var historyAdapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initUi()
        initAdapters()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.search_toolbar)
        // нажатие на иконку назад
        toolbar.setNavigationOnClickListener {
            finish()
        }

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
    }

    private fun initAdapters() {
        adapter.tracks = tracks
        listTracks.adapter = adapter

        historyRecyclerView.adapter = historyAdapter

        // Обработчик кликов для основного списка треков
        adapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                searchHistory.addTrack(track)
            }
        })

        // Обработчик кликов для истории
        historyAdapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                searchHistory.addTrack(track)
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

        //обрабатываем нажатие на кнопку Done на клавиатуре
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val input = searchInput.text.toString()
                lastInput = input
                trackSearch(input)
                true
            }
            false
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

        placeholderNoFound.visibility = View.GONE
        placeholderError.visibility = View.GONE

        trackService.search(input)
            .enqueue(object : Callback<TrackResponce> {
                override fun onResponse(
                    call: Call<TrackResponce>,
                    response: Response<TrackResponce>
                ) {
                    if (response.isSuccessful) {
                        tracks.clear()
                        val results = response.body()?.results

                        if (!results.isNullOrEmpty()) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                            placeholderNoFound.visibility = View.GONE
                            placeholderHistory.visibility = View.GONE
                        } else {
                            placeholderNoFound.visibility = View.VISIBLE
                            placeholderHistory.visibility = View.GONE
                        }
                    } else {
                        placeholderError.visibility = View.VISIBLE
                        placeholderHistory.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<TrackResponce>, t: Throwable) {
                    placeholderError.visibility = View.VISIBLE
                    placeholderHistory.visibility = View.GONE
                }
            })
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
}
