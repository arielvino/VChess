package net.av.vchess.android.lan

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.av.vchess.R
import net.av.vchess.android.viewmodels.OnePlayerBoardViewModel
import net.av.vchess.android.ui.layout.BoardView
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.Rulers.TestRuler
import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.io.XmlBoardParser
import net.av.vchess.network.Encryptor
import net.av.vchess.network.GamePendingInformer
import net.av.vchess.network.HostConnector
import net.av.vchess.network.NetworkGameManager
import net.av.vchess.network.data.GameInformerData
import net.av.vchess.network.data.NetworkGameMetadata
import net.av.vchess.reusables.PlayerColor
import java.security.SecureRandom

class HostGameActivity : ComponentActivity() {
    companion object {
        const val LOBBY_NAME_KEY = "LOBBY_NAME"
        const val MY_COLOR_KEY = "MY_COLOR"
    }

    private lateinit var gameNameLabel: TextView
    private lateinit var messageBox: TextView
    private lateinit var boardHolder: ViewGroup
    private lateinit var boardView: BoardView

    private var connector: HostConnector? = null
    private var informer: GamePendingInformer? = null
    private lateinit var encryptor: Encryptor
    private lateinit var networkGameManager: NetworkGameManager
    private lateinit var game: ActualGame
    private lateinit var myColor: PlayerColor
    private lateinit var rivalColor: PlayerColor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_game_activity)
        actionBar?.hide()

        gameNameLabel = findViewById(R.id.game_name_label)
        messageBox = findViewById(R.id.message_box)
        boardHolder = findViewById(R.id.board_holder)

        val lobbyName: String = intent.getStringExtra(LOBBY_NAME_KEY)!!
        gameNameLabel.text = lobbyName


        val myClearColor =
            GameInformerData.MyColorSetting.valueOf(intent.getStringExtra(MY_COLOR_KEY)!!)
        val recipientClearColor: GameInformerData.MyColorSetting = when (myClearColor) {
            GameInformerData.MyColorSetting.Random -> GameInformerData.MyColorSetting.Random
            GameInformerData.MyColorSetting.Black -> GameInformerData.MyColorSetting.White
            GameInformerData.MyColorSetting.White -> GameInformerData.MyColorSetting.Black
        }
        informer = GamePendingInformer(lobbyName, recipientClearColor)
        informer!!.start()

        messageBox.text = getString(R.string.waiting_for_a_player)

        game = ActualGame(
            object : ActualGame.IListener {
                override fun onTurnDone(color: PlayerColor, turnInfo: TurnInfo) {
                    if (color == myColor) {
                        networkGameManager.notifyTurn(turnInfo)
                    }
                }

                override fun onWin(color: PlayerColor) {
                    TODO("Not yet implemented")
                }
            },
            TestBoardRenderer(),
            PlayerColor.White,
            TestRuler(TestBoardRenderer(), PlayerColor.White)
        )
        rivalColor = when (recipientClearColor) {
            GameInformerData.MyColorSetting.Black -> PlayerColor.Black
            GameInformerData.MyColorSetting.White -> PlayerColor.White
            GameInformerData.MyColorSetting.Random -> {
                if (SecureRandom().nextInt() % 2 == 0) PlayerColor.Black else PlayerColor.White
            }
        }
        myColor = when (rivalColor) {
            PlayerColor.White -> PlayerColor.Black
            PlayerColor.Black -> PlayerColor.White
        }

        startConnector()
    }

    private fun startConnector() {
        connector = HostConnector(object : HostConnector.IListener {
            override fun onConnect(clientName: String) {
                informer!!.stop()
                messageBox.text = getString(R.string.connected_to, clientName)
                startEncryptor()
            }
        })
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
        boardView = BoardView(this, game.board, OnePlayerBoardViewModel(game, myColor))
        boardView.pointOfView = myColor
        encryptor.send(XmlBoardParser.writeBoard(boardView.boardViewModel.board))
        encryptor.send(
            Json.encodeToString(
                NetworkGameMetadata(
                    rivalColor, PlayerColor.White, "test"
                )
            )
        )
        networkGameManager = NetworkGameManager(
            object : NetworkGameManager.IListener {
                override fun onTurnDone(turnInfo: TurnInfo) {
                    if (true) {//todo: add checks
                        game.performTurn(turnInfo)
                    }
                }

                override fun onActivated() {
                    runOnUiThread {
                        boardHolder.addView(boardView)
                    }
                }

                override fun onDisconnect() {
                    TODO("Not yet implemented")
                }
            }, encryptor
        )
        networkGameManager.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        connector?.stop()
        informer?.stop()
    }
}