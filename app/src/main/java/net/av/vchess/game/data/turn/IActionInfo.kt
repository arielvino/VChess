package net.av.vchess.game.data.turn

import kotlinx.serialization.Serializable
import net.av.vchess.game.data.GameRepresentation

@Serializable
sealed interface IActionInfo {
    fun performAction(game: GameRepresentation)
    fun revertAction(game: GameRepresentation)
    fun clone(): IActionInfo
}