package com.example.playlistmaker.search.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val trackService: TrackApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            try {
                // сетевой вызов в IO потоке
                val result = withContext(Dispatchers.IO) {
                    trackService.search(dto.expression)
                }
                // возвращаем результат с фиктивным resultCode 200
                result.apply { resultCode = 200 }
            } catch (e: Throwable) {
                // при ошибке сети
                Response().apply { resultCode = 500 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}
