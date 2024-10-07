package net.av.vchess

import net.av.vchess.game.data.GameRepresentation
import net.av.vchess.game.data.turn.MoveAction
import net.av.vchess.reusables.Vector2D

class GameTest {

    private fun stepsTest(game: GameRepresentation, index:Int = 0) {
        //define board:
        val board = game.board

        val turns = game.white.pieces[index].listUnfilteredPossibleTurns(board)
        val validPlaces: ArrayList<Vector2D> = arrayListOf()
        for (turn in turns) {
            for (action in turn.actions) {
                if (action is MoveAction) {
                    validPlaces.add(action.destination)
                }
            }
        }
        for (x in 0..<board.width) {
            for (y in 0..<board.height) {
                if (Vector2D(x, y) == game.white.pieces[index].location) {
                    print(2)
                } else {
                    if (validPlaces.contains(Vector2D(x, y))) {
                        print(1)
                    } else {
                        if (board.getTile(x, y).piece != null) {
                            print(3)
                        } else {
                            print("-")
                        }
                    }
                }
                print(" ")
            }
            print("\n")
        }
        print("\n")
    }

}