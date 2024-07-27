package net.av.vchess.game.data.Rulers

import net.av.vchess.game.data.GameRepresentation
import net.av.vchess.game.data.IGameRuler
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.io.IBoardSource
import net.av.vchess.reusables.PlayerColor

class TestRuler(boardSource: IBoardSource, startingAtTurnOf: PlayerColor) : IGameRuler {

    val emulatorGame = GameRepresentation(boardSource, startingAtTurnOf)

    override fun getLegalTurnsFrom(turns: ArrayList<TurnInfo>): ArrayList<TurnInfo> {
        val result: ArrayList<TurnInfo> = turns.clone() as ArrayList<TurnInfo>

        //filtering here:
        for (turn in turns) {
            emulatorGame.performTurn(turn)

            //check here:
            for (x in 0..<emulatorGame.board.width) {
                for (y in 0..<emulatorGame.board.height) {

                    if (emulatorGame.board.getTile(x, y).piece != null) {
                        val checkedPiece = emulatorGame.board.getTile(x, y).piece

                        for (a in 0..<emulatorGame.board.width) {
                            for (b in 0..<emulatorGame.board.height) {

                                if (emulatorGame.board.getTile(a, b).piece != null) {
                                    val comparedPiece = emulatorGame.board.getTile(a, b).piece

                                    val sameType =
                                        checkedPiece!!.javaClass.name == comparedPiece!!.javaClass.name
                                    if (sameType) {
                                        val xEqual =
                                            checkedPiece.location.x == comparedPiece.location.x
                                        val yEqual =
                                            checkedPiece.location.y == comparedPiece.location.y
                                        if (xEqual.xor(yEqual)) {
                                            result.remove(turn)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            emulatorGame.revertTurn()
        }

        return result
    }

    override fun performTurn(turnDone: TurnInfo) {
        val myTurn = turnDone.clone()
        emulatorGame.performTurn(myTurn)
    }

    override fun revertTurn() {
        emulatorGame.revertTurn()
    }
}