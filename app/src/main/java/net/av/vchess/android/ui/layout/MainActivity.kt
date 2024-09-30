package net.av.vchess.android.ui.layout

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import net.av.vchess.android.MainMenuActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //test:
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }
}