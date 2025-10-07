package com.example.playlistmaker.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClickDebounce(
    private val scope: CoroutineScope,
    private val delayMillis: Long = 1000L
) {
    private var job: Job? = null

    fun submit(action: () -> Unit) {
        if (job?.isActive == true) return
        job = scope.launch {
            action()
            delay(delayMillis)
        }
    }
}
