package net.av.vchess.network

import java.io.IOException
import java.net.Socket
import java.net.InetAddress
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread


class GameSearcher(private val listener: ResultCollector) {

    private lateinit var mainThread: Thread

    fun scanForGames() {
        val localIp = NetworkUtils.getLocalIpAddress() ?: return
        val networkIp = localIp.substring(0, localIp.lastIndexOf('.'))
        mainThread = thread {
            var latch = CountDownLatch(0)
            for (port in NetworkConfiguration.GameInformerPorts) {
                for (y in 1..254) {
                    latch = CountDownLatch((latch.count + 1).toInt())
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
                            latch.countDown()
                        }
                    }
                }
            }
            try {
                latch.await()
                listener.onFinish()
            }
            catch (e:InterruptedException){
                println("Search interrupted.")
            }
        }
    }

    fun stopScan(){
        mainThread.interrupt()
    }


    interface ResultCollector {
        fun onResultFound(fakeId: String)
        fun onFinish()
    }
}