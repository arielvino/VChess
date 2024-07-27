package net.av.vchess.game.data

import net.av.vchess.game.data.turn.TurnInfo

interface IGameRuler {
    fun getLegalTurnsFrom(turns:ArrayList<TurnInfo>):ArrayList<TurnInfo>
    fun performTurn(turnDone:TurnInfo)
    fun revertTurn()
}