package com.example.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.StorageClient
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val context: Context,
    private val dataKey: String,
    private val type: Type) : StorageClient<T> {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("HISTORY_KEY",
        Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun storeData(data: T) {
        sharedPreferences.edit().putString(dataKey, gson.toJson(data, type)).apply()
    }

    override fun getData(): T? {
        val dataJson = sharedPreferences.getString(dataKey, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }

}