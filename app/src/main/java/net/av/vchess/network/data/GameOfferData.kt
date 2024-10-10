package net.av.vchess.network.data

data class GameOfferData(val ipAddress:String, val lobbyName:String, val recipientColorSetting: GameInformerData.MyColorSetting)