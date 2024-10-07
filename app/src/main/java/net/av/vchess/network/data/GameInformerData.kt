package net.av.vchess.network.data

import kotlinx.serialization.Serializable

@Serializable
data class GameInformerData(
    var ipAddress: String,
    val recipientColor: MyColorSetting,
    val gameName: String
) {
    enum class MyColorSetting {
        Black, White, Random
    }
}
