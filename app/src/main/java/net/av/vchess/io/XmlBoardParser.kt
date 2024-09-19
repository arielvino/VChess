package net.av.vchess.io

import net.av.vchess.game.data.Board
import net.av.vchess.game.data.Piece
import net.av.vchess.game.data.Tile
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XmlBoardParser : IBoardParser {
    override fun readBoard(rawData: String): Board {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()

        val document: Document = documentBuilder.parse(InputSource(StringReader(rawData)))

        val boardElement = document.documentElement
        val width = boardElement.getAttribute("x").toInt()
        val height = boardElement.getAttribute("y").toInt()

        val board = Board(width, height)

        val pieces = boardElement.getElementsByTagName("piece")
        for(i in 0..<pieces.length)
        {
            val pieceElement = pieces.item(i) as Element
            val className = pieceElement.getAttribute("type")
            val color:PlayerColor = PlayerColor.valueOf(pieceElement.getAttribute("color"))
            val steps = pieceElement.getAttribute("steps").toInt()
            val location = Vector2D(pieceElement.getAttribute("location").split(",")[0].toInt(), pieceElement.getAttribute("location").split(",")[1].toInt())

            val piece = Class.forName(className).constructors.first().newInstance(color, board, location, steps) as Piece
            piece.consistentMobility = Piece.Mobility.valueOf(pieceElement.getAttribute("mobility"))
            board.getTile(location).piece = piece
        }

        val tiles = boardElement.getElementsByTagName("tile")
        for(i in 0..<tiles.length)
        {
            val tileElement = tiles.item(i) as Element
            val location = Vector2D(tileElement.getAttribute("location").split(",")[0].toInt(), tileElement.getAttribute("location").split(",")[1].toInt())
            val tile = board.getTile(location)
            if(tileElement.hasAttribute("traversability")) {
                tile.consistentTraversability =
                    Tile.Traversability.valueOf(tileElement.getAttribute("traversability"))
            }
        }

        return board
    }


    override fun writeBoard(board: Board): String {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()

        val document: Document = documentBuilder.newDocument()

        val boardElement: Element = document.createElement("board")
        boardElement.setAttribute("x", board.width.toString())
        boardElement.setAttribute("y", board.height.toString())
        document.appendChild(boardElement)

        for (x in 0..<board.width) {
            for (y in 0..<board.height) {
                val tile = board.getTile(x, y)
                val tileElement: Element = document.createElement("tile")
                if (tile.consistentTraversability != Tile.Traversability.Normal) {
                    tileElement.setAttribute("traversability", tile.consistentTraversability.name)
                }
                if (tileElement.attributes.length > 0) {
                    tileElement.setAttribute(
                        "location",
                        tile.location.x.toString() + "," + tile.location.y.toString()
                    )
                    boardElement.appendChild(tileElement)
                }

                if (tile.piece != null) {
                    val piece = tile.piece
                    val pieceElement: Element = document.createElement("piece")
                    pieceElement.setAttribute("type", piece!!.javaClass.name)
                    pieceElement.setAttribute("color", piece.color.name)
                    pieceElement.setAttribute("steps", piece.stepsCounter.toString())
                    pieceElement.setAttribute(
                        "location",
                        piece.location.x.toString() + "," + piece.location.y.toString()
                    )
                    pieceElement.setAttribute("mobility", piece.consistentMobility.name)
                    boardElement.appendChild(pieceElement)
                }
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        val source = DOMSource(document)
        val result = StreamResult(StringWriter())
        transformer.transform(source, result)
        return result.writer.toString()
    }
}