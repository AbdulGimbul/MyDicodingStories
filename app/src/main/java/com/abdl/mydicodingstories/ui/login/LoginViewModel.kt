package com.abdl.mydicodingstories.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abdl.mydicodingstories.data.remote.response.LoginResponse
import com.abdl.mydicodingstories.data.remote.response.RegisterResponse
import com.abdl.mydicodingstories.data.remote.service.ApiService
import com.abdl.mydicodingstories.utils.SessionManager
import kotlinx.coroutines.*
import org.json.JSONObject

class LoginViewModel(private val session: SessionManager, private val apiService: ApiService) :
    ViewModel() {

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    fun getLogin(email: String, password: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.login(email, password)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val loginResult = response.body()
                    _loginResponse.postValue(loginResult)
                    if (loginResult != null) {
                        session.saveAuthToken(
                            loginResult.loginResult.token,
                            loginResult.loginResult.name,
                            loginResult.loginResult.userId
                        )
                    }
                } else {
                    _isLoading.value = false
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                    Log.d("LoginViewModel", "cekkk : ${error}}")
                }
            }
        }
    }

    fun getRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.register(name, email, password)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val registerResult = response.body()
                    _registerResponse.postValue(registerResult)
                } else {
                    _isLoading.value = false
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun onError(message: String) {
        _isLoading.postValue(false)
        _errorMessage.postValue(message)
    }

    fun getErrorMessage(raw: String): String {
        val obj = JSONObject(raw)
        return obj.getString("message");
    }
}