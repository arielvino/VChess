package net.av.vchess.network

import java.net.ServerSocket
import kotlin.concurrent.thread

class GamePendingInformer(private val fakeId: String) {
    private lateinit var serverSocket:ServerSocket
    private var available = true

    fun start() {
        for(port in NetworkConfiguration.GameInformerPorts){
            try {
                println("Trying to connect to port $port...")
                serverSocket = ServerSocket(port)
                println("Game informer started on port $port.")
                break
            }
            catch (e:  Exception){
                println("Failed to connect to port $port.")
            }
        }
        thread {
            while (available) {
                val clientSocket = serverSocket.accept()
                thread {
                    val message = BinaryUtils.readMessage(clientSocket.getInputStream())
                    if (message.contentEquals("vchess ping")) {
                        println("Ping received.")
                        BinaryUtils.sendMessage(clientSocket.getOutputStream(), fakeId)
                        println("Data sent.")
                    }
                    clientSocket.close()
                }
            }
            serverSocket.close()
            println("Game informer stopped.")
        }
    }

    fun stop(){
        available = false
    }
}