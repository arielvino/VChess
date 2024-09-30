package net.av.vchess.android.viewmodels

import net.av.vchess.game.data.Tile
import net.av.vchess.reusables.PlayerColor

class OnePlayerTileViewModel(tile:Tile, boardViewModel: OnePlayerBoardViewModel, private val belongsTo:PlayerColor):
    TwoPlayersTileViewModel(tile, boardViewModel) {
    override fun onClick() {
            if(board.game.currentTurn == belongsTo){
                super.onClick()
            }
    }
}