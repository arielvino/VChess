package net.av.vchess.network

import net.av.vchess.game.data.ActualGame
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class ProtocolManager {
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    lateinit var game: ActualGame

    constructor(ipAddress: String, port: Int) {
        var socket: Socket

        socket = Socket(ipAddress, port)
        inputStream = socket.getInputStream()
        outputStream = socket.getOutputStream()
    }

    constructor() {

    }

    fun connected(): Boolean {
        return inputStream != null
    }
}