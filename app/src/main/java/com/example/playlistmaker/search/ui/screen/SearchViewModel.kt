package com.example.playlistmaker.search.ui.screen

import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null

    private var lastQuery: String = ""

    var currentSearchQuery: String = ""
    var currentSearchResults: List<Track> = emptyList()

    var isShowingHistory: Boolean = false

    var cameFromHistory: Boolean = false
        private set

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

        viewModelScope.launch {
            searchInteractor.searchTracks(newSearchText)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> renderState(SearchState.Loading)
                        is Resource.Success -> {
                            val tracks = resource.data
                            if (tracks.isEmpty()) renderState(SearchState.NothingFound)
                            else renderState(SearchState.Content(tracks))
                        }
                        is Resource.Error -> renderState(SearchState.NoConnection)
                    }
                }
        }
    }

    fun retrySearch() {
        if (lastQuery.isNotEmpty()) {
            searchRequest(lastQuery)
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            searchInteractor.getHistory()
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> Unit
                        is Resource.Success -> {
                            if (currentSearchQuery.isEmpty() && currentSearchResults.isEmpty()) {
                                isShowingHistory = true
                                renderState(SearchState.History(resource.data))
                            }
                        }
                        is Resource.Error -> renderState(SearchState.NoConnection)
                    }
                }
        }
    }

    fun saveTrack(track: Track) {
        searchInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
        loadHistory()
    }

    fun markCameFromHistory(fromHistory: Boolean) {
        cameFromHistory = fromHistory
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
    }

}