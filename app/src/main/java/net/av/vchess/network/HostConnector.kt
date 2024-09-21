package net.av.vchess.network

import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class HostConnector(private val listener: IListener) : IConnector {
    private var client: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    init {
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
                client = serverSocket!!.accept()
                var message: String
                message = BinaryUtils.readMessage(client!!.getInputStream())
                if (message.startsWith(IConnector.CONNECT_KEYWORD)) {
                    inputStream = client!!.getInputStream()
                    outputStream = client!!.getOutputStream()
                    send(IConnector.OK_KEYWORD)
                    println("Connection created.")
                    listener.onConnect(message.substring(message.indexOf(" ") + 1))
                }
            }
        }
    }

    override fun sendAsync(message: String) {
        BinaryUtils.sendMessage(outputStream!!, message)
    }

    override fun send(message: String) {
        BinaryUtils.sendMessage(outputStream!!, message)
    }

    override fun receive(): String {
        return BinaryUtils.readMessage(inputStream!!)
    }

    override fun isConnected(): Boolean {
        return outputStream != null
    }

    override fun stop() {
        client?.close()
    }

    interface IListener{
        fun onConnect(clientName:String)
    }
}