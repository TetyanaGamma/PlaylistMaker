package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.network.Response
import com.example.playlistmaker.search.data.network.TrackApi
import com.example.playlistmaker.search.data.network.TrackSearchRequest

class RetrofitNetworkClient (private val trackService: TrackApi): NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val resp = trackService.search(dto.expression).execute()
            val body = resp.body() ?: Response()
            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

}