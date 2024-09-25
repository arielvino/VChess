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

    /**
     * Blocking operation that read a message from an inputStream. The byte length of the message must be attached before the start of the message, as 4 byte Int.
     * @param stream - The inputStream you want to read from.
     * @param fast - Indicates that this read operation need to be immediate. If fast is false - a sleep command is invoked between each iteration of the loops, in order to reduce intensity.
     */
    fun readMessage(stream: InputStream, fast:Boolean = false): String {
        var message: String
        runBlocking {

            message = withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    val sizeBuffer = ByteArray(Int.SIZE_BYTES)
                    var totalRead = 0
                    while (totalRead < Int.SIZE_BYTES) {
                        if(!fast){
                            Thread.sleep(500)
                        }
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
                        if(!fast){
                            Thread.sleep(500)
                        }
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

     fun sendMessage(stream: OutputStream, message: String) {
         runBlocking {
             withContext(Dispatchers.IO) {
                 stream.write(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(message.length).array())
                 stream.write(message.encodeToByteArray())
             }
         }
    }
}