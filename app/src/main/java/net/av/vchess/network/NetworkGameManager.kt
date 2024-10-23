package net.av.vchess.network

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.network.lan.interfaces.Encryptor
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class NetworkGameManager(private val listener: IListener, private val encryptor: Encryptor) {
    companion object {
        const val DISCONNECT_KEYWORD = "VCHESS_DISCONNECT"
        const val PERFORM_TURN = "PERFORM_TURN"
        const val SEPARATOR = " "
    }

    private var active by Delegates.notNull<Boolean>()
    fun start() {
        active = true
        thread {
            while (active) {
                val message: String = encryptor.receive() ?: continue

                if (message.startsWith(DISCONNECT_KEYWORD)) {
                    stop()
                    listener.onDisconnect()
                }
                if (message.startsWith(PERFORM_TURN)) {
                    listener.onTurnDone(
                        Json.decodeFromString(
                            message.substring(
                                message.indexOf(
                                    SEPARATOR
                                ) + 1
                            )
                        )
                    )
                }
            }
        }
        listener.onActivated()
    }

    fun stop() {
        active = false
    }

    fun disconnect() {
        encryptor.send(DISCONNECT_KEYWORD)
        stop()
    }

    fun notifyTurn(turnInfo: TurnInfo) {
        encryptor.send(PERFORM_TURN + SEPARATOR + Json.encodeToString(turnInfo))
    }

    interface IListener {
        fun onActivated()
        fun onDisconnect()
        fun onTurnDone(turnInfo: TurnInfo)
    }
}