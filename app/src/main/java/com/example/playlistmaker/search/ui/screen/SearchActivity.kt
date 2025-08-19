package com.example.playlistmaker.search.ui.screen

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.AudioplayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private val viewModel: SearchViewModel by viewModel()
    private var textWatcher: TextWatcher? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initListeners()

        viewModel.observeState().observe(this, Observer {
            render(it)
        })
        viewModel.loadHistory()

    }

    private fun initUi() {

        binding.searchToolbar.setNavigationOnClickListener { finish() }
        binding.trackList.adapter = adapter
        binding.historyTrackList.adapter = historyAdapter
    }

    private fun initListeners() {

        // создаём анонимный класс TextWatcher для обработки ввода текста
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE
                else View.VISIBLE
                if (s.isNullOrEmpty()) viewModel.loadHistory()
                else viewModel.searchDebounce(s.toString())
            }
        }
        textWatcher.let { binding.serchInput.addTextChangedListener(it) }

        binding.clearIcon.setOnClickListener {
            binding.serchInput.text.clear()
            hideKeyboard()
            binding.trackList.visibility = View.GONE
            binding.notFoundPlaceholder.visibility = View.GONE
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel.retrySearch()
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.searchHistory.visibility = View.GONE
        }

        adapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                viewModel.saveTrack(track)
                openPlayer(track)
            }
        })

        historyAdapter.setOnTrackClickListener(object : TrackAdapter.OnTrackClicklistener {
            override fun onTrackClick(track: Track) {
                viewModel.saveTrack(track)
                openPlayer(track)
            }
        })
    }

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


    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.serchInput.windowToken, 0)
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioplayerActivity::class.java)
        intent.putExtra(AudioplayerActivity.TRACK_EXTRA, track)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { binding.serchInput.removeTextChangedListener(it) }
    }

}