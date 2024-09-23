package net.av.vchess.android

import net.av.vchess.game.data.Board
import net.av.vchess.reusables.Vector2D

open class SimpleBoardViewModel(val board: Board) {
    val tiles: ArrayList<ArrayList<SimpleTileViewModel>> = arrayListOf()

    init {
        createBoard()
    }

    open fun createBoard(){
            for (x in 0..<board.width) {
                tiles.add(arrayListOf())
                for (y in 0..<board.height) {
                    tiles[x].add(SimpleTileViewModel(board.getTile(x, y), this))
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

    fun getTile(x: Int, y: Int): SimpleTileViewModel {
        return tiles[x][y]
    }

    fun getTile(location: Vector2D): SimpleTileViewModel {
        return getTile(location.x, location.y)
    }
}