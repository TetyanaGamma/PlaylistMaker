package com.example.playlistmaker.data.dto

class TrackResponse (val searchType: String,
                     val expression: String,
                     val results: List<TrackDto>): Response()