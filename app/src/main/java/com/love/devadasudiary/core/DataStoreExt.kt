package com.love.devadasudiary.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * App-wide DataStore instance. Using the property delegate guarantees a
 * single instance per process, which is the documented contract — creating
 * multiple instances for the same file is undefined behavior.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "devadasu_diary_store"
)
