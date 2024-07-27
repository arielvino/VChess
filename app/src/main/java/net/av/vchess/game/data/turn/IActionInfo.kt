package net.av.vchess.game.data.turn

import net.av.vchess.game.data.GameRepresentation

interface IActionInfo {
    fun performAction(game: GameRepresentation)
    fun revertAction(game: GameRepresentation)
    fun clone(): IActionInfo
}