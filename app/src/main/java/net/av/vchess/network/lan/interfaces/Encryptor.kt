package net.av.vchess.network.lan.interfaces

import net.av.vchess.android.App
import net.av.vchess.network.Encryptor.AesFactory
import net.av.vchess.network.Encryptor.RsaFactory
import kotlin.concurrent.thread

class Encryptor {
    private val listeners = mutableListOf<IListener>()
    private var connector: IConnector? = null
    private var symmetricKey: String? = null
    private var isConnected = false
    private var isAwaitingConnection = false

    fun startHost() {
        println("starting host")
        disconnect()

        connector = HostConnector()
        connector?.addListener(object : IConnector.IListener {
            override fun onConnected() {
                thread {
                    Thread.sleep(40000)
                    if (isAwaitingConnection) {
                        println("connection timeout")
                        disconnect()
                        invokeOnConnectionAbandoned()
                    }
                }
                thread {
                    isAwaitingConnection = true

                    try {
                        val keyPair = App.keyPair!!
                        println("Key pair generated.")
                        connector!!.send(keyPair.first)
                        println("Public key sent.")
                        var encryptedKey: String? = null
                        while (encryptedKey == null && isAwaitingConnection) {
                            encryptedKey = connector!!.receive()
                        }
                        println("Symmetric Key received: $encryptedKey.")
                        symmetricKey =
                            RsaFactory.decryptWithPrivateKey(encryptedKey!!, keyPair.second)
                        println("Symmetric key decrypted and saved.")
                        isAwaitingConnection = false
                        invokeOnConnected()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("error establishing encryption")
                        disconnect()
                        invokeOnConnectionAbandoned()
                    }
                }
            }

            override fun onReconnected() {
                invokeOnReconnect()
            }

            override fun onConnectionLost() {
                if (symmetricKey == null) {
                    println("connection lost while establishing encryption")
                    disconnect()
                    invokeOnConnectionAbandoned()
                } else {
                    invokeOnConnectionLost()
                    reconnectHost()
                }
            }

            override fun onConnectionAbandoned() {
                println("connection abandoned while establishing encryption")
                disconnect()
                invokeOnConnectionAbandoned()
            }

        })
        connector!!.connect()
    }

    fun startClient(ipAddress: String) {
        println("starting client")
        disconnect()

        isAwaitingConnection = true
        connector = ClientConnector(ipAddress)
        connector!!.addListener(object : IConnector.IListener {
            override fun onConnected() {
                thread {
                    Thread.sleep(10000)
                    if (isAwaitingConnection) {
                        println("connection timeout")
                        disconnect()
                        invokeOnConnectionAbandoned()
                    }
                }
                thread {
                    try {
                        var publicKey: String? = null
                        while (publicKey == null && isAwaitingConnection) {
                            publicKey = connector!!.receive()
                        }
                        println("Public key received: $publicKey")
                        symmetricKey = AesFactory.generateAESKey()
                        println("Symmetric key generated.")
                        val encryptedKey =
                            RsaFactory.encryptWithPublicKey(symmetricKey!!, publicKey!!)
                        connector!!.send(encryptedKey)
                        println("Symmetric key sent.")
                        isAwaitingConnection = false
                        invokeOnConnected()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("error while establishing encryption")
                        disconnect()
                        invokeOnConnectionAbandoned()
                    }
                }
            }

            override fun onReconnected() {
                invokeOnReconnect()
            }

            override fun onConnectionLost() {
                if (symmetricKey == null) {
                    println("connection lost while establishing encryption")
                    disconnect()
                    invokeOnConnectionAbandoned()
                } else {
                    invokeOnConnectionLost()
                    reconnectClient()
                }
            }

            override fun onConnectionAbandoned() {
                println("connection abandoned while establishing encryption")
                disconnect()
                invokeOnConnectionAbandoned()
            }
        })

        connector!!.connect()
    }

    fun reconnectHost() {
        connector?.reconnect(
            AesFactory.encryptWithAESGCM(
                ClientConnector.OK_KEYWORD, symmetricKey!!
            )
        ) { reconnectMessage: String ->
            AesFactory.decryptWithAESGCM(
                reconnectMessage, symmetricKey!!
            ) == HostConnector.CONNECT_KEYWORD
        }
    }

    fun reconnectClient() {
        connector?.reconnect(
            AesFactory.encryptWithAESGCM(
                HostConnector.CONNECT_KEYWORD, symmetricKey!!
            )
        ) { reconnectMessage: String ->
            AesFactory.decryptWithAESGCM(
                reconnectMessage, symmetricKey!!
            ) == ClientConnector.OK_KEYWORD
        }
    }

    fun disconnect() {
        println("Disconnected")
        isConnected = false
        isAwaitingConnection = false
        connector?.disconnect()
        connector = null
        symmetricKey = null
    }

    fun send(message: String) {
        connector!!.send(AesFactory.encryptWithAESGCM(message, symmetricKey!!))
    }

    fun receive(): String? {
        val message = connector!!.receive()
        return if (message == null) {
            null
        } else {
            AesFactory.decryptWithAESGCM(message, symmetricKey!!)
        }
    }

    fun addListener(listener: IListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    private fun invokeOnConnected() {
        for (l in listeners) {
            l.onConnected()
        }
    }

    private fun invokeOnReconnect() {
        for (l in listeners) {
            l.onReconnected()
        }
    }

    private fun invokeOnConnectionLost() {
        for (l in listeners) {
            l.onConnectionLost()
        }
    }

    private fun invokeOnConnectionAbandoned() {
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