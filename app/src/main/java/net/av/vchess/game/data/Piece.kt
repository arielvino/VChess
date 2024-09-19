package net.av.vchess.game.data

import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D

abstract class Piece(
    val color: PlayerColor,
    val board: Board,
    location: Vector2D,
    var stepsCounter: Int
) {
    var location: Vector2D = location
        get() {
            return field
        }
        set(value) {
            if (board.hasTile(value)) {
                field = value
            } else {
                throw IndexOutOfBoundsException("Location is out of the board.")
            }
        }
    abstract val capturable: Boolean
    abstract val canCapture: Boolean
    abstract var consistentMobility: Mobility
    abstract var ruledMobility: Mobility

    abstract fun listUnfilteredPossibleTurns(): ArrayList<TurnInfo>

    /**
     * This method return true if and only if this piece can capture an enemy piece standing in the requested tile.
     * Regardless if there is currently an enemy piece there or not.
     * @param location - the coordinates of the tile to check.
     * @return - true if the piece can hit someone who stand there.
     */
    abstract fun isThreateningTile(location: Vector2D): Boolean

    fun resetRules(){
        ruledMobility = consistentMobility
    }

    fun forceRules(){}

    enum class Mobility{
        Normal,
        Frozen
    }
}