package net.av.vchess.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import net.av.vchess.android.lan.HostGameDialog
import net.av.vchess.network.data.GameInformerData
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class GamePendingInformer(private val gameName: String, private val recipientColor: HostGameDialog.MyColorSetting = HostGameDialog.MyColorSetting.Random) {
    companion object{
        const val PING_KEYWORD = "VCHESS_PING"
    }

    private lateinit var serverSocket:ServerSocket

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
            while (true) {
                var clientSocket :Socket
                try {
                    clientSocket = serverSocket.accept()
                }
                catch (e: Exception){
                    break
                }
                thread {
                    val message = BinaryUtils.readMessage(clientSocket.getInputStream())
                    if (message.contentEquals(PING_KEYWORD)) {
                        println("Ping received.")
                        val serializedMessage:String = Json.encodeToString(GameInformerData("", recipientColor, gameName))
                        BinaryUtils.sendMessage(clientSocket.getOutputStream(), serializedMessage)
                        println("Game informer data sent.")
                    }
                    clientSocket.close()
                }
            }
            println("Game informer stopped.")
        }
    }

    fun stop(){
        serverSocket.close()
    }
}