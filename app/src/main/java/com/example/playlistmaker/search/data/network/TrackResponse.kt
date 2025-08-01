package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.TrackDto

class TrackResponse (val searchType: String,
                     val expression: String,
                     val results: List<TrackDto>): Response()