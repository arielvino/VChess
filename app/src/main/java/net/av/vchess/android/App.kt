package net.av.vchess.android

import android.app.Application
import android.content.Context

class App:Application() {
    companion object{
        lateinit var appContext:Context
            private set
    }
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
    }
}