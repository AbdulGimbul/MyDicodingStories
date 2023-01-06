package com.abdl.mydicodingstories.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.abdl.mydicodingstories.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        loginViewModel.registerResponse.observe(this) { result ->
            when (result.message) {
                "User created" -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(this@RegisterActivity, LoginActivity::class.java).also { intent ->
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                "\"email\" must be a valid email" -> {
                    Toast.makeText(this@RegisterActivity, "Email harus valid!", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal!", Toast.LENGTH_SHORT)
                        .show()
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
            if (it == "Error : \"email\" must be a valid email") {
                Toast.makeText(this, "Email tidak valid!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser() {
        with(binding) {
            val name = edtFirstName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString().trim()
            val repeatPass = edtRepeatPass.text.toString().trim()

            var isInvalidFields = false

            if (name.isEmpty()) {
                isInvalidFields = true
                edtFirstName.error = "Nama depan tidak boleh kosong"
            }

            if (!isValidEmail(email)) {
                isInvalidFields = true
                edtEmail.error = "Email tidak valid"
            }

            if (email.isEmpty()) {
                isInvalidFields = true
                edtEmail.error = "Email tidak boleh kosong"
            }

            if (pass.length < 6) {
                isInvalidFields = true
                edtPass.error = "Password minimal terdiri dari 8 karakter"
            }

            if (pass.isEmpty()) {
                isInvalidFields = true
                edtPass.error = "Password tidak boleh kosong"
            }

            if (repeatPass.isEmpty()) {
                isInvalidFields = true
                edtRepeatPass.error = "Password tidak boleh kosong"
            }

            if (repeatPass != pass) {
                isInvalidFields = true
                edtRepeatPass.error = "Password tidak boleh beda"
            }

            if (!isInvalidFields) {
                loginViewModel.getRegister(name, email, pass)
            }
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}