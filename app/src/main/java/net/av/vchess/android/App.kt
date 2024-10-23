package net.av.vchess.android

import android.app.Application
import android.content.Context
import net.av.vchess.network.lan.interfaces.Encryptor
import kotlin.concurrent.thread

class App : Application() {
    companion object {
        lateinit var appContext: Context
            private set

        var keyPair: Pair<String, String>? = null
            private set

        val encryptor = Encryptor()
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        thread {
            keyPair = net.av.vchess.network.Encryptor.RsaFactory.generateRSAKeyPair()
        }
    }
}