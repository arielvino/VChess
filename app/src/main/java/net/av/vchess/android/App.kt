package net.av.vchess.android

import android.app.Application
import android.content.Context
import net.av.vchess.network.Encryptor
import kotlin.concurrent.thread

class App : Application() {
    companion object {
        lateinit var appContext: Context
            private set

        lateinit var keyPair: Pair<String, String>
            private set
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        thread {
            keyPair = Encryptor.RsaFactory.generateRSAKeyPair()
        }
    }
}