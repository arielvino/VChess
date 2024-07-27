package net.av.vchess.network

import java.net.ServerSocket
import kotlin.concurrent.thread

class GamePendingInformer(private val fakeId: String) {
    private val port = 50005
    private val serverSocket = ServerSocket(port)
    private var available = true

    fun start() {
        thread {
            while (available) {
                val clientSocket = serverSocket.accept()
                thread {
                    val message = BinaryUtils.readMessage(clientSocket.getInputStream())
                    if (message.contentEquals("vchess ping")) {
                        BinaryUtils.sendMessage(clientSocket.getOutputStream(), fakeId)
                    }
                    clientSocket.close()
                }
            }
            serverSocket.close()
        }
    }

    fun stop(){
        available = false
    }
}