package net.av.vchess.android.ui.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import net.av.vchess.R
import net.av.vchess.android.BoardViewModel
import net.av.vchess.android.TileViewModel
import net.av.vchess.game.data.ActualGame
import net.av.vchess.game.data.Board
import net.av.vchess.game.data.GameRepresentation

/**
 * TODO: document your custom view class.
 */
class BoardView(
    context: Context,
    private val game: ActualGame,
    val boardViewModel: BoardViewModel = BoardViewModel(game)
) : FrameLayout(context) {
    val tiles: ArrayList<ArrayList<TileView>> = arrayListOf()

    init {
        val tileSize = 130

        val linesOfTheBoard: LinearLayout = LinearLayout(context)
        linesOfTheBoard.orientation = LinearLayout.VERTICAL
        addView(linesOfTheBoard)

        for (y in 0..<game.board.height) {
            val line = LinearLayout(context)
            line.orientation = LinearLayout.HORIZONTAL
            line.layoutDirection = LAYOUT_DIRECTION_LTR
            linesOfTheBoard.addView(line)

            for (x in 0..<game.board.width) {
                val tile = TileView(context)
                tile.layoutParams = LinearLayout.LayoutParams(tileSize, tileSize)
                line.addView(tile)
                val model: TileViewModel =
                    boardViewModel.getTile(x, boardViewModel.board.height - 1 - y)
                model.listener = tile
                model.refresh()

                tile.setOnClickListener {
                    boardViewModel.getTile(x, boardViewModel.board.height - 1 - y).onClick()
                }
            }
        }
    }
}