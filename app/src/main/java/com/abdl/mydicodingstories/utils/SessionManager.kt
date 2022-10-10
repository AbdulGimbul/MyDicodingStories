package com.abdl.mydicodingstories.utils

import android.content.Context
import android.content.SharedPreferences
import com.abdl.mydicodingstories.R

class SessionManager(context: Context) {
    private val prefs: SharedPreferences? =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val NAME = "name"
        const val USER_ID = "user_id"
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
}