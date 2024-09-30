package net.av.vchess.io

import net.av.vchess.android.App
import net.av.vchess.game.data.Board
import java.io.File

object BoardIO {
    private const val PATH = "/boards/"
    fun saveBoard(board: Board, name: String): Boolean {
        createBoardsDir()
        val file = File(App.appContext.filesDir.absolutePath + PATH + name)
        try {
            if (!file.exists()) {
                file.writeText(XmlBoardParser.writeBoard(board))
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun editBoard(board: Board, name: String): Boolean {
        val file = File(App.appContext.filesDir.absolutePath + PATH + name)
        file.delete()
        return saveBoard(board, name)
    }

    fun deleteBoard(name: String): Boolean {
        val file = File(App.appContext.filesDir.absolutePath + PATH + name)
        return file.delete()
    }

    private fun createBoardsDir() {
        val dir = File(App.appContext.filesDir.absolutePath + PATH)
        if (!dir.exists()) {
            dir.mkdir()
        }
    }
}