package net.av.vchess.network

import java.io.IOException
import java.net.Socket
import java.net.InetAddress
import kotlin.concurrent.thread


class GameSearcher(private val listener: ResultCollector) {
    fun scanForGames() {
        val localIp = NetworkUtils.getLocalIpAddress() ?: return
        val networkIp = localIp.substring(0, localIp.lastIndexOf('.'))
        thread {
            for (port in NetworkConfiguration.GameInformerPorts) {
                for (y in 1..254) {
                    thread {
                        var socket: Socket? = null
                        try {
                            val ipAddress = InetAddress.getByName("${networkIp}.${y}")
                            if (!ipAddress.hostAddress.contentEquals(NetworkUtils.getLocalIpAddress())) {
                                socket = Socket(ipAddress, port)
                                BinaryUtils.sendMessage(socket.getOutputStream(), "vchess ping")
                                val fakeId = BinaryUtils.readMessage(socket.getInputStream())
                                listener.onResultFound(fakeId + " " + ipAddress.hostAddress)
                            }
                        } catch (e: IOException) {
                            print(e.stackTrace)
                        } finally {
                            socket?.close()
                            if (y == 254) {
                                listener.onFinish()
                            }
                        }
                    }
                }
            }
        }
    }


    interface ResultCollector {
        fun onResultFound(fakeId: String)
        fun onFinish()
    }
}