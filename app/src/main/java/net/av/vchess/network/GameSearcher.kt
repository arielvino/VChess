package net.av.vchess.network

import kotlinx.serialization.json.Json
import net.av.vchess.network.data.GameInformerData
import java.io.IOException
import java.net.Socket
import java.net.InetSocketAddress
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
            val latch = CountDownLatch(254*NetworkConfiguration.GameInformerPorts.size)
            for (port in NetworkConfiguration.GameInformerPorts) {
                for (y in 1..254) {
                    thread {
                        val socket = Socket()
                        val ipAddress = "${networkIp}.${y}"
                        try {
                            socket.connect(InetSocketAddress(ipAddress, port), 2000)
                            BinaryUtils.sendMessage(
                                socket.getOutputStream(),
                                GamePendingInformer.PING_KEYWORD
                            )
                            val rawData = BinaryUtils.readMessage(socket.getInputStream())
                            val gameInformerData =
                                Json.decodeFromString<GameInformerData>(rawData)
                            gameInformerData.ipAddress = ipAddress
                            listener.onResultFound(gameInformerData)
                        } catch (_: IOException) {
                        } finally {
                            if (socket.isConnected) {
                                println("Successful: $ipAddress:$port")
                            } else {
                                println("Failed: $ipAddress:$port")
                            }
                            socket.close()
                            latch.countDown()
                            println("Remaining threads: ${latch.count}")
                        }
                    }
                }
            }
            try {
                println("Latch is awaiting ${latch.count} threads.")
                latch.await()
                listener.onFinish()
                println("Search ended.")
            } catch (e: InterruptedException) {
                println("Searcher stopped.")
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
    }
}