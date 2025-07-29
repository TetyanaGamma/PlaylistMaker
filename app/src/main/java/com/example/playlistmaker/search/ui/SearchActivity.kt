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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.player.ui.AudioplayerActivity
import com.example.playlistmaker.search.domain.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var placeholderNoFound: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var updateButton: View
    private lateinit var listTracks: RecyclerView
    private lateinit var placeholderHistory: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyHeadder: TextView
    private lateinit var historyClearButton: View
    private lateinit var progressBar: ProgressBar

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private var viewModel: SearchViewModel? = null
    private var textWatcher: TextWatcher? = null

  /*  private val searchHandler = Handler(Looper.getMainLooper())
             private val searchRunnable = Runnable { viewModel?.searchRequest(searchInput.text.toString()) }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initUi()
        initListeners()
        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel?.observeState()?.observe(this, Observer {
            render (it)
        })
        viewModel?.loadHistory()

    }

    private fun initUi() {
        val toolbar: Toolbar = findViewById(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        searchInput = findViewById(R.id.serch_input)
        clearButton = findViewById(R.id.clear_icon)
        updateButton = findViewById(R.id.button_update)
        placeholderNoFound = findViewById(R.id.notFound_placeholder)
        placeholderError = findViewById(R.id.error_placeholder)
        listTracks = findViewById(R.id.track_list)
        placeholderHistory = findViewById(R.id.search_history)
        historyRecyclerView = findViewById(R.id.history_track_list)
        historyHeadder = findViewById(R.id.history_headder)
        historyClearButton = findViewById(R.id.button_clear_history)
        progressBar = findViewById(R.id.progressBar)

        listTracks.adapter = adapter
        historyRecyclerView.adapter = historyAdapter
    }

    private fun initListeners() {

        // создаём анонимный класс TextWatcher для обработки ввода текста
        textWatcher = object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) viewModel?.loadHistory() else viewModel?.searchDebounce(s.toString())
            }
        }
        textWatcher.let { searchInput.addTextChangedListener(it) }


        clearButton.setOnClickListener {
            searchInput.text.clear()
            hideKeyboard()
            viewModel?.loadHistory()
        }

        updateButton.setOnClickListener {
            viewModel?.retrySearch()
        }

        historyClearButton.setOnClickListener {
            viewModel?.clearHistory()
            placeholderHistory.visibility = View.GONE
        }

        adapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                viewModel?.saveTrack(track)
                openPlayer(track)
            }
        })

        historyAdapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                viewModel?.saveTrack(track)
                openPlayer(track)
            }
        })
    }

 /*   override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(INPUT_TEXT, searchInput.text.toString())
    }

    private fun restoreSearchText(savedInstanceState: Bundle?) {
        val savedText = savedInstanceState?.getString(INPUT_TEXT, "") ?: ""
        searchInput.setText(savedText)
    }
*/

    private fun render(state: SearchState) {
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showTracks(state.tracks)
                is SearchState.NoConnection -> showError()
                is SearchState.NothingFound -> showEmpty()
                is SearchState.History -> showHistory(state.tracks)
            }
        }


    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        listTracks.visibility = View.GONE
        placeholderError.visibility = View.GONE
        placeholderNoFound.visibility = View.GONE
        placeholderHistory.visibility = View.GONE
    }

    private fun showTracks(tracks: List<Track>) {
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()

        progressBar.visibility = View.GONE
        listTracks.visibility = View.VISIBLE
        placeholderError.visibility = View.GONE
        placeholderNoFound.visibility = View.GONE
        placeholderHistory.visibility = View.GONE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        listTracks.visibility = View.GONE
        placeholderError.visibility = View.VISIBLE
        placeholderNoFound.visibility = View.GONE
        placeholderHistory.visibility = View.GONE
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        listTracks.visibility = View.GONE
        placeholderError.visibility = View.GONE
        placeholderNoFound.visibility = View.VISIBLE
        placeholderHistory.visibility = View.GONE
    }

    private fun showHistory(history: List<Track>) {
        if (history.isEmpty()) {
            placeholderHistory.visibility = View.GONE
            return
        }
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(history)
        historyAdapter.notifyDataSetChanged()

        progressBar.visibility = View.GONE
        listTracks.visibility = View.GONE
        placeholderError.visibility = View.GONE
        placeholderNoFound.visibility = View.GONE
        placeholderHistory.visibility = View.VISIBLE
    }

   /* private fun debounceSearch() {
        searchHandler.removeCallbacks(searchRunnable)
        searchHandler.postDelayed(searchRunnable, 2000L)
    }*/

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioplayerActivity::class.java)
        intent.putExtra("TRACK_EXTRA", track)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { searchInput.removeTextChangedListener(it) }
    }

    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"
    }
}

