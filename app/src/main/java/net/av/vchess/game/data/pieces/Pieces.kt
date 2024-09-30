package net.av.vchess.game.data.pieces

import net.av.vchess.game.data.Piece

object Pieces {
    fun getPiecesTypes(): MutableList<Class<out Piece>> {
        return mutableListOf(
            Queen::class.java,
            Rook::class.java
            //todo: add more pieces
        )
    }
}