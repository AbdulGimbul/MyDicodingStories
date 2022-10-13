package com.abdl.mydicodingstories.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdl.mydicodingstories.data.remote.service.ApiService
import com.abdl.mydicodingstories.ui.MainViewModel
import com.abdl.mydicodingstories.ui.login.LoginViewModel

class ViewModelFactory(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(sessionManager, apiService) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}