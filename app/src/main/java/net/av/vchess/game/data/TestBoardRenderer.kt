package net.av.vchess.game.data

import net.av.vchess.game.data.pieces.Queen
import net.av.vchess.game.data.pieces.Rook
import net.av.vchess.io.IBoardSource
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D

class TestBoardRenderer : IBoardSource {
    override fun renderBoard(): Board {
        val board = Board(8, 8)

        val rookW0_0 = Rook(PlayerColor.White, board, Vector2D(0, 0))
        board.getTile(rookW0_0.location).piece = rookW0_0

        val rookB0_7 = Rook(PlayerColor.Black, board, Vector2D(0, 7))
        board.getTile(rookB0_7.location).piece = rookB0_7

        val qweenW3_0 = Queen(PlayerColor.White, board, Vector2D(3, 0))
        board.getTile(qweenW3_0.location).piece = qweenW3_0

        board.getTile(3,3).consistentTraversability = Tile.Traversability.Blocked
        board.getTile(2,3).consistentTraversability = Tile.Traversability.Peaceful
        board.getTile(1,3).consistentTraversability = Tile.Traversability.OnlyPass
        board.getTile(0,3).consistentTraversability = Tile.Traversability.Blocked


        return board
    }
}