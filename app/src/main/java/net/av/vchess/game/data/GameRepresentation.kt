package net.av.vchess.game.data

import net.av.vchess.game.data.turn.CaptureAction
import net.av.vchess.game.data.turn.MoveAction
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.io.IBoardSource
import net.av.vchess.reusables.PlayerColor

open class GameRepresentation(
    boardSource: IBoardSource,
    setCurrentTurn: PlayerColor = PlayerColor.White
) {
    var currentTurn: PlayerColor = setCurrentTurn
        private set

    fun changeTurn() {
        if (currentTurn == PlayerColor.White) {
            currentTurn = PlayerColor.Black
        } else {
            currentTurn = PlayerColor.White
        }
    }

    val historyList: ArrayList<TurnInfo> = ArrayList()
    val board: Board = boardSource.renderBoard()
    val white: Player = Player(PlayerColor.White)
    val black: Player = Player(PlayerColor.Black)

    init {
        //iterate each tile:
        for (x in 0..<board.width) {
            for (y in 0..<board.height) {
                //tile has a piece:
                val piece: Piece? = board.getTile(x, y).piece
                if (piece != null) {
                    //add the piece to a player:
                    if (piece.color == PlayerColor.White) {
                        white.addPiece(piece)
                    } else {
                        if (piece.color == PlayerColor.Black) {
                            black.addPiece(piece)
                        } else {
                            throw IllegalArgumentException("Piece's player was null.")
                        }
                    }
                }
            }
        }
    }

    fun getPlayer(piece: Piece): Player {
        return getPlayer(piece.color)
    }

    fun getPlayer(playerColor: PlayerColor): Player {
        if (playerColor == PlayerColor.White) {
            return white
        } else {
            return black
        }
    }

    open fun performTurn(turnInfo: TurnInfo) {
        for (action in turnInfo.actions) {
            action.performAction(this)
        }
        historyList.add(turnInfo)
        changeTurn()
        refreshRules()
    }

    open fun revertTurn() {
        if(historyList.isEmpty()){
            return
        }

        val lastTurn = historyList.last()
        //iterate the actions list in reverse:
        for (i in lastTurn.actions.lastIndex downTo 0) {
            val action = lastTurn.actions[i]

            action.revertAction(this)
        }
        historyList.remove(lastTurn)
        changeTurn()
        refreshRules()
    }

    fun resetRules() {
        for (x in 0..<board.width) {
            for (y in 0..<board.height) {
                val tile = board.getTile(x, y)
                tile.resetRules()
                if (tile.piece != null) {
                    tile.piece!!.resetRules()
                }
            }
        }
    }

    fun refreshRules() {
        resetRules()
        for (piece in white.pieces) {
            piece.forceRules()
        }
        for (piece in black.pieces) {
            piece.forceRules()
        }
    }
}