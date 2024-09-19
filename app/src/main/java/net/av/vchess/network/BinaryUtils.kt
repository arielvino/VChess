package net.av.vchess.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object BinaryUtils {

    fun readMessage(stream: InputStream): String {
        var message: String
        runBlocking {

            message = withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    val sizeBuffer = ByteArray(Int.SIZE_BYTES)
                    var totalRead = 0
                    while (totalRead < Int.SIZE_BYTES) {
                        val bytesRead =
                            stream.read(sizeBuffer, totalRead, Int.SIZE_BYTES - totalRead)
                        if (bytesRead == -1) {
                            continuation.resumeWithException(
                                IOException("Unexpected end of stream")
                            )
                            return@suspendCoroutine
                        }
                        totalRead += bytesRead
                    }
                    val messageSize = ByteBuffer.wrap(sizeBuffer).getInt()
                    val messageBytes = ByteArray(messageSize)
                    totalRead = 0
                    while (totalRead < messageSize) {
                        val bytesRead =
                            stream.read(messageBytes, totalRead, messageSize - totalRead)
                        if (bytesRead == -1) {
                            continuation.resumeWithException(
                                IOException("Unexpected end of stream")
                            )
                            return@suspendCoroutine
                        }
                        totalRead += bytesRead
                    }
                    continuation.resume(messageBytes.decodeToString())
                }
            }
        }
        return message
    }

//    fun readMessage1(stream: InputStream): String {
//        while (stream.available() < Int.SIZE_BYTES) {
//        }
//        val initialBytes = ByteArray(Int.SIZE_BYTES)
//        stream.read(initialBytes, 0, Int.SIZE_BYTES)
//        val messageSize = ByteBuffer.wrap(initialBytes).getInt()
//
//        while (stream.available() < messageSize) {
//        }
//        val messageBytes = ByteArray(messageSize)
//        stream.read(messageBytes, 0, messageSize)
//        return messageBytes.decodeToString()
//    }

     fun sendMessage(stream: OutputStream, message: String) {
         runBlocking {
             withContext(Dispatchers.IO) {
                 stream.write(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(message.length).array())
                 stream.write(message.encodeToByteArray())
             }
         }
    }
}