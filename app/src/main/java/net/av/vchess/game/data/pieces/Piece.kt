package net.av.vchess.game.data.pieces

import net.av.vchess.game.data.Board
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D

abstract class Piece {
    abstract val color: PlayerColor
    abstract var location: Vector2D
    abstract var stepsCounter: Int
    abstract val capturable: Boolean
    abstract val canCapture: Boolean
    abstract var consistentMobility: Mobility
    abstract var ruledMobility: Mobility

    abstract fun listUnfilteredPossibleTurns(board: Board): ArrayList<TurnInfo>

    /**
     * This method return true if and only if this piece can capture an enemy piece standing in the requested tile.
     * Regardless if there is currently an enemy piece there or not.
     * @param location - the coordinates of the tile to check.
     * @return - true if the piece can hit someone who stand there.
     */
    abstract fun isThreateningTile(location: Vector2D, board: Board): Boolean

    fun resetRules() {
        ruledMobility = consistentMobility
    }

    fun forceRules() {}

    override fun toString(): String {
        return "{type: ${this::class.java.simpleName}, color: ${color.name}, location: $location, consistentMobility: ${consistentMobility.name}, mobility: ${ruledMobility.name}, steps: $stepsCounter}"
    }

    enum class Mobility {
        Normal,
        Frozen
    }
}