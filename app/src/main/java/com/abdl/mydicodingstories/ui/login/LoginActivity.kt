package com.abdl.mydicodingstories.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.abdl.mydicodingstories.databinding.ActivityLoginBinding
import com.abdl.mydicodingstories.ui.MainActivity
import com.abdl.mydicodingstories.utils.SessionManager
import com.abdl.mydicodingstories.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val session = SessionManager(this)
        if (session.fetchAuthToken() != null) {
            Intent(this@LoginActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        loginViewModel =
            ViewModelProvider(this, ViewModelFactory(session, this))[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.edtPass.addTextChangedListener {
            if (it?.length!! < 6) {
                binding.edtPass.error = "Minimal 6 karakter"
            }
        }


        binding.lupaPassword.setOnClickListener {
            Snackbar.make(binding.container, "Silahkan menghubungi admin!", Snackbar.LENGTH_LONG)
                .also { snackbar ->
                    snackbar.setAction("Ok") {
                        snackbar.dismiss()
                    }
                }.show()
        }

        loginViewModel.loginResponse.observe(this@LoginActivity) { result ->
            when (result.message) {
                "success" -> {
                    Intent(this@LoginActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }
                else -> {
                    Toast.makeText(this@LoginActivity, "Login gagal!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginViewModel.isLoading.observe(this) {
            if (it == true) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        loginViewModel.errorMessage.observe(this) {
            when (it) {
                "Error : User not found" -> {
                    Toast.makeText(this@LoginActivity, "User tidak ditemukan!", Toast.LENGTH_SHORT)
                        .show()
                }
                "Error : Invalid password" -> {
                    Toast.makeText(this@LoginActivity, "Password salah!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loginUser() {
        with(binding) {
            val username = edtUsername.text.toString().trim()
            val password = edtPass.text.toString().trim()

            var isInvalidFields = false

            if (!isValidEmail(username)) {
                isInvalidFields = true
                edtUsername.error = "Email tidak valid"
            }

            if (username.isEmpty()) {
                isInvalidFields = true
                edtUsername.error = "Email tidak boleh kosong"
            }

            if (password.length < 6) {
                isInvalidFields = true
                edtPass.error = "Password minimal terdiri dari 8 karakter"
            }

            if (password.isEmpty()) {
                isInvalidFields = true
                edtPass.error = "Password tidak boleh kosong"
            }

            if (!isInvalidFields) {
                loginViewModel.getLogin(username, password)
            }
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}