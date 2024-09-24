package net.av.vchess.network.data

import kotlinx.serialization.Serializable
import net.av.vchess.android.ui.layout.HostGameDialog

@Serializable
data class GameInformerData(var ipAddress:String, val recipientColor: HostGameDialog.MyColorSetting, val gameName:String){
}