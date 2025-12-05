package com.aghyy.gameflix

import android.app.Application

class GameflixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: GameflixApplication

        val appContext get() = instance.applicationContext
    }
}

