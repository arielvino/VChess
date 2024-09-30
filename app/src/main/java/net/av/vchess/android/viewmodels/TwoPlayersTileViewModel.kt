package net.av.vchess.android.viewmodels

import androidx.annotation.ColorRes
import net.av.vchess.game.data.Tile
import net.av.vchess.R
import net.av.vchess.game.data.turn.CaptureAction
import net.av.vchess.game.data.turn.MoveAction
import net.av.vchess.game.data.turn.TurnInfo


open class TwoPlayersTileViewModel(tile: Tile, override val board: TwoPlayersBoardViewModel) :
    UnresponsiveTileViewModel(tile, board) {

    val turnsInfo: ArrayList<TurnInfo> = arrayListOf()

    fun addTurn(turn: TurnInfo) {
        turnsInfo.add(turn)
        refresh()
    }

    fun clearTurns() {
        turnsInfo.clear()
        refresh()
    }


    override fun onClick() {
        if (turnsInfo.size == 1) {
            board.game.performTurn(turnsInfo[0])
            board.cleanTargets()
            board.unselectAll()

            return
        }
        if (tile.piece != null) {
            if (tile.piece!!.color == board.game.currentTurn) {
                if (board.selectedTile == this) {
                    board.unselectAll()

                    return
                } else {
                    select()

                    return
                }
            }
        }
    }

    private fun select() {
        //first make the previous selected tile unselected:
        board.unselectAll()

        if (tile.piece != null) {
            board.selectedTile = this

            //TEST
            val turns = board.game.gameRuler.getLegalTurnsFrom(tile.piece!!.listUnfilteredPossibleTurns())
            for (turn in turns) {
                for (action in turn.actions) {
                    if (action is MoveAction) {
                        (board.getTile(action.destination) as TwoPlayersTileViewModel).addTurn(turn)
                        break
                    }
                    if (action is CaptureAction) {
                        (board.getTile(action.origin) as TwoPlayersTileViewModel).addTurn(turn)
                        break
                    }

                    //ToDo: add support for more Actions
                }
            }
            //TEST
        }
    }

    @ColorRes
    private fun targetColor(): Int {
        return if (isDark()) {
            R.color.tile_target_dark
        } else {
            R.color.tile_target_light
        }
    }

    override fun refresh() {
        super.refresh()

        if (turnsInfo.isNotEmpty()) {
            bodyColor = targetColor()
        }else if (board.selectedTile == this) {
            bodyColor = R.color.tile_selected
        }
        if(tile.ruledTraversability == Tile.Traversability.Normal){
            borderColor = bodyColor
        }
    }
}