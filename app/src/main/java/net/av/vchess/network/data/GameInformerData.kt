package net.av.vchess.network.data

import kotlinx.serialization.Serializable
import net.av.vchess.reusables.PlayerColor

@Serializable
data class GameInformerData(var ipAddress:String, val recipientColor: PlayerColor? = null, val gameName:String){
}