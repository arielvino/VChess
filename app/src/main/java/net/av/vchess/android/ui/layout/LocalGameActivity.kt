package net.av.vchess.android.ui.layout

import android.os.Bundle
import android.widget.FrameLayout
import net.av.vchess.R
import androidx.activity.ComponentActivity
import net.av.vchess.android.TwoPlayersBoardViewModel
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.Rulers.TestRuler
import net.av.vchess.game.data.TestBoardRenderer
import net.av.vchess.game.data.turn.MoveAction
import net.av.vchess.game.data.turn.TurnInfo
import net.av.vchess.reusables.PlayerColor
import net.av.vchess.reusables.Vector2D

class LocalGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val game = ActualGame(TestBoardRenderer(), PlayerColor.White, TestRuler(TestBoardRenderer(), PlayerColor.White))

        setContentView(R.layout.local_game_activity)
        findViewById<FrameLayout>(R.id.board_holder).addView(
            BoardView(
                this,
                game.board,
                TwoPlayersBoardViewModel(game)
            )
        )

        //test:
        val turn = TurnInfo(Vector2D(0, 0))
        turn.actions.add(
            MoveAction(
                Vector2D(0, 0),
                Vector2D(0, 4)
            )
        )
    }
}