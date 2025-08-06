package com.example.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class PrefsStorageClient<T>(
    context: Context,
    private var gson: Gson,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "HISTORY_KEY",
        Context.MODE_PRIVATE
    )

    override fun storeData(data: T) {
        sharedPreferences.edit { putString(dataKey, gson.toJson(data, type)) }
    }

    override fun getData(): T? {
        val dataJson = sharedPreferences.getString(dataKey, null)
        return dataJson?.let { gson.fromJson(it, type) }
    }

}