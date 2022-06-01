package com.ashehata.instabugtask

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}