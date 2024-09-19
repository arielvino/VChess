package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.network.ClientConnector
import net.av.vchess.network.Encryptor
import net.av.vchess.network.IConnector
import kotlin.concurrent.thread

class JoinGameActivity : ComponentActivity() {
    companion object {
        const val IP_ADDRESS_KEY = "IP_ADDRESS"
    }

    private lateinit var boardHolder: ViewGroup
    private lateinit var messageBox: TextView

    lateinit var encryptor: Encryptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_game_activity)
        boardHolder = findViewById(R.id.board_holder)
        messageBox = findViewById(R.id.message_box)

        val ipAddress = intent.getStringExtra(IP_ADDRESS_KEY)

        thread {
            val connector = ClientConnector(
                object : IConnector.IListener {
                    override fun onConnect(clientAlias: String) {
                        runOnUiThread {
                            Toast.makeText(
                                this@JoinGameActivity,
                                "Connected!",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                }, ipAddress!!, "Fabricated nick name."
            )
            @Suppress("ControlFlowWithEmptyBody")
            while (!connector.isConnected());
            encryptor = Encryptor(connector)
            runOnUiThread {
                Toast.makeText(
                    this@JoinGameActivity,
                    encryptor.receive(),
                    Toast.LENGTH_LONG
                ).show()
            }
            encryptor.send("I'm the client.")
        }
    }
}