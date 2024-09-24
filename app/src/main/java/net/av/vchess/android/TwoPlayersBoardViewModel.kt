package net.av.vchess.android

import net.av.vchess.game.data.ActualGame

open class TwoPlayersBoardViewModel(val game: ActualGame) : UnresponsiveBoardViewModel(game.board) {
    init {
        createBoard()
    }

    var selectedTile: TwoPlayersTileViewModel? = null

    private fun createBoard() {
        tiles.clear()
        for (x in 0..<board.width) {
            tiles.add(arrayListOf())
            for (y in 0..<board.height) {
                tiles[x].add(TwoPlayersTileViewModel(board.getTile(x, y), this))
                board.getTile(x, y).addChangesListener(tiles[x][y])
            }
        }
    }


    fun unselectAll() {
        selectedTile = null

        cleanTargets()
    }

    fun cleanTargets() {
        for (line in tiles) {
            for (tile in line) {
                (tile as TwoPlayersTileViewModel).clearTurns()
            }
        }
    }
}