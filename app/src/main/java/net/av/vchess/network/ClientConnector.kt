package net.av.vchess.network

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

class ClientConnector(
    private val listener: IListener,
    private val ipAddress: String,
    private val myNickname: String
) : IConnector {

    private var activeSocket: Socket? = null


    override fun sendAsync(message: String) {
        thread {
            send(message)
        }
    }

    override fun send(message: String) {
        BinaryUtils.sendMessage(activeSocket!!.getOutputStream(), message)
    }

    override fun receive(): String {
        return BinaryUtils.readMessage(activeSocket!!.getInputStream())
    }

    override fun isConnected(): Boolean {
        return activeSocket != null
    }

    override fun isAwaitingConnection(): Boolean {
        TODO("Not yet implemented")
    }

    override fun start() {
        thread {
            var socket: Socket? = null
            for (port in NetworkConfiguration.GamePorts) {
                try {
                    println("Trying to connect on port $port...")
                    socket = Socket(ipAddress, port)
                    println("Connected to remote port $port.")
                    break
                } catch (e: Exception) {
                    println("Failed to connect on port $port.")
                }
            }

            //test
            BinaryUtils.sendMessage(
                socket!!.getOutputStream(),
                "${IConnector.CONNECT_KEYWORD} $myNickname"
            )
            val res = BinaryUtils.readMessage(socket.getInputStream())
            if (res.contentEquals(IConnector.OK_KEYWORD)) {
                activeSocket = socket
                listener.onConnect()
            }
        }
    }

    override fun stop() {
        activeSocket?.close()
    }

    interface IListener {
        fun onConnect()
    }
}