package com.abdl.mydicodingstories.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.abdl.mydicodingstories.databinding.ActivityLoginBinding
import com.abdl.mydicodingstories.ui.MainActivity
import com.abdl.mydicodingstories.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

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

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
            when (result?.message) {
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

        playAnimation()
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

    private fun playAnimation() {
        val appLogo = ObjectAnimator.ofFloat(binding.appLogo, View.ALPHA, 1f).setDuration(500)
        val tvLogin = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val edtUsername =
            ObjectAnimator.ofFloat(binding.edtUsernameH, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding.edtPassH, View.ALPHA, 1f).setDuration(500)
        val forgetPass =
            ObjectAnimator.ofFloat(binding.lupaPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.register, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(appLogo, tvLogin, edtUsername, edtPass, forgetPass, btnLogin, register)
            start()
        }
    }
}