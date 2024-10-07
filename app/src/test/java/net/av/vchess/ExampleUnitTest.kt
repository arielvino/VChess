package net.av.vchess

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.game.data.pieces.Queen
import net.av.vchess.game.data.pieces.Rook
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun serializationOfPiece() {
        println(Json.encodeToString(Rook(PlayerColor.Black, Vector2D(2,2))))
        println(Json.encodeToString(Queen(PlayerColor.White, Vector2D(4,6), 5)))
        println(Json.encodeToString(TestBoardRenderer().renderBoard()))
    }
}