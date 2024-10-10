package net.av.vchess.network.data

import kotlinx.serialization.Serializable

@Serializable
data class GameInformerData(
    val recipientColor: MyColorSetting,
    val lobbyName: String
) {
    enum class MyColorSetting {
        Black, White, Random
    }
}
