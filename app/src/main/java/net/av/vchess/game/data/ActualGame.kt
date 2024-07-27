package net.av.vchess.game.data

import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.io.IBoardSource
import net.av.vchess.reusables.PlayerColor

class ActualGame(boardSource: IBoardSource, startingAtTurnOf: PlayerColor, val gameRuler: IGameRuler) : GameRepresentation(boardSource, startingAtTurnOf) {
      override fun performTurn(turnInfo: TurnInfo){
          super.performTurn(turnInfo)
          gameRuler.performTurn(turnInfo)
      }

    override fun revertTurn(){
        super.revertTurn()
        gameRuler.revertTurn()
    }
}