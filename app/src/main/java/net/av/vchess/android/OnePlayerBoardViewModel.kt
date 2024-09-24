package net.av.vchess.android

import net.av.vchess.game.data.ActualGame
import net.av.vchess.reusables.PlayerColor

class OnePlayerBoardViewModel(game: ActualGame, private  val belongsTo:PlayerColor):TwoPlayersBoardViewModel(game) {

    init {
        createBoard()
    }
    private fun createBoard() {
        tiles.clear()
        for (x in 0..<board.width) {
            tiles.add(arrayListOf())
            for (y in 0..<board.height) {
                tiles[x].add(OnePlayerTileViewModel(board.getTile(x, y), this, belongsTo))
                board.getTile(x, y).addChangesListener(tiles[x][y])
            }
        }
    }
}