package net.av.vchess.android

import net.av.vchess.game.data.ActualGame

class BoardViewModel(val game: ActualGame) : SimpleBoardViewModel(game.board) {
    var selectedTile: TileViewModel? = null

    override fun createBoard() {
        for (x in 0..<board.width) {
            tiles.add(arrayListOf())
            for (y in 0..<board.height) {
                tiles[x].add(TileViewModel(board.getTile(x, y), this))
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
                (tile as TileViewModel).clearTurns()
            }
        }
    }
}