package net.av.vchess.network.lan.interfaces

import java.util.LinkedList
import kotlin.concurrent.thread

abstract class IConnector {
    private val listeners = mutableListOf<IListener>()
    private val inputMessages = LinkedList<String>()
    private val outputMessages = LinkedList<String>()
    protected var isConnected = false
    protected var isAwaitingConnection = false

    protected abstract fun read(): String
    protected abstract fun write(message: String)

    abstract fun connect()
    abstract fun reconnect(
        reconnectionMessage: String,
        authenticatePeer: (reconnectMessage: String) -> Boolean
    )

    abstract fun disconnect()
    protected abstract fun stopWaiting()

    fun send(message: String) {
        outputMessages.add(message)
    }

    fun receive(): String? {
        return if (inputMessages.size > 0) {
            inputMessages.poll()
        } else {
            null
        }
    }

    protected fun startLoop() {
        thread {
            while (isConnected) {
                try {
                    inputMessages.add(read())
                } catch (ex: Exception) {
                    isConnected = false
                    println("Connector: error reading")
                    invokeOnConnectionLost()
                }
            }
        }
        thread {
            while (isConnected) {
                if (outputMessages.size > 0) {
                    val message = outputMessages.element()
                    try {
                        write(message)
                        outputMessages.remove(message)
                    } catch (ex: Exception) {
                        isConnected = false
                        println("Connector: error writing")
                        invokeOnConnectionLost()
                    }
                }
            }
        }
    }

    fun addListener(listener: IListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    protected fun invokeOnConnected() {
        for (l in listeners) {
            l.onConnected()
        }
    }

    protected fun invokeOnReconnect() {
        for (l in listeners) {
            l.onReconnected()
        }
    }

    protected fun invokeOnConnectionLost() {
        for (l in listeners) {
            l.onConnectionLost()
        }
    }

    protected fun invokeOnConnectionAbandoned() {
        for (l in listeners) {
            l.onConnectionAbandoned()
        }
    }

    interface IListener {
        fun onConnected()
        fun onReconnected()
        fun onConnectionLost()
        fun onConnectionAbandoned()
    }
}