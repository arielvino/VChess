package net.av.vchess.game.data

import net.av.vchess.game.data.pieces.Piece
import net.av.vchess.reusables.PlayerColor

class Player(val color:PlayerColor) {
    val pieces:ArrayList<Piece> = arrayListOf()

    fun addPiece(piece: Piece){
        pieces.add(piece)
    }

    fun removePiece(piece: Piece){
        pieces.remove(piece)
    }
}