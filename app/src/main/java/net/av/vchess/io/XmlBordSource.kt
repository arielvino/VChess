package net.av.vchess.io

import net.av.vchess.game.data.Board

class XmlBordSource(private val xmlString: String):IBoardSource {
    override fun renderBoard(): Board {
        return XmlBoardParser.readBoard(xmlString)
    }

}