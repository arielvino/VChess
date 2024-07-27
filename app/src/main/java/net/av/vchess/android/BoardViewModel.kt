package net.av.vchess.android

import androidx.lifecycle.ViewModel
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.Board
import net.av.vchess.game.data.GameRepresentation
import net.av.vchess.reusables.Vector2D

class BoardViewModel(val game: ActualGame) : ViewModel() {

    val board:Board = game.board
    val tiles: ArrayList<ArrayList<TileViewModel>> = arrayListOf()
    var selectedTile: TileViewModel? = null

    init {
        for (x in 0..<board.width) {
            tiles.add(arrayListOf())
            for (y in 0..<board.height) {
                tiles[x].add(TileViewModel(board.getTile(x, y), this))
                board.getTile(x,y).addChangesListener(tiles[x][y])
            }
        }
    }

    fun getTile(x: Int, y: Int): TileViewModel {
        return tiles[x][y]
    }

    fun getTile(location: Vector2D): TileViewModel {
        return getTile(location.x, location.y)
    }

    fun unselectAll(){
        selectedTile = null

        cleanTargets()
    }

    fun cleanTargets() {
        for (line in tiles) {
            for (tile in line) {
                tile.clearTurns()
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
}