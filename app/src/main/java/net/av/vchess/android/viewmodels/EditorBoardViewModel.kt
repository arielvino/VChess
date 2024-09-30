package net.av.vchess.android.viewmodels

import net.av.vchess.game.data.Board
import net.av.vchess.reusables.Vector2D

class EditorBoardViewModel(val listener: IListener, board: Board) :
    UnresponsiveBoardViewModel(board) {
    var selectedLocation: Vector2D? = null
        set(value) {
            field = value
            listener.onSelectionChanged(value)
        }

    init {
        createBoard()
    }

    private fun createBoard() {
        tiles.clear()
        for (x in 0..<board.width) {
            tiles.add(arrayListOf())
            for (y in 0..<board.height) {
                tiles[x].add(EditorTileViewModel(board.getTile(x, y), this))
                board.getTile(x, y).addChangesListener(tiles[x][y])
            }
        }
    }

    interface IListener {
        fun onSelectionChanged(selectedLocation: Vector2D?)
    }
}