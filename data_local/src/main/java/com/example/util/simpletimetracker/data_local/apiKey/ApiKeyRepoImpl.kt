package com.example.util.simpletimetracker.data_local.apiKey

import android.content.Context
import com.example.util.simpletimetracker.domain.parser.ApiKeyRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ApiKeyRepo {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun get(): String? {
        return prefs.getString(KEY_API_KEY, null)?.takeIf { it.isNotBlank() }
    }

    override fun set(key: String) {
        prefs.edit().putString(KEY_API_KEY, key.trim()).apply()
    }

    override fun clear() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }

    private companion object {
        const val PREFS_NAME = "time_ledger_prefs"
        const val KEY_API_KEY = "deepseek_api_key"
    }
}
