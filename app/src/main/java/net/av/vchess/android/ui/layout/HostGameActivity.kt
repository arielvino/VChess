package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.av.vchess.R
import net.av.vchess.android.BoardViewModel
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.Rulers.TestRuler
import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.io.XmlBoardParser
import net.av.vchess.network.Encryptor
import net.av.vchess.network.GamePendingInformer
import net.av.vchess.network.HostConnector
import net.av.vchess.network.data.NetworkGameMetadata
import net.av.vchess.reusables.PlayerColor
import kotlin.concurrent.thread

class HostGameActivity : ComponentActivity() {
    companion object {
        const val GAME_NAME_KEY = "net.av.vchess.GAME_NAME"
    }

    private lateinit var gameNameLabel: TextView
    private lateinit var messageBox: TextView
    private lateinit var boardHolder: ViewGroup

    private lateinit var connector:HostConnector
    private var informer:GamePendingInformer? = null
    private lateinit var encryptor: Encryptor
    private lateinit var game: ActualGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_game_activity)
        actionBar?.hide()

        gameNameLabel = findViewById(R.id.game_name_label)
        messageBox = findViewById(R.id.message_box)
        boardHolder = findViewById(R.id.board_holder)


        val name: String = intent.extras!!.getString(GAME_NAME_KEY)!!
        gameNameLabel.text = name

        informer = GamePendingInformer(name)
        informer!!.start()

        messageBox.text = getString(R.string.waiting_for_a_player)

        game = ActualGame(TestBoardRenderer(), PlayerColor.White, TestRuler(TestBoardRenderer(), PlayerColor.White))
        val boardView = BoardView(this, game.board, BoardViewModel(game))
        boardHolder.addView(boardView)

        thread {
            connector = HostConnector(object : HostConnector.IListener {
                override fun onConnect(clientName: String) {
                    informer!!.stop()
                    runOnUiThread {
                        messageBox.text = getString(R.string.connected_to, clientName)
                    }
                }
            })
            @Suppress("ControlFlowWithEmptyBody")
            while (!connector.isConnected());
            messageBox.text=getString(R.string.encrypting)
            encryptor = Encryptor(connector)
            messageBox.text = getString(R.string.encryption_established)
            encryptor.send(XmlBoardParser.writeBoard(boardView.boardViewModel.board))
            encryptor.send(Json.encodeToString(NetworkGameMetadata(PlayerColor.Black, PlayerColor.White, "test")))
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        connector.stop()
        informer?.stop()
    }
}