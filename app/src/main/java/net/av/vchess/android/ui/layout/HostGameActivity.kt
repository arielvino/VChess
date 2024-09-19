package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import net.av.vchess.R
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.Rulers.TestRuler
import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.network.Encryptor
import net.av.vchess.network.GamePendingInformer
import net.av.vchess.network.HostConnector
import net.av.vchess.network.IConnector
import net.av.vchess.reusables.PlayerColor
import kotlin.concurrent.thread

class HostGameActivity : ComponentActivity() {
    companion object {
        const val GAME_NAME_KEY = "net.av.vchess.GAME_NAME"
    }

    private lateinit var gameNameLabel: TextView
    private lateinit var messageBox: TextView
    private lateinit var connectedLabel: TextView
    private lateinit var boardHolder: ViewGroup

    private lateinit var encryptor: Encryptor
    private lateinit var game: ActualGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_game_activity)

        gameNameLabel = findViewById(R.id.game_name_label)
        messageBox = findViewById(R.id.message_box)
        connectedLabel = findViewById(R.id.connected_label)
        boardHolder = findViewById(R.id.board_holder)


        val name: String = intent.extras!!.getString(GAME_NAME_KEY)!!
        gameNameLabel.text = name

        val informer = GamePendingInformer(name)
        informer.start()

        messageBox.text = getString(R.string.waiting_for_a_player)

        game = ActualGame(TestBoardRenderer(), PlayerColor.White, TestRuler(TestBoardRenderer(), PlayerColor.White))
        val boardView = BoardView(this, game)
        boardHolder.addView(boardView)

        thread {
            val connector = HostConnector(object : IConnector.IListener {
                override fun onConnect(clientAlias: String) {
                    runOnUiThread {
                        informer.stop()
                        messageBox.text = getString(R.string.connected_to, clientAlias)
                    }
                }
            })
            @Suppress("ControlFlowWithEmptyBody")
            while (!connector.isConnected());
            encryptor = Encryptor(connector)
            encryptor.send("I'm the host.")
            runOnUiThread {
                messageBox.text = getString(R.string.received, encryptor.receive())
            }
        }


    }
}