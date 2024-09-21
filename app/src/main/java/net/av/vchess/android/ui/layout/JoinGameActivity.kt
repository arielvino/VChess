package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.serialization.json.Json
import net.av.vchess.R
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.IGameRuler
import net.av.vchess.game.data.Rulers.TestRuler
import net.av.vchess.io.XmlBordSource
import net.av.vchess.network.ClientConnector
import net.av.vchess.network.Encryptor
import net.av.vchess.network.data.NetworkGameMetadata
import kotlin.concurrent.thread

class JoinGameActivity : ComponentActivity() {
    companion object {
        const val IP_ADDRESS_KEY = "IP_ADDRESS"
    }

    private lateinit var boardHolder: ViewGroup
    private lateinit var messageBox: TextView

    private var connector: ClientConnector? = null
    private lateinit var encryptor: Encryptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_game_activity)
        boardHolder = findViewById(R.id.board_holder)
        messageBox = findViewById(R.id.message_box)

        val ipAddress = intent.getStringExtra(IP_ADDRESS_KEY)

        thread {
            val connector = ClientConnector(ipAddress!!, "Fabricated nick name.")
            @Suppress("ControlFlowWithEmptyBody") while (!connector.isConnected());
            messageBox.text = getString(R.string.encrypting)
            encryptor = Encryptor(connector)
            messageBox.text = getString(R.string.encryption_established)
            val xmlBoardString = encryptor.receive()
            val boardSource = XmlBordSource(xmlBoardString)
            val networkGameMetadata: NetworkGameMetadata =
                Json.decodeFromString(encryptor.receive())
            var ruler: IGameRuler? = null
            if (networkGameMetadata.rulerName.contentEquals("test")) {
                ruler = TestRuler(boardSource, networkGameMetadata.currentTurn)
            }
            val game = ActualGame(boardSource, networkGameMetadata.currentTurn, ruler!!)
            val boardView = BoardView(this@JoinGameActivity, game)
            runOnUiThread {
                boardHolder.addView(boardView)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        connector?.stop()
    }
}