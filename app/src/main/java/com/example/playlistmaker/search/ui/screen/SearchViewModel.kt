package com.example.playlistmaker.search.ui.screen

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.model.Track

class SearchViewModel(private val context: Context) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val searchInteractor: SearchInteractor = Creator.provideSearchInteractor(context)
    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lastQuery: String = ""

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) return

        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty())
            return
        lastQuery = newSearchText
        renderState(SearchState.Loading)

        searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?) {
                handler.post {
                    when {
                        foundTracks == null -> renderState(SearchState.NoConnection)
                        foundTracks.isEmpty() -> renderState(SearchState.NothingFound)
                        else -> renderState(SearchState.Content(foundTracks))
                    }
                }
            }
        }
        )
    }

    fun retrySearch() {
        if (lastQuery.isNotEmpty()) {
            searchRequest(lastQuery)
        }
    }

    fun loadHistory() {
        val history = searchInteractor.getHistory()
        renderState(SearchState.History(history))
    }

    fun saveTrack(track: Track) {
        searchInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
        loadHistory()
    }


    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as App)
                SearchViewModel(app)
            }
        }
    }

}