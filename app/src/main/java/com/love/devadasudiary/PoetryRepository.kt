package com.love.devadasudiary

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.love.devadasudiary.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class PoetryRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun poemKey(poemId: String) = stringPreferencesKey("cached_poem_$poemId")

    suspend fun fetchPoemFromNetwork(gistRawUrl: String): String {
        return withContext(Dispatchers.IO) {
            val req = Request.Builder()
                .url(gistRawUrl)
                .header("Cache-Control", "no-cache")
                .get()
                .build()

            val response = client.newCall(req).execute()

            if (!response.isSuccessful) {
                throw IOException("Server returned ${response.code}")
            }

            response.body?.string() ?: throw IOException("Empty response from server")
        }
    }

    suspend fun savePoem(poemId: String, poem: String) {
        context.dataStore.edit { prefs ->
            prefs[poemKey(poemId)] = poem
        }
    }

    suspend fun loadCachedPoem(poemId: String): String? {
        val prefs = context.dataStore.data.first()
        return prefs[poemKey(poemId)]
    }
}
