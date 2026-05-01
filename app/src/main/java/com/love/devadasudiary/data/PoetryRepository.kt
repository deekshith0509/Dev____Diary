package com.love.devadasudiary.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.love.devadasudiary.core.DiaryTimings
import com.love.devadasudiary.core.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Loads poem markdown either from a remote raw-Gist URL or from the local
 * DataStore cache. Single OkHttp client is shared across instances so that
 * connection pooling is preserved across config changes.
 */
class PoetryRepository(private val context: Context) {

    private val appContext = context.applicationContext

    suspend fun fetchPoemFromNetwork(gistRawUrl: String): String =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(gistRawUrl)
                .header("Cache-Control", "max-age=300")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Server returned HTTP ${response.code}")
                }
                val body = response.body?.string()
                    ?: throw IOException("Empty response body")
                cleanContent(body)
            }
        }

    suspend fun savePoem(poemId: String, content: String) {
        appContext.dataStore.edit { prefs ->
            prefs[poemKey(poemId)] = content
        }
    }

    suspend fun loadCachedPoem(poemId: String): String? {
        val prefs = appContext.dataStore.data
            .catch { emit(emptyPreferences()) }
            .first()
        return prefs[poemKey(poemId)]
    }

    private fun poemKey(poemId: String) = stringPreferencesKey("cached_poem_$poemId")

    /**
     * Strip the UTF-8 BOM and trim trailing whitespace before persisting or
     * rendering. Doing this in one place avoids the previous bug where the
     * BOM was only removed on the network path, so cached poems would
     * silently retain it.
     */
    private fun cleanContent(raw: String): String =
        raw.replace("\uFEFF", "").trim()

    companion object {
        private val client: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(DiaryTimings.CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(DiaryTimings.READ_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(DiaryTimings.WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }
    }
}
