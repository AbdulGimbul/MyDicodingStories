package com.abdl.mydicodingstories.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class SessionManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences? =
        context.getSharedPreferences("MyDicodingStories", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val NAME = "name"
        const val USER_ID = "user_id"
        const val SELECTED_LANGUAGE = "selected_language"
        const val DEFAULT_LANGUAGE_CODE = "in"
    }

    fun saveAuthToken(token: String, name: String, userId: String) {
        val editor = prefs?.edit()
        if (editor != null) {
            editor.putString(USER_TOKEN, token)
            editor.putString(NAME, name)
            editor.putString(USER_ID, userId)
            editor.apply()
        }
    }

    fun fetchAuthToken(): String? {
        return prefs?.getString(USER_TOKEN, null)
    }

    fun fetchName(): String? {
        return prefs?.getString(NAME, null)
    }

    fun fetchUserId(): String? {
        return prefs?.getString(USER_ID, null)
    }

    fun deleteAuthToken(): Boolean {
        return prefs?.edit()?.clear()!!.commit()
    }

    fun saveLanguage(language: String) {
        prefs?.edit {
            putString(SELECTED_LANGUAGE, language)
        }
    }

    fun fetchLanguage(): String? {
        return prefs?.getString(SELECTED_LANGUAGE, DEFAULT_LANGUAGE_CODE) ?: DEFAULT_LANGUAGE_CODE
    }
}