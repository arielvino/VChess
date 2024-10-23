package net.av.vchess.network.lan.interfaces

import net.av.vchess.network.BinaryUtils
import net.av.vchess.network.NetworkConfiguration
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class HostConnector : IConnector() {
    companion object {
        const val CONNECT_KEYWORD = "VCHESS_CONNECT"
    }

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private val timeout: Long = 40000

    override fun read(): String {
        return BinaryUtils.readMessage(clientSocket!!.getInputStream())
    }

    override fun write(message: String) {
        BinaryUtils.sendMessage(clientSocket!!.getOutputStream(), message)
    }

    override fun connect() {
        //discard any previous connection
        println("Host connecting...")
        disconnect()
        isAwaitingConnection = true

        //search for available port
        for (port in NetworkConfiguration.GamePorts) {
            try {
                println("Trying to bind to port $port.")
                serverSocket = ServerSocket(port)
                serverSocket!!.reuseAddress = true
                println("Game started at port $port.")
                break
            } catch (e: Exception) {
                println("Failed to bind to port $port.")
            }
        }
        if (serverSocket == null) {
            println("Failed to connect: No available port.")
            disconnect()
            invokeOnConnectionAbandoned()
            return
        }


        thread {
            //set timeout for listening
            thread {
                Thread.sleep(timeout)
                if (isAwaitingConnection && !isConnected) {
                    stopWaiting()
                    invokeOnConnectionAbandoned()
                }
            }
            while (isAwaitingConnection && !isConnected) {
                try {
                    val client = serverSocket!!.accept()
                    thread {
                        //set timeout for client interaction
                        thread {
                            Thread.sleep(timeout)
                            if (clientSocket != client) {
                                client.close()
                            }
                        }
                        try {
                            val message: String = BinaryUtils.readMessage(client!!.getInputStream())
                            if (message.startsWith(CONNECT_KEYWORD)) {
                                BinaryUtils.sendMessage(
                                    client.getOutputStream(),
                                    ClientConnector.OK_KEYWORD
                                )
                                clientSocket = client
                                isConnected = true
                                stopWaiting()
                                startLoop()
                                println("Connection created.")
                                invokeOnConnected()
                            }
                        } catch (_: IOException) {
                            client.close()
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    override fun reconnect(
        reconnectionMessage: String,
        authenticatePeer: (reconnectMessage: String) -> Boolean
    ) {
        //discard any previous connection
        println("Host connector reconnecting...")
        disconnect()
        isAwaitingConnection = true

        //search for available port
        for (port in NetworkConfiguration.GamePorts) {
            try {
                println("Trying to bind to port $port.")
                serverSocket = ServerSocket(port)
                serverSocket!!.reuseAddress = true
                println("Game started at port $port.")
                break
            } catch (e: Exception) {
                println("Failed to bind to port $port.")
            }
        }
        if (serverSocket == null) {
            println("Failed to connect: No available port.")
            disconnect()
            invokeOnConnectionAbandoned()
            return
        }


        thread {
            //set timeout for listening
            thread {
                Thread.sleep(timeout)
                if (isAwaitingConnection && !isConnected) {
                    stopWaiting()
                    invokeOnConnectionAbandoned()
                }
            }
            while (isAwaitingConnection && !isConnected) {
                try {
                    val client = serverSocket!!.accept()
                    thread {
                        //set timeout for client interaction
                        thread {
                            Thread.sleep(timeout)
                            if (clientSocket != client) {
                                client.close()
                            }
                        }
                        try {
                            val message: String = BinaryUtils.readMessage(client!!.getInputStream())
                            if (authenticatePeer(message)) {
                                send(reconnectionMessage)
                                clientSocket = client
                                isConnected = true
                                stopWaiting()
                                startLoop()
                                println("Connection recreated.")
                                invokeOnReconnect()
                            }
                        } catch (_: IOException) {
                            client.close()
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    override fun disconnect() {
        stopWaiting()
        isConnected = false
        clientSocket?.close()
        clientSocket = null
    }

    override fun stopWaiting() {
        isAwaitingConnection = false
        serverSocket?.close()
        serverSocket = null
    }
}