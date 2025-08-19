package com.example.playlistmaker.mediateca.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MediatekaViewModel: ViewModel() {

    private val _selectedTab = MutableStateFlow(0) // Позиция выбранного таба
    val selectedTab: StateFlow<Int> = _selectedTab

    // Метод для выбора таба
    fun selectTab(position: Int) {
        viewModelScope.launch {
            _selectedTab.emit(position)
        }
    }
}