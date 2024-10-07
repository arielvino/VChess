package net.av.vchess.game.data.pieces

object Pieces {
    fun getPiecesTypes(): MutableList<Class<out Piece>> {
        return mutableListOf(
            Queen::class.java,
            Rook::class.java
            //todo: add more pieces
        )
    }
}