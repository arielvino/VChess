package net.av.vchess.game.data

import net.av.vchess.game.data.pieces.Piece
import net.av.vchess.reusables.Vector2D

class Tile(val location: Vector2D) {
    var consistentTraversability: Traversability = Traversability.Normal
        set(value) {
            field = value
            ruledTraversability = field
        }

    var piece: Piece? = null
        set(value) {
            field = value
            invokePieceChanged()
        }
    var ruledTraversability: Traversability = consistentTraversability
        set(value) {
            field = value
            invokeTraversabilityChanged()
        }

    fun resetRules() {
        ruledTraversability = consistentTraversability
    }

    enum class Traversability {
        Blocked,
        OnlyPass,
        Peaceful,
        Normal
    }

    private val changesListeners: ArrayList<ChangesListener> = arrayListOf()

    fun addChangesListener(listener: ChangesListener) {
        changesListeners.add(listener)
    }

    private fun invokeTraversabilityChanged(){
        for(listener in changesListeners){
            listener.onTraversabilityChanged()
        }
    }

    private fun invokePieceChanged(){
        for(listener in changesListeners){
            listener.onPieceChanged()
        }
    }

    interface ChangesListener {
        fun onTraversabilityChanged()
        fun onPieceChanged()
    }
}