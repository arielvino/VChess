package net.av.vchess.network

interface IConnector {
    companion object {
        const val CONNECT_KEYWORD = "VCHESS_CONNECT"
        const val OK_KEYWORD = "VCHESS_OK"
    }

    fun sendAsync(message: String)
    fun send(message: String)
    fun receive(fast: Boolean = false): String
    fun isConnected(): Boolean
    fun isAwaitingConnection(): Boolean
    fun start()
    fun stop()
}