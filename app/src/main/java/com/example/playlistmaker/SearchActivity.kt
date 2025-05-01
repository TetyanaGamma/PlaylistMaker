package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.search_toolbar)
        searchInput = findViewById(R.id.serch_input)
        clearButton = findViewById<ImageView>(R.id.clear_icon)
        updateButton = findViewById(R.id.button_update)
        placeholderNoFound = findViewById(R.id.notFound_placeholder)
        placeholderError = findViewById(R.id.error_placeholder)
        listTracks = findViewById(R.id.track_list)

        adapter.tracks = tracks
        listTracks.adapter = adapter

        // нажатие на иконку назад
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // создаём анонимный класс TextWatcher для обработки ввода текста
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility =
                        View.GONE // если в строке поиска ничего нет, кнопка очистить не нужна
                } else {
                    clearButton.visibility = View.VISIBLE
                    searchText = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchInput.addTextChangedListener(searchTextWatcher)

        // по клику на строку поиска выводим клавиатуру
        searchInput.setOnClickListener() {
            showKeyBoard()
        }

        // обрабатываем нажатие на кнопку очистить
        clearButton.setOnClickListener() {
            searchInput.text.clear()
            clearButton.visibility = View.GONE
            hideKeyBoard()
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderNoFound.visibility = View.GONE
        }

        // получаем сохраненное значение
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(INPUT_TEXT, SAVED_TEXT)
            searchInput.setText(searchText)
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

        // по нажатию на кнопку обновить - выполняем последний сохраненный поисковый API запрос
        updateButton.setOnClickListener {
            val input = searchInput.text.toString()
            lastInput = input
            lastInput?.let {input ->
                trackSearch(input)
            }
        }
    }

    private fun showKeyBoard() {
        searchInput.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchInput.windowToken, 0)
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
                    response: Response<TrackResponce>) {
                    if (response.isSuccessful) {
                        tracks.clear()
                        val results = response.body()?.results

                        if (!results.isNullOrEmpty()) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                            placeholderNoFound.visibility = View.GONE
                        }
                        else {
                            placeholderNoFound.visibility = View.VISIBLE
                        }
                    }
                    else {
                        placeholderError.visibility = View.VISIBLE
                        }
                    }

                override fun onFailure(call: Call<TrackResponce>, t: Throwable) {
                    placeholderError.visibility = View.VISIBLE
                }
    })
}
}