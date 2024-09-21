package net.av.vchess.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import net.av.vchess.network.data.GameInformerData
import net.av.vchess.reusables.PlayerColor
import java.net.ServerSocket
import kotlin.concurrent.thread

class GamePendingInformer(private val gameName: String, private val recipientColor:PlayerColor? = null) {
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
                if(!available){
                    break
                }
                thread {
                    val message = BinaryUtils.readMessage(clientSocket.getInputStream())
                    if (message.contentEquals("vchess ping")) {
                        println("Ping received.")
                        val serializedMessage:String = Json.encodeToString(GameInformerData("", recipientColor, gameName))
                        BinaryUtils.sendMessage(clientSocket.getOutputStream(), serializedMessage)
                        println("Game informer data sent.")
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