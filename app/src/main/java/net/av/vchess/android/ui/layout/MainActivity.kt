package net.av.vchess.android.ui.layout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.network.GamePendingInformer
import net.av.vchess.network.GameSearcher
import java.security.SecureRandom
import java.util.Base64

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //test:
        setContentView(R.layout.play_lan_activity)
        val intent = Intent(this, PlayLanActivity::class.java)
        startActivity(intent)
    }
}