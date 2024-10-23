package net.av.vchess.game.data

import net.av.vchess.game.data.Rulers.TestRuler
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.network.data.GameSettings
import net.av.vchess.reusables.PlayerColor

class ActualGame(private val listener:IListener, val settings: GameSettings) : GameRepresentation(settings.boardSource, settings.startingPlayerColor) {
    val ruler:IGameRuler

    init {
        ruler = when(settings.rulerName){
            "test" -> TestRuler(settings.boardSource, settings.startingPlayerColor)
            else -> TODO()
        }
    }

      override fun performTurn(turnInfo: TurnInfo){
          super.performTurn(turnInfo)
          ruler.performTurn(turnInfo)
          val color = when(currentTurn){
              PlayerColor.White->PlayerColor.Black
              PlayerColor.Black->PlayerColor.White
          }
          listener.onTurnDone(color, turnInfo)
      }

    override fun revertTurn(){
        super.revertTurn()
        ruler.revertTurn()
    }

    interface IListener{
        fun onTurnDone(color: PlayerColor, turnInfo: TurnInfo)
        fun onWin(color: PlayerColor)
    }
}