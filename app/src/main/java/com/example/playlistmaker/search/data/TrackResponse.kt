package com.example.playlistmaker.search.data

class TrackResponse (val searchType: String,
                     val expression: String,
                     val results: List<TrackDto>): Response()