package net.av.vchess.io

import net.av.vchess.game.data.Board

interface IBoardParser {
    fun readBoard(rawData:String):Board
    fun writeBoard(board:Board):String
}