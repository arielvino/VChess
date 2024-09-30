package net.av.vchess.android.viewmodels

import net.av.vchess.game.data.Tile
import net.av.vchess.R

class EditorTileViewModel(tile: Tile, boardViewModel: EditorBoardViewModel):UnresponsiveTileViewModel(tile, boardViewModel) {
    override fun onClick() {
        board as EditorBoardViewModel
        if(board.selectedLocation == tile.location){
            board.selectedLocation = null
            refresh()
        }
        else{
            if(board.selectedLocation != null){
                val previous = board.getTile(board.selectedLocation!!)
                board.selectedLocation = null
                previous.refresh()
            }
            board.selectedLocation = tile.location
            refresh()
        }
    }

    override fun refresh() {
        super.refresh()
        board as EditorBoardViewModel
        if(board.selectedLocation == tile.location){
            bodyColor = R.color.tile_selected
        }
        if(tile.ruledTraversability == Tile.Traversability.Normal){
            borderColor = bodyColor
        }
    }
}