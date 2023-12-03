package com.stevdza.san.mongodemo.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class CurrentTermKey(private val context: Context) {

    // to make sure there's only one instance
    companion object {
        internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore("CurrentTerm")
        val CURRENT_TERM = stringPreferencesKey("CurrentTerm")

    }

    //get the saved key
    val getKey = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_TERM] ?: ""

        }

    //save key into datastore
    suspend fun saveKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_TERM] = key
        }
    }


}