package com.love.devadasudiary

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "devadasu_diary_store")
