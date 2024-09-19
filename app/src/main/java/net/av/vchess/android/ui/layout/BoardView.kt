package net.av.vchess.android.ui.layout

import android.content.Context
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.GridLayout
import net.av.vchess.android.BoardViewModel
import net.av.vchess.android.TileViewModel
import net.av.vchess.game.data.ActualGame
import kotlin.properties.Delegates

/**
 * TODO: document your custom view class.
 */
class BoardView(
    context: Context,
    private val game: ActualGame,
    val boardViewModel: BoardViewModel = BoardViewModel(game)
) : FrameLayout(context) {
    val tiles: ArrayList<ArrayList<TileView>> = arrayListOf()
    var tileSize by Delegates.notNull<Int>()

    init {
        val gridLayout = GridLayout(context)
        gridLayout.columnCount = game.board.width
        gridLayout.rowCount = game.board.height
        gridLayout.layoutDirection = LAYOUT_DIRECTION_LTR
        addView(gridLayout)

        // Wait for the parent view to be laid out before calculating the tile size
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple callbacks
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Calculate the tile size based on the parent width and the number of columns
                tileSize = width / game.board.width

                for (y in 0.until(game.board.height)) {
                    for (x in 0.until(game.board.width)) {
                        val tile = TileView(context)
                        val layoutParams = GridLayout.LayoutParams()
                        layoutParams.width = tileSize
                        layoutParams.height = tileSize
                        tile.layoutParams = layoutParams
                        gridLayout.addView(tile)

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
        })
    }
}