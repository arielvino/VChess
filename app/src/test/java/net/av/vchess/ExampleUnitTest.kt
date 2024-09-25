package net.av.vchess

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.av.vchess.reusables.PlayerColor
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
         println(Json.encodeToString(arrayOf(PlayerColor.Black, PlayerColor.White, PlayerColor.valueOf("null"), null)))
    }
}