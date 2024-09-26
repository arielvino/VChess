package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.serialization.json.Json
import net.av.vchess.R
import net.av.vchess.android.OnePlayerBoardViewModel
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.IGameRuler
import net.av.vchess.game.data.Rulers.TestRuler
import net.av.vchess.io.XmlBordSource
import net.av.vchess.network.ClientConnector
import net.av.vchess.network.Encryptor
import net.av.vchess.network.data.NetworkGameMetadata

class JoinGameActivity : ComponentActivity() {
    companion object {
        const val IP_ADDRESS_KEY = "IP_ADDRESS"
    }

    private lateinit var boardHolder: ViewGroup
    private lateinit var messageBox: TextView

    private var connector: ClientConnector? = null
    private lateinit var encryptor: Encryptor
    private lateinit var game: ActualGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_game_activity)
        actionBar?.hide()

        boardHolder = findViewById(R.id.board_holder)
        messageBox = findViewById(R.id.message_box)

        startConnector()
    }

    private fun startConnector() {
        val ipAddress = intent.getStringExtra(IP_ADDRESS_KEY)
        connector = ClientConnector(object : ClientConnector.IListener {
            override fun onConnect() {
                startEncryptor()
            }

        }, ipAddress!!, "Fabricated nick name.")
        connector!!.start()
    }

    private fun startEncryptor() {
        messageBox.text = getString(R.string.encrypting)
        encryptor = Encryptor(object : Encryptor.IListener {
            override fun onEncryptionEstablished() {
                runOnUiThread {
                    messageBox.text = getString(R.string.encryption_established)
                    initGame()
                }
            }
        }, connector!!)
        encryptor.start()
    }

    private fun initGame() {
        val xmlBoardString = encryptor.receive()
        val boardSource = XmlBordSource(xmlBoardString)
        val networkGameMetadata: NetworkGameMetadata = Json.decodeFromString(encryptor.receive())
        var ruler: IGameRuler? = null
        if (networkGameMetadata.rulerName.contentEquals("test")) {
            ruler = TestRuler(boardSource, networkGameMetadata.currentTurn)
        }
        game = ActualGame(boardSource, networkGameMetadata.currentTurn, ruler!!)
        val boardView = BoardView(
            this@JoinGameActivity,
            game.board,
            OnePlayerBoardViewModel(game, networkGameMetadata.yourColor)
        )
        boardHolder.addView(boardView)
    }

    override fun onDestroy() {
        super.onDestroy()

        connector?.stop()
    }
}