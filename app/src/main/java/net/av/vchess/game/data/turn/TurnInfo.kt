package net.av.vchess.game.data.turn

import kotlinx.serialization.Serializable
import net.av.vchess.reusables.Vector2D

@Serializable
class TurnInfo(val originLocation: Vector2D) {
    val actions: ArrayList<IActionInfo> = arrayListOf()

    fun addAction(action: IActionInfo) {
        actions.add(action)
    }

    fun clone(): TurnInfo {
        val copy = TurnInfo(originLocation)

        for(action in actions){
            copy.addAction(action)
        }

        return copy
    }
}