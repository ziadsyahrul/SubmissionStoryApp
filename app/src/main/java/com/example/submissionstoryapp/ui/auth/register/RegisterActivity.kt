package com.example.submissionstoryapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.coroutineScope
import com.example.submissionstoryapp.databinding.ActivityRegisterBinding
import com.example.submissionstoryapp.ui.auth.AuthViewModel
import com.example.submissionstoryapp.ui.auth.login.LoginActivity
import com.example.submissionstoryapp.utils.NetworkResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private var job: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.tvToLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        register()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgPhotoRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.etUsername, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val buttonLogin =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val toLogin =
            ObjectAnimator.ofFloat(binding.tvToLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(name, email, password, buttonLogin, toLogin)
            start()
        }
    }

    private fun register() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(
                    password
                )
            ) {
                Toast.makeText(this, "Mohon lengkapi data terlebih dahulu", Toast.LENGTH_SHORT)
                    .show()
            } else {
                lifecycle.coroutineScope.launchWhenResumed {
                    if (job.isActive) job.cancel()
                    job = launch {
                        viewModel.registerUser(username, email, password).collect { result ->
                            when (result) {
                                is NetworkResource.SUCCESS -> {
                                    startActivity(
                                        Intent(
                                            this@RegisterActivity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Register Berhasil",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                }
                                is NetworkResource.LOADING -> {

                                }
                                is NetworkResource.ERROR -> {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Register Gagal",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}