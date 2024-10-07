package net.av.vchess.game.data.pieces

import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D

class Rook(
    override val color: PlayerColor,
    override var location: Vector2D,
    override var stepsCounter: Int = 0
) : Rider() {
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