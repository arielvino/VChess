package net.av.vchess.game.data.turn

import net.av.vchess.game.data.GameRepresentation
import net.av.vchess.game.data.Piece
import net.av.vchess.reusables.Vector2D

class CaptureAction(val origin: Vector2D) : IActionInfo {
    lateinit var piece: Piece

    override fun performAction(game: GameRepresentation) {
        piece = game.board.getTile(origin).piece!!
        game.board.getTile(origin).piece = null
        game.getPlayer(piece).removePiece(piece)
    }

    override fun revertAction(game: GameRepresentation) {
        game.board.getTile(origin).piece = piece
        game.getPlayer(piece).addPiece(piece)
    }

    override fun clone(): IActionInfo {
        return CaptureAction(origin)
    }
}