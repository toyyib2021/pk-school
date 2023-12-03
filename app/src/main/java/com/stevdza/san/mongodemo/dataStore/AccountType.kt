package com.stevdza.san.mongodemo.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class AccountTypeKey(private val context: Context) {

    // to make sure there's only one instance
    companion object {
        internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AccountTypeKey")
        val ACCOUNT_TYPE_KEY = stringPreferencesKey("AccountTypeKey")

    }

    //get the saved key
    val getKey = context.dataStore.data
        .map { preferences ->
            preferences[ACCOUNT_TYPE_KEY] ?: ""

        }

    //save key into datastore
    suspend fun saveKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCOUNT_TYPE_KEY] = key
        }
    }


}