package net.av.vchess.network

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

class ClientConnector(
    private val listener: IConnector.IListener,
    ipAddress: String,
    myNickname: String
) : IConnector {
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    init {
        thread {
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
            val res = BinaryUtils.readMessage(socket!!.getInputStream())
            if (res.contentEquals(IConnector.OK_KEYWORD)) {
                inputStream = socket!!.getInputStream()
                outputStream = socket!!.getOutputStream()

                listener.onConnect(myNickname)
            }
        }
    }

    override fun sendAsync(message: String) {
        thread {
            send(message)
        }
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
        socket?.close()
    }

}