package net.av.vchess

import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.io.XmlBoardParser
import org.junit.Test

class XmlBoardParserTest {
    @Test
    fun test(): Unit {
        println("Test start...")
        println(XmlBoardParser().writeBoard(TestBoardRenderer().renderBoard()))
        println("Test ended.")
    }

    @Test
    fun mergedTest(){
        val board1 = XmlBoardParser().writeBoard(TestBoardRenderer().renderBoard())
        val board2 = XmlBoardParser().writeBoard(XmlBoardParser().readBoard(board1))
        assert(board1.contentEquals(board2))
    }
}