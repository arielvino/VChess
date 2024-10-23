package net.av.vchess.network.lan.interfaces

import net.av.vchess.network.BinaryUtils
import net.av.vchess.network.NetworkConfiguration
import java.net.Socket
import kotlin.concurrent.thread

class ClientConnector(private val ipAddress: String) : IConnector() {
    companion object {
        const val OK_KEYWORD = "VCHESS_OK"
    }

    private var activeSocket: Socket? = null
    private val timeout: Long = 10000

    override fun read(): String {
        return BinaryUtils.readMessage(activeSocket!!.getInputStream())
    }

    override fun write(message: String) {
        BinaryUtils.sendMessage(activeSocket!!.getOutputStream(), message)
    }

    override fun connect() {
        disconnect()
        isAwaitingConnection = true
        var socket: Socket? = null

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
            if (socket == null) {
                invokeOnConnectionAbandoned()
                return@thread
            }

            thread {
                Thread.sleep(timeout)
                if (!isConnected && isAwaitingConnection) {
                    socket?.close()
                    stopWaiting()
                    invokeOnConnectionAbandoned()
                }
            }

            try {
                BinaryUtils.sendMessage(socket!!.getOutputStream(), HostConnector.CONNECT_KEYWORD)
                val response = BinaryUtils.readMessage(socket!!.getInputStream())
                if (response.contentEquals(OK_KEYWORD)) {
                    activeSocket = socket
                    stopWaiting()
                    isConnected = true
                    startLoop()
                    invokeOnConnected()
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun reconnect(
        reconnectionMessage: String,
        authenticatePeer: (reconnectMessage: String) -> Boolean
    ) {
        disconnect()
        isAwaitingConnection = true
        var socket: Socket? = null

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
            if (socket == null) {
                stopWaiting()
                invokeOnConnectionAbandoned()
                return@thread
            }

            thread {
                Thread.sleep(timeout)
                if (!isConnected) {
                    socket?.close()
                    stopWaiting()
                    invokeOnConnectionAbandoned()
                }
            }

            try {
                BinaryUtils.sendMessage(socket!!.getOutputStream(), reconnectionMessage)
                val response = BinaryUtils.readMessage(socket!!.getInputStream())
                if (authenticatePeer(response)) {
                    activeSocket = socket
                    stopWaiting()
                    isConnected = true
                    startLoop()
                    invokeOnConnected()
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun disconnect() {
        isConnected = false
        isAwaitingConnection = false
        activeSocket?.close()
        activeSocket = null
    }

    override fun stopWaiting() {
        isAwaitingConnection = false
    }
}