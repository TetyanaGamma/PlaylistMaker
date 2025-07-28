package com.example.playlistmaker.search.data

interface NetworkClient {
    fun doRequest(dto: Any): Response

}