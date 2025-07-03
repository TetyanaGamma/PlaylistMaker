package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.model.Track

class TrackResponce (val resultCount: Int,
                      val results: List<Track>)