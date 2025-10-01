package com.example.playlistmaker.search.ui.screen

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null
  //  private val handler = Handler(Looper.getMainLooper())
    private var lastQuery: String = ""

    var currentSearchQuery: String = ""
    var currentSearchResults: List<Track> = emptyList()

    var isShowingHistory: Boolean = false
    private var debounceJob: Job? = null

    // --- DEBOUNCE ---
    fun searchDebounce(query: String) {
        if (currentSearchQuery == query) return

        currentSearchQuery = query
        debounceJob?.cancel()
        if (query.isEmpty()) {
            loadHistory()
            return
        }

        debounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(query)
        }
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty())
            return
        lastQuery = newSearchText
        renderState(SearchState.Loading)

        searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?) {
                    when {
                        foundTracks == null -> renderState(SearchState.NoConnection)
                        foundTracks.isEmpty() -> renderState(SearchState.NothingFound)
                        else -> renderState(SearchState.Content(foundTracks))
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
        isShowingHistory = true
        // очищаем результаты поиска
        currentSearchResults = emptyList()
        // очищаем поле ввода
        currentSearchQuery = ""
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
        //  сохраняем результаты последнего поиска
        if (state is SearchState.Content) {
            currentSearchResults = state.tracks
            isShowingHistory = false
        }
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        debounceJob?.cancel()}

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

}