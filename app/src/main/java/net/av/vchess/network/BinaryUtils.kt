package net.av.vchess.network

import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

object BinaryUtils {
    fun readMessage(stream: InputStream): String {
        while (stream.available() < Int.SIZE_BYTES) {
        }
        val initialBytes = ByteArray(Int.SIZE_BYTES)
        stream.read(initialBytes, 0, Int.SIZE_BYTES)
        val messageSize = ByteBuffer.wrap(initialBytes).getInt()

        while (stream.available() < messageSize) {
        }
        val messageBytes = ByteArray(messageSize)
        stream.read(messageBytes, 0, messageSize)
        return messageBytes.decodeToString()
    }

    fun sendMessage(stream: OutputStream, message: String) {
        stream.write(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(message.length).array())
        stream.write(message.encodeToByteArray())
    }
}