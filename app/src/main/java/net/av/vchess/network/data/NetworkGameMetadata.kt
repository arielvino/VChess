package net.av.vchess.network.data

import kotlinx.serialization.Serializable
import net.av.vchess.reusables.PlayerColor

@Serializable
data class NetworkGameMetadata(val recipientColor: PlayerColor, val currentTurn:PlayerColor, val rulerName:String)