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
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.player.ui.AudioplayerActivity
import com.example.playlistmaker.search.domain.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

  /*  private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var placeholderNoFound: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var updateButton: View
    private lateinit var listTracks: RecyclerView
    private lateinit var placeholderHistory: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyHeadder: TextView
    private lateinit var historyClearButton: View
    private lateinit var progressBar: ProgressBar*/

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private var viewModel: SearchViewModel? = null
    private var textWatcher: TextWatcher? = null

  /*  private val searchHandler = Handler(Looper.getMainLooper())
             private val searchRunnable = Runnable { viewModel?.searchRequest(searchInput.text.toString()) }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
      //  val toolbar: Toolbar = findViewById(R.id.search_toolbar)
        binding.searchToolbar.setNavigationOnClickListener { finish() }

     /*   binding.serchInput
        binding.clearIcon
        binding.buttonUpdate
        binding.notFoundPlaceholder
       binding.notFoundPlaceholder
      //  binding.trackList
        binding.searchHistory
        binding.historyTrackList
        binding.historyHeadder
       binding.buttonClearHistory
       binding.progressBar*/

        binding.trackList.adapter = adapter
        binding.historyTrackList.adapter = historyAdapter
    }

    private fun initListeners() {

        // создаём анонимный класс TextWatcher для обработки ввода текста
        textWatcher = object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) viewModel?.loadHistory() else viewModel?.searchDebounce(s.toString())
            }
        }
        textWatcher.let { binding.serchInput.addTextChangedListener(it) }


        binding.clearIcon.setOnClickListener {
            binding.serchInput.text.clear()
            hideKeyboard()
            viewModel?.loadHistory()
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel?.retrySearch()
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel?.clearHistory()
            binding.searchHistory.visibility = View.GONE
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
        binding.progressBar.visibility = View.VISIBLE
        binding.trackList.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.GONE
        binding.notFoundPlaceholder.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
        hideKeyboard()
    }

    private fun showTracks(tracks: List<Track>) {
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
        hideKeyboard()

        binding.progressBar.visibility = View.GONE
        binding.trackList.visibility = View.VISIBLE
        binding.errorPlaceholder.visibility = View.GONE
        binding.notFoundPlaceholder.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.trackList.visibility = View.GONE
       binding.errorPlaceholder.visibility = View.VISIBLE
        binding.notFoundPlaceholder.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
       binding.trackList.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.GONE
        binding.notFoundPlaceholder.visibility = View.VISIBLE
        binding.searchHistory.visibility = View.GONE
    }

    private fun showHistory(history: List<Track>) {
        if (history.isEmpty()) {
            binding.searchHistory.visibility = View.GONE
            return
        }
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(history)
        historyAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.GONE
       binding.trackList.visibility = View.GONE
       binding.errorPlaceholder.visibility = View.GONE
        binding.notFoundPlaceholder.visibility = View.GONE
        binding.searchHistory.visibility = View.VISIBLE
    }

   /* private fun debounceSearch() {
        searchHandler.removeCallbacks(searchRunnable)
        searchHandler.postDelayed(searchRunnable, 2000L)
    }*/

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.serchInput.windowToken, 0)
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioplayerActivity::class.java)
        intent.putExtra("TRACK_EXTRA", track)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { binding.serchInput.removeTextChangedListener(it) }
    }

    companion object {
        const val TRACK_EXTRA = "TRACK_EXTRA"
    }
}

