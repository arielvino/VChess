package net.av.vchess.game.data.pieces

import net.av.vchess.game.data.Board
import net.av.vchess.game.data.Piece
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D

class Rook(
    color: PlayerColor,
    board: Board,
    location: Vector2D,
    stepsCounter: Int = 0
) : Rider(color, board, location, stepsCounter) {
    override val canCapture: Boolean = true
    override var consistentMobility: Mobility = Mobility.Normal
    override var ruledMobility: Mobility = consistentMobility
        set(value) {
            field = value
        }
    override val capturable: Boolean = true
    override val steps: List<Vector2D> =
        listOf(Vector2D(0, 1), Vector2D(0, -1), Vector2D(1, 0), Vector2D(-1, 0))

}