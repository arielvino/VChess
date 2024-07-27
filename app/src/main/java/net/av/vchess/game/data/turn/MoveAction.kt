package net.av.vchess.game.data.turn

import net.av.vchess.game.data.GameRepresentation
import net.av.vchess.game.data.Piece
import net.av.vchess.reusables.Vector2D

class MoveAction(val origin: Vector2D, val destination:Vector2D) : IActionInfo {
    override fun performAction(game: GameRepresentation) {
        val piece = game.board.getTile(origin).piece
        piece!!.location = destination
        piece!!.stepsCounter++
        game.board.getTile(origin).piece = null
        game.board.getTile(destination).piece = piece
    }

    override fun revertAction(game: GameRepresentation) {
        val piece = game.board.getTile(destination).piece
        piece!!.location = origin
        piece!!.stepsCounter--
        game.board.getTile(origin).piece = piece
        game.board.getTile(destination).piece = null
    }

    override fun clone(): IActionInfo {
        return MoveAction(origin, destination)
    }
}