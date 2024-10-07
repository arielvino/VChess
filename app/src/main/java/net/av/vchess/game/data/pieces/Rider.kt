package net.av.vchess.game.data.pieces

import net.av.vchess.game.data.Board
import net.av.vchess.game.data.Tile
import net.av.vchess.game.data.turn.MoveAction
import net.av.vchess.game.data.turn.CaptureAction
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.reusables.Vector2D

abstract class Rider :
    Piece() {

    abstract val steps: List<Vector2D>

    override fun listUnfilteredPossibleTurns(board: Board): ArrayList<TurnInfo> {
        val turns: ArrayList<TurnInfo> = arrayListOf()

        //if the piece is immobile:
        if (ruledMobility == Mobility.Frozen) {
            return turns
        }

        for (step in steps) {
            var testedLocation = location + step

            while (board.hasTile(testedLocation)) {
                //move to an empty tile:
                val tile: Tile = board.getTile(testedLocation)
                val piece: Piece? = tile.piece
                if (piece == null) {
                    //if tile rule allow landing:
                    if (tile.ruledTraversability == Tile.Traversability.Normal || tile.ruledTraversability == Tile.Traversability.Peaceful) {
                        val action = MoveAction(location, testedLocation)
                        val turn = TurnInfo(testedLocation)
                        turn.addAction(action)
                        turns.add(turn)
                    }
                    //if tile rule allow passing:
                    if (tile.ruledTraversability == Tile.Traversability.Normal || tile.ruledTraversability == Tile.Traversability.Peaceful || tile.ruledTraversability == Tile.Traversability.OnlyPass) {
                        //continue in the same direction:
                        testedLocation += step
                    } else {
                        //if you can not pass further - stop advancing in that direction:
                        break
                    }
                }
                //tile has a piece:
                else {
                    //enemy piece:
                    if (piece.color != color) {
                        //capture:
                        if (piece.capturable) {
                            if (tile.ruledTraversability == Tile.Traversability.Normal) {
                                val captureAction =
                                    CaptureAction(
                                        testedLocation
                                    )
                                val moveAction =
                                    MoveAction(location, testedLocation)
                                val turn = TurnInfo(testedLocation)
                                turn.addAction(captureAction)
                                turn.addAction(moveAction)
                                turns.add(turn)
                            }
                        }

                    }
                    //stop advancement:
                    break
                }
            }
        }

        return turns
    }

    override fun isThreateningTile(location: Vector2D, board: Board): Boolean {
        if (!canCapture) {
            return false
        }
        if (board.getTile(location).ruledTraversability != Tile.Traversability.Normal) {
            return false
        }
        if (ruledMobility == Mobility.Frozen) {
            return false
        }

        val distance = Vector2D(location.x - this.location.x, location.y - this.location.y)
        for (step in steps) {
            if (step.sameDirection(distance)) {
                var checkedLocation: Vector2D = location + step
                while (board.hasTile(checkedLocation)) {
                    //can reach the tile. Don't care who currently occupying the tile, if any.
                    if (checkedLocation == location) {
                        return true
                    } else {
                        val tile = board.getTile(checkedLocation)
                        val piece = tile.piece

                        //tile is empty, go ahead:
                        if (piece == null && tile.ruledTraversability != Tile.Traversability.Blocked) {
                            checkedLocation += step
                            continue
                        }

                        //the way is blocked:
                        return false
                    }
                }
            }
        }
        return false
    }
}