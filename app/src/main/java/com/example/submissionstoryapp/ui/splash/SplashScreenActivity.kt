package com.example.submissionstoryapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.submissionstoryapp.databinding.ActivitySplashScreenBinding
import com.example.submissionstoryapp.ui.auth.login.LoginActivity
import com.example.submissionstoryapp.ui.main.MainActivity
import com.example.submissionstoryapp.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val prefs = PreferencesManager(this)

        lifecycleScope.launch {
            delay(2500)
            val intent = if (prefs.exampleBoolean) {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashScreenActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }

    }
}