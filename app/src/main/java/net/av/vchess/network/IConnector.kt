package net.av.vchess.network

interface IConnector {
    companion object{
        const val CONNECT_KEYWORD = "VCHESS_CONNECT"
        const val OK_KEYWORD = "VCHESS_OK"
    }

    fun sendAsync(message: String)
    fun send(message: String)
    fun receive(): String
    fun isConnected():Boolean

    interface IListener {
        fun onConnect(clientAlias: String)
    }

}