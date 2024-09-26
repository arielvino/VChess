package net.av.vchess.network

import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class HostConnector(private val listener: IListener) : IConnector {
    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null

    override fun start() {
        var serverSocket: ServerSocket? = null
        for (port in NetworkConfiguration.GamePorts) {
            try {
                println("Trying to bind to port $port.")
                serverSocket = ServerSocket(port)
                serverSocket.reuseAddress = true
                println("Game started at port $port.")
                break
            } catch (e: Exception) {
                println("Failed to bind to port $port.")
            }
        }
        thread {
            while (!isConnected()) {
                clientSocket = serverSocket!!.accept()
                var message: String
                message = BinaryUtils.readMessage(clientSocket!!.getInputStream())
                if (message.startsWith(IConnector.CONNECT_KEYWORD)) {
                    send(IConnector.OK_KEYWORD)
                    println("Connection created.")
                    listener.onConnect(message.substring(message.indexOf(" ") + 1))
                }
            }
        }
    }

    override fun stop() {
        clientSocket?.close()
    }

    override fun sendAsync(message: String) {
        thread {
            send(message)
        }
    }

    override fun send(message: String) {
        BinaryUtils.sendMessage(clientSocket!!.getOutputStream(), message)
    }

    override fun receive(): String {
        return BinaryUtils.readMessage(clientSocket!!.getInputStream())
    }

    override fun isConnected(): Boolean {
        return clientSocket != null
    }

    override fun isAwaitingConnection(): Boolean {
        return serverSocket != null
    }

    interface IListener {
        fun onConnect(clientName: String)
    }
}