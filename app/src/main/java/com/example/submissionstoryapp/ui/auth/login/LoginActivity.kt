package com.example.submissionstoryapp.ui.auth.login

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
import com.example.submissionstoryapp.databinding.ActivityLoginBinding
import com.example.submissionstoryapp.ui.main.MainActivity
import com.example.submissionstoryapp.ui.auth.AuthViewModel
import com.example.submissionstoryapp.ui.auth.register.RegisterActivity
import com.example.submissionstoryapp.utils.NetworkResource
import com.example.submissionstoryapp.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var prefs: PreferencesManager
    private var job: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        prefs = PreferencesManager(this)

        binding.tvToRegister.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

        login()
        playAnimation()

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgPhotoLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.etLoginEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.etLoginPassword, View.ALPHA, 1f).setDuration(500)
        val buttonLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val textRegister =
            ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(email, password, buttonLogin, textRegister)
            start()
        }
    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Masukkan data yang valid", Toast.LENGTH_SHORT).show()
            } else {
                showLoading(true)
                lifecycle.coroutineScope.launchWhenResumed {
                    if (job.isActive) job.cancel()
                    job = launch {
                        viewModel.loginUser(email, password).collectLatest { result ->
                            when (result) {
                                is NetworkResource.SUCCESS -> {
                                    prefs.exampleBoolean = !result.data?.error!!
                                    prefs.token = result.data.result.token
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login Berhasil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showLoading(false)
                                }
                                is NetworkResource.LOADING -> {
                                    showLoading(true)
                                }
                                is NetworkResource.ERROR -> {
                                    showLoading(false)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "${result.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.pbLogin.visibility = View.VISIBLE
            binding.tvToRegister.visibility = View.GONE
        } else {
            binding.pbLogin.visibility = View.GONE
            binding.tvToRegister.visibility = View.VISIBLE
        }
    }
}