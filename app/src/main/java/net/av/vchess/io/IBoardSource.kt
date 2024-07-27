package net.av.vchess.io

import net.av.vchess.game.data.Board

interface IBoardSource {
    fun renderBoard():Board
}