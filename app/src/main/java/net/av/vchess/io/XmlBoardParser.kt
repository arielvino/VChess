package net.av.vchess.io

import net.av.vchess.game.data.Board
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class XmlBoardParser : IBoardParser {
    override fun readBoard(rawData: String): Board {
        TODO("Not yet implemented")
    }

    override fun writeBoard(board: Board): String {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()

        val document: Document = documentBuilder.newDocument()

        val boardElement: Element = document.createElement("board")
        boardElement.setAttribute("x", board.width.toString())
        boardElement.setAttribute("y", board.height.toString())
        document.appendChild(boardElement)

        for(x in 0..<board.width){
            for (y in 0..<board.height){
                val tile = board.getTile(x, y)
            }
        }

        TODO()
    }
}