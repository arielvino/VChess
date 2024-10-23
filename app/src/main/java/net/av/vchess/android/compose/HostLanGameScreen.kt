package net.av.vchess.android.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.av.vchess.android.App
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.io.XmlBoardParser
import net.av.vchess.network.GamePendingInformer
import net.av.vchess.network.NetworkGameManager
import net.av.vchess.network.data.GameInformerData
import net.av.vchess.network.data.GameSettings
import net.av.vchess.network.data.NetworkGameMetadata
import net.av.vchess.network.lan.interfaces.Encryptor
import net.av.vchess.reusables.PlayerColor
import java.security.SecureRandom

fun Modifier.gesturesDisabled(disabled: Boolean = true) = if (disabled) {
    pointerInput(Unit) {
        awaitPointerEventScope {
            // we should wait for all new pointer events
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach(
                    PointerInputChange::consume
                )
            }
        }
    }
} else {
    this
}

/*
@Composable
fun HostGameWaitingRoom(data: GameInformerData) {
    val message = rememberSaveable { mutableStateOf("Waiting for a player...") }
    Text(text = message.value)

    App.encryptor.addListener(object : Encryptor.IListener {
        override fun onConnected() {
            App.informer!!.stop()

            TODO("initialize game")
        }

        override fun onReconnected() {
            message.value = "Connection restored."

            TODO("Not yet implemented")
        }

        override fun onConnectionLost() {
            message.value = "Connection interrupted."

            TODO("Not yet implemented")
        }

        override fun onConnectionAbandoned() {
            App.informer?.stop()
            App.runningGame = null
            message.value = "Error starting game."
        }

    })

    LaunchedEffect(key1 = Unit) {

        App.encryptor.startHost()

        App.informer = GamePendingInformer(data)
        App.informer!!.start()

    }
}
*/

@Composable
fun HostLanGameScreen(data: GameInformerData, onBackPressed: () -> Unit) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val game: MutableState<ActualGame?> = remember { mutableStateOf(null) }
    val myColor = rememberSaveable { mutableStateOf(PlayerColor.White) }
    var initializedGame: ActualGame? = null
    var networkGameManager: NetworkGameManager? = null


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .gesturesDisabled(showDialog.value)
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            //var connector: HostConnector? = null
            var informer: GamePendingInformer? = null
            lateinit var encryptor: Encryptor
            lateinit var rivalColor: PlayerColor

            val message = rememberSaveable {
                mutableStateOf("")
            }

            Text(
                text = message.value,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.scale(if (message.value.contentEquals("")) 0f else 1f)
            )

            GameBoard(
                game.value,
                isReactive = { color: PlayerColor -> color == myColor.value },
                pointOfView = myColor.value
            )

            fun initGame() {
                encryptor.send(XmlBoardParser.writeBoard(initializedGame!!.board))
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
                                game.value!!.performTurn(turnInfo)
                            }
                        }

                        override fun onActivated() {
                            game.value = initializedGame
                        }

                        override fun onDisconnect() {
                            networkGameManager!!.stop()
                            message.value = "Your opponent left"
                        }
                    }, encryptor
                )
                networkGameManager!!.start()
            }

            fun startEncryptor() {
                message.value = "Encrypting..."
                encryptor = Encryptor()
                encryptor.addListener(object : Encryptor.IListener {

                    override fun onConnected() {
                        message.value = "Encryption established!"
                        initGame()
                    }

                    override fun onReconnected() {
                        message.value = "Connection restored"
                    }

                    override fun onConnectionLost() {
                        message.value = "Connection lost"
                    }

                    override fun onConnectionAbandoned() {
                        message.value = "Game abandoned"
                    }
                })
                encryptor.startHost()
            }

            LaunchedEffect(key1 = Unit) {
                informer = GamePendingInformer(data)
                informer!!.start()

                message.value = "Waiting for a player..."

                val boardSource = TestBoardRenderer()
                val firstPlayer = PlayerColor.White

                initializedGame = ActualGame(
                    object : ActualGame.IListener {
                        override fun onTurnDone(color: PlayerColor, turnInfo: TurnInfo) {
                            if (color == myColor.value) {
                                networkGameManager!!.notifyTurn(turnInfo)
                            }
                        }

                        override fun onWin(color: PlayerColor) {
                            TODO("Not yet implemented")
                        }
                    }, GameSettings(
                        boardSource, "test", firstPlayer
                    )
                )
                rivalColor = when (data.recipientColor) {
                    GameInformerData.MyColorSetting.Black -> PlayerColor.Black
                    GameInformerData.MyColorSetting.White -> PlayerColor.White
                    GameInformerData.MyColorSetting.Random -> {
                        if (SecureRandom().nextInt() % 2 == 0) PlayerColor.Black else PlayerColor.White
                    }
                }
                myColor.value = when (rivalColor) {
                    PlayerColor.White -> PlayerColor.Black
                    PlayerColor.Black -> PlayerColor.White
                }

                startEncryptor()
            }

            DisposableEffect(key1 = Unit) {
                onDispose {
                    encryptor.disconnect()
                    informer?.stop()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = if (showDialog.value) MaterialTheme.colorScheme.background.copy(
                    alpha = 0.5f
                ) else Color(0x00000000)
            )
            .scale(if (showDialog.value) 1f else 0f)
            .padding(20.dp),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .wrapContentSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Are you sure you want to leave?",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Justify)
            )
            MenuButton(text = "Leave", onClick = {
                networkGameManager?.disconnect()
                onBackPressed() })
            MenuButton(text = "Stay", onClick = { showDialog.value = !showDialog.value })
        }
    }

    BackHandler {
        showDialog.value = !showDialog.value
    }
}