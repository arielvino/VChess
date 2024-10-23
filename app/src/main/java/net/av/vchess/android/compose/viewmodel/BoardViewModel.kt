package net.av.vchess.android.compose.viewmodel

import net.av.vchess.game.data.Board
import net.av.vchess.reusables.Vector2D

open class BoardViewModel(val board: Board) {
    private val tiles: ArrayList<ArrayList<TileViewModel>> = arrayListOf()

    init {
        createBoard()
    }

    private fun createBoard(){
            for (x in 0..<board.width) {
                tiles.add(arrayListOf())
                for (y in 0..<board.height) {
                    tiles[x].add(TileViewModel(board.getTile(x, y), this))
                    board.getTile(x, y).addChangesListener(tiles[x][y])
                }
            }
    }

    fun refresh(){
        for(x in 0..<board.width){
            for(y in 0..<board.height){
                tiles[x][y].refresh()
            }
        }
    }

    fun getTile(x: Int, y: Int): TileViewModel {
        return tiles[x][y]
    }

    fun getTile(location: Vector2D): TileViewModel {
        return getTile(location.x, location.y)
    }
}