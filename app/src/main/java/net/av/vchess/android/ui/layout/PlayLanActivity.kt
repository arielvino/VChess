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

class PlayLanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.play_lan_activity)

        val startGameButton = findViewById<Button>(R.id.start_game_button)
        startGameButton.setOnClickListener {
            val intent = Intent(this, HostGameDialog::class.java)
            startActivity(intent)
        }

        val searchGamesButton = findViewById<Button>(R.id.search_games_button)
        searchGamesButton.setOnClickListener {
            val intent = Intent(this, ScanGamesActivity::class.java)
            startActivity(intent)
        }
    }
}