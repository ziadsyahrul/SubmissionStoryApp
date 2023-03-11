package com.example.submissionstoryapp.utils

import android.app.Application
import android.content.Context
import com.example.submissionstoryapp.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class MyApp : Application() {

    companion object {
        var applicationContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Companion.applicationContext = this.applicationContext
    }
}