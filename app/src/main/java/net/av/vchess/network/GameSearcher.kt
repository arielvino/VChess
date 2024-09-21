package net.av.vchess.network

import kotlinx.serialization.json.Json
import net.av.vchess.network.data.GameInformerData
import java.io.IOException
import java.net.Socket
import java.net.InetAddress
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread


class GameSearcher(private val listener: ResultCollector) {

    private var mainThread: Thread? = null

    fun scanForGames() {
        val localIp = NetworkUtils.getLocalIpAddress()
        if (localIp == null) {
            listener.onFinish()
            return
        }
        val networkIp = localIp.substring(0, localIp.lastIndexOf('.'))
        mainThread?.interrupt()
        mainThread = thread {
            listener.onSearchStarted()
            var latch = CountDownLatch(0)
            for (port in NetworkConfiguration.GameInformerPorts) {
                for (y in 1..254) {
                    latch = CountDownLatch((latch.count + 1).toInt())
                    thread {
                        var socket: Socket? = null
                        try {
                            val ipAddress = "${networkIp}.${y}"
                            if (!ipAddress.contentEquals(NetworkUtils.getLocalIpAddress())) {
                                socket = Socket(ipAddress, port)
                                BinaryUtils.sendMessage(socket.getOutputStream(), "vchess ping")
                                val rawData = BinaryUtils.readMessage(socket.getInputStream())
                                val gameInformerData =
                                    Json.decodeFromString<GameInformerData>(rawData)
                                gameInformerData.ipAddress = ipAddress
                                listener.onResultFound(gameInformerData)
                            }
                        } catch (e: IOException) {
                            print(e.stackTrace)
                        } finally {
                            socket?.close()
                            latch.countDown()
                        }
                    }
                }
            }
            try {
                latch.await()
                listener.onFinish()
            } catch (e: InterruptedException) {
                listener.onSearchStopped()
            }
        }
    }

    fun stopScan() {
        mainThread?.interrupt()
    }


    interface ResultCollector {
        fun onSearchStarted()
        fun onResultFound(data: GameInformerData)
        fun onFinish()
        fun onSearchStopped()
    }
}