package net.av.vchess.game.data

import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.io.IBoardSource
import net.av.vchess.reusables.PlayerColor

class ActualGame(private val listener:IListener, boardSource: IBoardSource, startingAtTurnOf: PlayerColor, val gameRuler: IGameRuler) : GameRepresentation(boardSource, startingAtTurnOf) {
      override fun performTurn(turnInfo: TurnInfo){
          super.performTurn(turnInfo)
          gameRuler.performTurn(turnInfo)
          val color = when(currentTurn){
              PlayerColor.White->PlayerColor.Black
              PlayerColor.Black->PlayerColor.White
          }
          listener.onTurnDone(color, turnInfo)
      }

    override fun revertTurn(){
        super.revertTurn()
        gameRuler.revertTurn()
    }

    interface IListener{
        fun onTurnDone(color: PlayerColor, turnInfo: TurnInfo)
        fun onWin(color: PlayerColor)
    }
}