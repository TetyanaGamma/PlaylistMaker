package com.example.playlistmaker.search.data

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
}